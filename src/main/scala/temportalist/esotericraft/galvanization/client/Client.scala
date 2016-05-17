package temportalist.esotericraft.galvanization.client

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.{ClientTickEvent, Phase}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.capability.HelperGalvanize
import temportalist.esotericraft.galvanization.common.entity.emulator.EntityState
import temportalist.origin.foundation.client.IModClient
import temportalist.origin.foundation.common.IModPlugin

/**
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
object Client extends IModClient {

	override def getMod: IModPlugin = Galvanize

	/**
	  * This needs to be called in [[temportalist.origin.foundation.common.IProxy.preInit]]
	  */
	override def preInit(): Unit = {
		super.preInit()

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

	def interpolateRotation(prev: Float, next: Float, partialTicks: Float): Float = {
		var f3 = next - prev
		while (f3 < -180)
			f3 += 360F
		while (f3 >= 180F) f3 -= 360F
		prev + partialTicks * f3
	}

}
