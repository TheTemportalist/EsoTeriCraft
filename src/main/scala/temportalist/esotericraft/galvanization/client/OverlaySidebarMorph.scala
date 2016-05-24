package temportalist.esotericraft.galvanization.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.{GuiIngameMenu, ScaledResolution}
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, OpenGlHelper, RenderHelper}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.{ClientTickEvent, Phase, RenderTickEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.capability.{HelperGalvanize, IPlayerGalvanize}
import temportalist.origin.api.client.TessRenderer
import temportalist.origin.foundation.client.gui.IOverlay

import scala.collection.JavaConversions
import scala.util.control.Breaks._

/**
  *
  * Created by TheTemportalist on 5/22/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
object OverlaySidebarMorph extends IOverlay {

	var doShowSelector = false
	var timerSelector = 0
	var (selectorSelected, selectorSelectedPrev) = (0, 0)
	var (selectorSelectedHori, selectorSelectedPrevHori) = (0, 0)
	val scrollTime = 3
	val selectorShowTime = 10
	var (scrollTimer, scrollTimerHori) = (0, 0)
	var playerEvent_RenderingSelected = false

	private val modid = Galvanize.getModId
	private val rlSelected = new ResourceLocation(modid, "textures/gui/guiSelected.png")
	private val rlUnselected = new ResourceLocation(modid, "textures/gui/guiUnselected.png")
	private val rlUnselectedSide = new ResourceLocation(modid, "textures/gui/guiUnselectedSide.png")

	@SubscribeEvent
	def onRenderTick(event: RenderTickEvent): Unit = {
		val mc = Minecraft.getMinecraft
		if (mc.theWorld == null) return
		if (event.phase == Phase.START) return
		if (mc.gameSettings.hideGUI) return

		HelperGalvanize.get(mc.thePlayer) match {
			case galvanized: IPlayerGalvanize =>
				//this.tryRenderSidebarSelector(mc, galvanized, event.renderTickTime)
				this.tryRenderSidebarSelector2(mc, galvanized, event.renderTickTime)
			case _ =>
		}

	}

	def tryRenderSidebarSelector(mc: Minecraft, galvanized: IPlayerGalvanize,
			renderTick: Float): Unit = {
		if (!this.doShowSelector && this.timerSelector <= 0) return

		GlStateManager.pushMatrix()

		var progress = 0F
		if (!(this.doShowSelector && this.timerSelector == 0)) {
			progress = (11F - (this.timerSelector + (1F - renderTick))) / 11F
			if (this.doShowSelector) progress = 1F - progress
		}
		progress = Math.pow(progress, 2).toFloat

		GlStateManager.translate(-52F * progress, 0F, 0F)

		val reso = new ScaledResolution(mc)

		var gap = (reso.getScaledHeight - (42 * 5)) / 2

		val size = 42D
		val width1 = 0D

		GlStateManager.pushMatrix()

		var maxShowable = Math.ceil(reso.getScaledHeight / size).toInt / 2

		if ((this.selectorSelected == 0 && this.selectorSelectedPrev > 0) ||
				(this.selectorSelectedPrev == 0 && this.selectorSelected > 0)) {
			maxShowable = 150
		}

		var progressV = (this.scrollTime - (this.scrollTimer - renderTick)) / scrollTime.toFloat
		progressV = Math.pow(progressV, 2).toFloat
		if (progressV > 1F) {
			progressV = 1F
			this.selectorSelectedPrev = selectorSelected
		}

		var progressH = (this.scrollTime - (this.scrollTimerHori - renderTick)) / scrollTime.toFloat
		progressH = Math.pow(progressH, 2).toFloat
		if (progressH > 1F) {
			progressH = 1F
			this.selectorSelectedPrevHori = selectorSelectedHori
		}

		GlStateManager.translate(
			0F,
			((this.selectorSelected - this.selectorSelectedPrev) * 42F) * (1F - progressV),
			0F
		)

		GlStateManager.disableDepth()
		GlStateManager.depthMask(false)
		GlStateManager.color(1F, 1F, 1F, 1F)
		GlStateManager.disableAlpha()

		GlStateManager.enableBlend()
		GlStateManager.blendFunc(770, 771)

		var i = 0

		val states = JavaConversions.asScalaBuffer(galvanized.getModelEntities)
		breakable {
			for (state <- states) {

				if (!(i > this.selectorSelected + maxShowable ||
						i < this.selectorSelected - maxShowable)) {

					val height1 = gap + size * (i - this.selectorSelected)

					// skipped https://github.com/iChun/Morph/blob/master/src/main/java/morph/client/core/TickHandlerClient.java#L213-L219
					// skipped https://github.com/iChun/Morph/blob/master/src/main/java/morph/client/core/TickHandlerClient.java#L225-L272

					if (i == this.selectorSelected) {



					}
					else {

						GlStateManager.pushMatrix()

						mc.getTextureManager.bindTexture(this.rlUnselected)
						val buf = TessRenderer.getBuffer
						TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX)
						buf.pos(width1,         height1 + size, -90D).tex(0, 1).endVertex()
						buf.pos(width1 + size,  height1 + size, -90D).tex(1, 1).endVertex()
						buf.pos(width1 + size,  height1,        -90D).tex(1, 0).endVertex()
						buf.pos(width1,         height1,        -90D).tex(0, 0).endVertex()
						TessRenderer.draw()

						GlStateManager.popMatrix()

					}

				}

				i += 1

			}
		}

		GlStateManager.disableBlend()

		var height1 = gap

		GlStateManager.depthMask(true)
		GlStateManager.enableDepth()
		GlStateManager.enableAlpha()

		gap += 36

		i = 0

		breakable {
			for (state <- states) {

				if (!(i > this.selectorSelected + maxShowable ||
						i < this.selectorSelected - maxShowable)) {

					// skipped https://github.com/iChun/Morph/blob/master/src/main/java/morph/client/core/TickHandlerClient.java#L307-L355

					val instance = state.getInstance(mc.theWorld)
					val entSize = Math.max(instance.width, instance.height)
					var prog =
						if (this.selectorSelected == i) {
							if (!this.doShowSelector) this.scrollTimer - renderTick
							else (3F - this.scrollTimer + renderTick) / 3F
						} else 0F
					prog = MathHelper.clamp_float(prog, 0F, 1F)
					val scaleMag = 2.5F / entSize
					this.drawEntityOnScreen(instance, 20, height1,
						if (entSize > 2.5F) 16F * scaleMag else 16F,
						2, 2, renderTick, this.selectorSelected == i, text = true
					)

				}

				GlStateManager.translate(0F, 0F, 20F)

				i += 1

			}
		}

		GlStateManager.popMatrix()

		if (this.doShowSelector) {
			GlStateManager.enableBlend()
			GlStateManager.blendFunc(770, 771)

			gap -= 36

			height1 = gap

			mc.getTextureManager.bindTexture(this.rlSelected)
			val buf = TessRenderer.getBuffer
			TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX)
			buf.pos(width1,         height1 + size, -90D).tex(0, 1).endVertex()
			buf.pos(width1 + size,  height1 + size, -90D).tex(1, 1).endVertex()
			buf.pos(width1 + size,  height1,        -90D).tex(1, 0).endVertex()
			buf.pos(width1,         height1,        -90D).tex(0, 0).endVertex()
			TessRenderer.draw()

			GlStateManager.disableBlend()

		}

		GlStateManager.popMatrix()
	}

	def tryRenderSidebarSelector2(mc: Minecraft, galvanized: IPlayerGalvanize,
			renderTick: Float): Unit = {
		if (!this.doShowSelector && this.timerSelector <= 0) return

		GlStateManager.pushMatrix()

		var progress = 0F
		if (!(this.doShowSelector && this.timerSelector == 0)) {
			progress = (11F - (this.timerSelector + (1F - renderTick))) / 11F
			if (this.doShowSelector) progress = 1F - progress
		}
		progress = Math.pow(progress, 2).toFloat

		GlStateManager.translate(-52F * progress, 0F, 0F)

		val reso = new ScaledResolution(mc)

		val gap = (reso.getScaledHeight - (42 * 5)) / 2

		val size = 42
		val width1 = 0D

		GlStateManager.pushMatrix()

		var maxShowable = Math.ceil(reso.getScaledHeight.toDouble / size.toDouble) / 2
		maxShowable = maxShowable.toInt

		if ((this.selectorSelected == 0 && this.selectorSelectedPrev > 0) ||
				(this.selectorSelectedPrev == 0 && this.selectorSelected > 0)) {
			maxShowable = 150
		}

		var progressV = (this.scrollTime - (this.scrollTimer - renderTick)) / scrollTime.toFloat
		progressV = Math.pow(progressV, 2).toFloat
		if (progressV > 1F) {
			progressV = 1F
			this.selectorSelectedPrev = selectorSelected
		}

		var progressH = (this.scrollTime - (this.scrollTimerHori - renderTick)) / scrollTime.toFloat
		progressH = Math.pow(progressH, 2).toFloat
		if (progressH > 1F) {
			progressH = 1F
			this.selectorSelectedPrevHori = selectorSelectedHori
		}

		GlStateManager.translate(
			0F,
			((this.selectorSelected - this.selectorSelectedPrev) * 42F) * (1F - progressV),
			0F
		)

		GlStateManager.disableDepth()
		GlStateManager.depthMask(false)
		GlStateManager.color(1F, 1F, 1F, 1F)
		GlStateManager.disableAlpha()

		GlStateManager.enableBlend()
		GlStateManager.blendFunc(770, 771)

		val states = JavaConversions.asScalaBuffer(galvanized.getModelEntities)

		for (i <- 0 until states.length + 1) {
			if (!(i > this.selectorSelected + maxShowable ||
					i < this.selectorSelected - maxShowable)) {
				val height1 = gap + size * (i - this.selectorSelected)

				// Start Draw Bkgd
				GlStateManager.pushMatrix()
				if (i == this.selectorSelected) mc.getTextureManager.bindTexture(this.rlSelected)
				else mc.getTextureManager.bindTexture(this.rlUnselected)
				this.drawSidebarEntryBackground(width1, height1, size)
				GlStateManager.popMatrix()
				// End Draw Bkgd

			}
		}

		/*
		for (i <- states.indices) {
			val state = states(i)

			if (!(i > this.selectorSelected + maxShowable ||
					i < this.selectorSelected - maxShowable)) {

				val height1 = gap + size * (i - this.selectorSelected + 1) // +1 for none state

				// Start Draw Bkgd
				GlStateManager.pushMatrix()

				if (i + 1 == this.selectorSelected)
					mc.getTextureManager.bindTexture(this.rlSelected)
				else mc.getTextureManager.bindTexture(this.rlUnselected)

				this.drawSidebarEntryBackground(width1, height1, size)
				GlStateManager.popMatrix()
				// End Draw Bkgd

			}

		}
		*/

		GlStateManager.disableBlend()

		GlStateManager.depthMask(true)
		GlStateManager.enableDepth()
		GlStateManager.enableAlpha()

		// render entities

		for (i <- 0 until states.length + 1) {
			if (!(i > this.selectorSelected + maxShowable ||
					i < this.selectorSelected - maxShowable)) {
				GlStateManager.pushMatrix()
				val instance = if (i == 0) mc.thePlayer else states(i - 1).getInstance(mc.theWorld)
				val entSize = Math.max(instance.width, instance.height)
				val scaleMag = 2.5F / entSize

				if (i == 0) this.playerEvent_RenderingSelected = true

				this.drawEntityOnScreen(instance, 20, gap + (size * (i - this.selectorSelected + 1)),
					if (entSize > 2.5F) 16F * scaleMag else 16F,
					2, 2, renderTick, this.selectorSelected == i, text = true
				)

				if (i == 0) this.playerEvent_RenderingSelected = false

				GlStateManager.popMatrix()
			}
		}

		/*
		if (!(0 > this.selectorSelected + maxShowable ||
				0 < this.selectorSelected - maxShowable)) {
			GlStateManager.pushMatrix()
			val instance = mc.thePlayer
			val entSize = Math.max(instance.width, instance.height)
			val scaleMag = 2.5F / entSize
			this.drawEntityOnScreen(instance, 20, gap + (size * (0 - this.selectorSelected + 1)),
				if (entSize > 2.5F) 16F * scaleMag else 16F,
				2, 2, renderTick, this.selectorSelected == 0, text = true
			)
			GlStateManager.popMatrix()
		}

		for (i <- states.indices) {
			val state = states(i)

			if (!(i + 1 > this.selectorSelected + maxShowable ||
					i + 1 < this.selectorSelected - maxShowable)) {

				// Start Draw Entity
				GlStateManager.pushMatrix()
				val instance = state.getInstance(mc.theWorld)
				val entSize = Math.max(instance.width, instance.height)
				var prog =
					if (this.selectorSelected == i + 1) {
						if (!this.doShowSelector) this.scrollTimer - renderTick
						else (3F - this.scrollTimer + renderTick) / 3F
					} else 0F
				prog = MathHelper.clamp_float(prog, 0F, 1F)
				val scaleMag = 2.5F / entSize
				this.drawEntityOnScreen(instance, 20, gap + (size * (i - this.selectorSelected + 2)),
					if (entSize > 2.5F) 16F * scaleMag else 16F,
					2, 2, renderTick, this.selectorSelected == i, text = true
				)
				GlStateManager.popMatrix()
				// End Draw Entity

			}
		}
		*/

		GlStateManager.popMatrix()

		GlStateManager.popMatrix()
	}

	def drawSidebarEntryBackground(x: Double, y: Double, size: Double): Unit = {
		val buf = TessRenderer.getBuffer
		TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX)
		buf.pos(x,         y + size, -90D).tex(0, 1).endVertex()
		buf.pos(x + size,  y + size, -90D).tex(1, 1).endVertex()
		buf.pos(x + size,  y,        -90D).tex(1, 0).endVertex()
		buf.pos(x,         y,        -90D).tex(0, 0).endVertex()
		TessRenderer.draw()
	}

	@SubscribeEvent
	def onTickWorld(event: ClientTickEvent): Unit = {
		if (event.phase != Phase.END) return
		val mc = Minecraft.getMinecraft
		val world = mc.theWorld
		if (world == null) return

		// https://github.com/iChun/Morph/blob/master/src/main/java/morph/client/core/TickHandlerClient.java#L615

		if (mc.currentScreen != null) {
			if (this.doShowSelector) {
				if (mc.currentScreen.isInstanceOf[GuiIngameMenu]) {
					mc.displayGuiScreen(null)
				}
				this.doShowSelector = false
				this.timerSelector = this.selectorShowTime - this.timerSelector
				this.scrollTimerHori = this.scrollTime
			}
		}

		if (this.timerSelector > 0) {

			this.timerSelector -= 1

			if (this.timerSelector == 0 && !this.doShowSelector) {
				this.selectorSelected = 0
				// skipped https://github.com/iChun/Morph/blob/master/src/main/java/morph/client/core/TickHandlerClient.java#L640-L672
			}

		}

		if (this.scrollTimer > 0)
			this.scrollTimer -= 1
		if (this.scrollTimerHori > 0)
			this.scrollTimerHori -= 1

	}

	def drawEntityOnScreen(ent: EntityLivingBase,
			posX: Int, posY: Int, scale: Float,
			par4: Float, par5: Float, renderTick: Float, selected: Boolean, text: Boolean
	): Unit = {
		var forceRender = true
		if (ent == null) {
			forceRender = false
			return
		}

		val mc = Minecraft.getMinecraft
		var hideGui = mc.gameSettings.hideGUI
		mc.gameSettings.hideGUI = true

		GlStateManager.enableColorMaterial()

		GlStateManager.pushMatrix()

		GlStateManager.disableAlpha()
		GlStateManager.translate(posX, posY, 50F)
		GlStateManager.scale(-scale, scale, scale)
		GlStateManager.rotate(180, 0, 0, 1F)

		var f2 = ent.renderYawOffset
		var f3 = ent.rotationYaw
		var f4 = ent.rotationPitch
		var f5 = ent.rotationYawHead

		GlStateManager.rotate(135F, 0, 1, 0)
		RenderHelper.enableStandardItemLighting()
		GlStateManager.rotate(-135F, 0, 1, 0)
		GlStateManager.rotate(
			-Math.atan(par5 / 40F).toFloat * 20,
			1, 0, 0
		)
		GlStateManager.rotate(15F, 1, 0, 0)
		GlStateManager.rotate(25F, 0, 1, 0)

		ent.renderYawOffset = Math.atan(par4 / 40F).toFloat * 20F
		ent.rotationYaw = Math.atan(par4 / 40F).toFloat * 40F
		ent.rotationPitch = -Math.atan(par5 / 40F).toFloat * 20F
		ent.rotationYawHead = ent.renderYawOffset

		GlStateManager.translate(0, ent.getYOffset, 0)
		GlStateManager.color(1f, 1f, 1f, 1f)

		if (ent.isInstanceOf[EntityDragon])
			GlStateManager.rotate(180F, 0, 1, 0)

		var viewY = mc.getRenderManager.playerViewY
		mc.getRenderManager.playerViewY = 180F
		mc.getRenderManager.doRenderEntity(ent, 0, 0, 0, 0, 0, false)

		if (ent.isInstanceOf[EntityDragon])
			GlStateManager.rotate(180F, 0, -1, 0)

		GlStateManager.translate(0, -0.22F, 0)
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 255F * 0.8F, 255F * 0.8F)
		// skipped https://github.com/iChun/Morph/blob/master/src/main/java/morph/client/core/TickHandlerClient.java#L1017

		mc.getRenderManager.playerViewY = viewY
		ent.renderYawOffset = f2
		ent.rotationYaw = f3
		ent.rotationPitch = f4
		ent.rotationYawHead = f5

		GlStateManager.popMatrix()

		RenderHelper.disableStandardItemLighting()

		// skipped https://github.com/iChun/Morph/blob/master/src/main/java/morph/client/core/TickHandlerClient.java#L1029-L1286

		GlStateManager.enableAlpha()
		GlStateManager.disableRescaleNormal()
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit)
		GlStateManager.disableTexture2D()
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit)

		mc.gameSettings.hideGUI = hideGui

		forceRender = false
	}

}
