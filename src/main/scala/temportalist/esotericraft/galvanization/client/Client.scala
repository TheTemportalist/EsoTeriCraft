package temportalist.esotericraft.galvanization.client

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.client.settings.{KeyConflictContext, KeyModifier}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.{ClientTickEvent, Phase, RenderTickEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.input.Keyboard
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.capability.{HelperGalvanize, IPlayerGalvanize}
import temportalist.esotericraft.galvanization.common.entity.emulator.EntityState
import temportalist.esotericraft.galvanization.common.network.PacketSetModel
import temportalist.origin.api.client.EnumKeyCategory
import temportalist.origin.foundation.client.modTraits.IHasKeys
import temportalist.origin.foundation.client.{IKeyBinder, IModClient}
import temportalist.origin.foundation.common.IModPlugin

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
object Client extends IModClient with IHasKeys {

	override def getMod: IModPlugin = Galvanize

	@SideOnly(Side.CLIENT)
	override def getKeyBinder: IKeyBinder = ModKeys

	/**
	  * This needs to be called in [[temportalist.origin.foundation.common.IProxy.preInit]]
	  */
	override def preInit(): Unit = {
		super.preInit()

		ModKeys.register()
		MinecraftForge.EVENT_BUS.register(ModKeys)

		// TODO not registered because must use ticker this.registerOverlay(OverlaySidebarMorph)
		MinecraftForge.EVENT_BUS.register(OverlaySidebarMorph)

		MinecraftForge.EVENT_BUS.register(ClientTask)

	}

	object ModKeys extends IKeyBinder {

		var sidebarUp: KeyBinding = _
		var sidebarDown: KeyBinding = _
		var sidebarLeft: KeyBinding = _
		var sidebarRight: KeyBinding = _

		override def register(): Unit = {
			val prefix = "morphSelector"
			val cate = EnumKeyCategory.GAMEPLAY.getName
			val cxt = KeyConflictContext.UNIVERSAL
			val km = KeyModifier.SHIFT

			this.sidebarUp = new KeyBinding(prefix + "Up", Keyboard.KEY_LBRACKET, cate)
			this.registerKeyBinding(this.sidebarUp)
			this.sidebarDown = new KeyBinding(prefix + "Down", Keyboard.KEY_RBRACKET, cate)
			this.registerKeyBinding(this.sidebarDown)
			this.sidebarLeft = new KeyBinding(prefix + "Left", cxt, km, Keyboard.KEY_LBRACKET, cate)
			this.registerKeyBinding(this.sidebarLeft)
			this.sidebarRight = new KeyBinding(prefix + "Right", cxt, km, Keyboard.KEY_RBRACKET, cate)
			this.registerKeyBinding(this.sidebarRight)

		}

		override def onKeyPressed(keyBinding: KeyBinding): Unit = {
			val desc = keyBinding.getKeyDescription
			val isUp = desc == this.sidebarUp.getKeyDescription // -Y
			val isDown = desc == this.sidebarDown.getKeyDescription // +Y
			val isLeft = desc == this.sidebarLeft.getKeyDescription // -X
			val isRight = desc == this.sidebarRight.getKeyDescription // +X

			val overlay = OverlaySidebarMorph
			if (isUp || isDown) {

				val mc = Minecraft.getMinecraft

				// skipped https://github.com/iChun/Morph/blob/f741101ce718323f0945c2cd7dc3e21acf8c314a/src/main/java/morph/common/core/EventHandler.java#L708
				if (!overlay.doShowSelector && mc.currentScreen == null) {
					overlay.doShowSelector = true
					overlay.timerSelector = overlay.selectorShowTime - overlay.timerSelector
					overlay.scrollTimerHori = overlay.scrollTime
					overlay.selectorSelected = 0
					overlay.selectorSelectedHori = 0

					// skipped https://github.com/iChun/Morph/blob/f741101ce718323f0945c2cd7dc3e21acf8c314a/src/main/java/morph/common/core/EventHandler.java#L718-L750
				}
				else {
					overlay.selectorSelectedHori = 0
					overlay.selectorSelectedPrev = overlay.selectorSelected
					overlay.scrollTimerHori = overlay.scrollTime
					overlay.scrollTimer = overlay.scrollTime

					HelperGalvanize.get(mc.thePlayer) match {
						case galvanized: IPlayerGalvanize =>
							if (isUp) {
								overlay.selectorSelected -= 1
								if (overlay.selectorSelected < 0) {
									overlay.selectorSelected = galvanized.getModelEntities.size() - 1 + 1 // +1 for none state
								}
							}
							else {
								overlay.selectorSelected += 1
								if (overlay.selectorSelected >= galvanized.getModelEntities.size() + 1) // + 1 for none state
									overlay.selectorSelected = 0
							}
						case _ =>
					}



				}

			}
			// skipped https://github.com/iChun/Morph/blob/f741101ce718323f0945c2cd7dc3e21acf8c314a/src/main/java/morph/common/core/EventHandler.java#L776-L836
			// skipped https://github.com/iChun/Morph/blob/f741101ce718323f0945c2cd7dc3e21acf8c314a/src/main/java/morph/common/core/EventHandler.java#L837-L1037

		}

		@SubscribeEvent
		def onKeyPress(event: KeyInputEvent): Unit = {
			if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
				if (OverlaySidebarMorph.doShowSelector) {
					OverlaySidebarMorph.doShowSelector = false

					val selectedIndex = OverlaySidebarMorph.selectorSelected
					new PacketSetModel(selectedIndex, 0).sendToServer(Galvanize)

				}
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_BACK) ||
					Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
				if (OverlaySidebarMorph.doShowSelector) {
					val selectedIndex = OverlaySidebarMorph.selectorSelected
					new PacketSetModel(selectedIndex, 1).sendToServer(Galvanize)
				}
			}
		}

	}

	@SubscribeEvent
	def tick(event: ClientTickEvent): Unit = {
		if (event.phase == Phase.END) {
			val mc = Minecraft.getMinecraft
			val world = mc.theWorld
			if (world != null) {

				if (!mc.isGamePaused) {
					mc.thePlayer match {
						case player: EntityPlayerSP =>
							HelperGalvanize.get(player).onTickClient()
						case _ =>
					}
				}

			}
		}
	}

	private var hasLoadedAGui = false

	@SubscribeEvent
	def initGuiPost(event: InitGuiEvent.Post): Unit = {
		if (!this.hasLoadedAGui) {
			this.hasLoadedAGui = true

			ModelHandler.loadEntityModels()

		}
	}

	@SubscribeEvent
	def renderPlayerPre(event: RenderPlayerEvent.Pre): Unit = {
		if (OverlaySidebarMorph.playerEvent_RenderingSelected) return
		val player = event.getEntityPlayer
		val galvanized = HelperGalvanize.get(player)

		galvanized.getEntityState match {
			case entityState: EntityState =>
				event.setCanceled(true)

				val instance: EntityLivingBase = entityState.getInstance(player.getEntityWorld)
				val entityModel: EntityModel[_, _] =
					galvanized.getEntityModelInstance(player.getEntityWorld)

				val yaw = this.interpolateRotation(player.prevRotationYaw, player.rotationYaw, event.getPartialRenderTick)

				if (entityModel != null) {
					entityModel.forceRender(instance, event.getX, event.getY, event.getZ, yaw, event.getPartialRenderTick)
				}

			case _ =>
		}

	}

	@SubscribeEvent
	def renderTick(event: RenderTickEvent): Unit = {
		val mc = Minecraft.getMinecraft
		if (mc.theWorld != null) {
			if (event.phase != Phase.START) {
				this.renderAbilitiesPost(mc.thePlayer)
			}
		}
	}

	def renderAbilitiesPost(player: EntityPlayerSP): Unit = {
		HelperGalvanize.get(player) match {
			case galvanized: IPlayerGalvanize =>
				for (ability <- JavaConversions.iterableAsScalaIterable(galvanized.getEntityAbilities)) {
					ability.renderPost(player)
				}
			case _ =>
		}
	}

	def interpolateRotation(prev: Float, next: Float, partialTicks: Float): Float = {
		var f3 = next - prev
		while (f3 < -180)
			f3 += 360F
		while (f3 >= 180F) f3 -= 360F
		prev + partialTicks * f3
	}

}
