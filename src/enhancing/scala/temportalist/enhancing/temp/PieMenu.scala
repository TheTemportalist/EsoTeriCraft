package temportalist.enhancing.temp

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, RenderHelper}
import net.minecraft.util.{MathHelper, ResourceLocation}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.{Display, GL11}
import temportalist.origin.api.client.utility.{TessRenderer, Rendering}

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

/**
  * Created by TheTemportalist on 1/8/2016.
  */
@SideOnly(Side.CLIENT)
abstract class PieMenu[T <: IPieObject] {

	def getPieObjectList: Array[T]

	def selectObj(index: Int, obj: T)

	def getCenterBackground: ResourceLocation

	def getBorderBackground: ResourceLocation

	def shouldContinueToDisplay: Boolean = true

	private var guiOpenTime = 0L
	private var prevTickTime = 0L
	private var shouldDisplayGui = false
	private var wasActiveLastState = false
	private var guiScale = 0F

	private val list_hover = ListBuffer[Boolean]()
	private val list_scale = ListBuffer[Float]()
	private val list_renderObjs = ListBuffer[T]()

	final def open(): Unit = {
		if (Rendering.mc.currentScreen == null) {
			this.shouldDisplayGui = true
			this.guiOpenTime = System.currentTimeMillis()
		}
	}

	def close(): Unit = {
		this.shouldDisplayGui = false
	}

	final def onTick(scaledRes: ScaledResolution, partialTicks: Float): Unit = {
		this.handleRadial(Rendering.mc, guiOpenTime,
			System.nanoTime() / 1000000L, scaledRes, partialTicks)
	}

	private final def selectIndex(i: Int): Unit = {
		this.close()
		this.selectObj(i, this.list_renderObjs(i))
	}

	private final def handleRadial(mc: Minecraft, openTime: Long,
			time: Long, scaledRes: ScaledResolution, partialTicks: Float): Unit = {

		if (!this.shouldContinueToDisplay) this.close()

		// Should show, or if gui is scaling down
		if (this.shouldDisplayGui || this.guiScale > 0F) {

			if (this.shouldDisplayGui) {
				if (mc.currentScreen != null) {
					this.close()
					mc.setIngameFocus()
					mc.setIngameNotInFocus()
					return
				}
				if (this.guiScale == 0F) {

					this.list_hover.clear()
					this.list_scale.clear()
					this.list_renderObjs.clear()
					this.list_renderObjs ++= this.getPieObjectList
					this.list_hover ++= Array.fill(this.list_renderObjs.size)(false)
					this.list_scale ++= Array.fill(this.list_renderObjs.size)(1F)

					val valid = true
					if (valid) {
						mc.inGameHasFocus = false
						mc.mouseHelper.ungrabMouseCursor()
					}
				}
			}
			else if (mc.currentScreen == null) {
				if (this.wasActiveLastState) {
					if (Display.isActive) {
						if (!mc.inGameHasFocus) {
							mc.inGameHasFocus = true
							mc.mouseHelper.grabMouseCursor()
						}
					}
					this.wasActiveLastState = false
				}
			}

			if (this.shouldDisplayGui && this.list_hover.nonEmpty)
				this.renderRadial(mc, scaledRes.getScaledWidth_double,
					scaledRes.getScaledHeight_double, partialTicks)

			// on the next ticking section
			if (time > this.prevTickTime) {

				// iterate over all radial indices
				for (i <- this.list_hover.indices) {
					// if hovering over a specfic index
					if (this.list_hover(i)) {
						// player has exited the gui
						if (!this.shouldDisplayGui) this.selectIndex(i)

						// if small scale
						if (this.list_scale(i) < 1.3F) {
							// scale up the index
							this.list_scale(i) += 0.025F
						}
					}
					// if large scale
					else if (this.list_scale(i) > 1F) {
						// scale down the index
						this.list_scale(i) -= 0.025F
					}
				}

				// Scaling the radial on and after key press
				if (!this.shouldDisplayGui) {
					this.guiScale -= 0.05F
				}
				else if (this.guiScale < 1F) this.guiScale += 0.05F
				if (this.guiScale > 1F) this.guiScale = 1F
				if (this.guiScale < 0F) this.guiScale = 0F

				// updating prev time and active states
				this.prevTickTime = time + 5L
				this.wasActiveLastState = this.shouldDisplayGui
			}

		}

	}

	private final def renderRadial(mc: Minecraft, sw: Double, sh: Double,
			partialTicks: Float): Unit = {
		// todo move this
		val largeScale = 2F

		val x = (Mouse.getEventX * sw / mc.displayWidth).toInt
		val y = (Mouse.getEventY * sh / mc.displayHeight).toInt
		val button = Mouse.getEventButton

		Rendering.push_gl()
		GlStateManager.clear(256)
		//GlStateManager.matrixMode(589)
		GlStateManager.loadIdentity()
		GlStateManager.ortho(0.0D, sw, sh, 0.0D, 1000.0D, 3000.0D)
		GlStateManager.matrixMode(5888)
		GlStateManager.loadIdentity()
		Rendering.translate_gl(0.0F, 0.0F, -2000.0F)
		GL11.glDisable(2929)
		GlStateManager.depthMask(false)

		Rendering.push_gl()
		Rendering.translate_gl(sw / 2.0D, sh / 2.0D, 0.0D)

		val width = 16F + this.list_hover.size * 2.5F

		Rendering.bindResource(this.getCenterBackground)
		Rendering.push_gl()
		GlStateManager
				.rotate(partialTicks + mc.thePlayer.ticksExisted % 720 / 2.0F, 0.0F, 0.0F, 1.0F)
		GlStateManager.alphaFunc(516, 0.003921569F)
		GL11.glEnable(3042)
		GlStateManager.blendFunc(770, 771)
		this.renderQuadCenteredFromTexture(width * 2.75F * this.guiScale * largeScale, 0.5F, 0.5F, 0.5F, 200,
			771, 0.5F)
		GL11.glDisable(3042)
		GlStateManager.alphaFunc(516, 0.1F)
		Rendering.pop_gl()

		Rendering.bindResource(this.getBorderBackground)
		Rendering.push_gl()
		GlStateManager
				.rotate(-(partialTicks + mc.thePlayer.ticksExisted % 720 / 2.0F), 0.0F, 0.0F, 1.0F)
		GlStateManager.alphaFunc(516, 0.003921569F)
		GL11.glEnable(3042)
		GlStateManager.blendFunc(770, 771)
		this.renderQuadCenteredFromTexture(width * 2.55F * this.guiScale * largeScale, 0.5F, 0.5F, 0.5F, 200,
			771, 0.5F)
		GL11.glDisable(3042)
		GlStateManager.alphaFunc(516, 0.1F)
		Rendering.pop_gl()

		var hovering_index = -1
		GlStateManager.scale(this.guiScale, this.guiScale, this.guiScale)
		var currentRot = -90F * this.guiScale
		val pieSlice = 360F / this.list_hover.size
		breakable(for (index <- this.list_hover.indices) {
			val xx = MathHelper.cos(currentRot / 180F * 3.1415927F) * width * largeScale
			val yy = MathHelper.sin(currentRot / 180F * 3.1415927F) * width * largeScale
			currentRot += pieSlice

			Rendering.push_gl()
			Rendering.translate_gl(xx, yy, 100D)
			val index_scale = this.list_scale(index)
			GlStateManager.scale(index_scale, index_scale, index_scale)
			GL11.glEnable(32826)
			RenderHelper.enableGUIStandardItemLighting()
			this.list_renderObjs(index).draw(mc, 0, 0)
			RenderHelper.disableStandardItemLighting()
			GL11.glDisable(32826)
			Rendering.pop_gl()

			if (this.shouldDisplayGui) {
				val mx = (x - sw / 2D - xx).toInt
				val my = (y - sh / 2D - yy).toInt
				if ((mx >= -10) && (mx <= 10) && (my >= -10) && (my <= 10)) {
					hovering_index = index
					this.list_hover(index) = true

					if (button == 0) {
						this.selectIndex(index)
						break()
					}
				}
				else {
					this.list_hover(index) = false
				}
			}
		})
		Rendering.pop_gl()
		if (hovering_index >= 0) {
			// todo draw tooltip
		}
		GlStateManager.depthMask(true)
		GL11.glEnable(2929)
		GL11.glDisable(3042)
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)

		Rendering.pop_gl()
	}

	final def renderQuadCenteredFromTexture(scale: Float,
			red: Float, green: Float, blue: Float,
			brightness: Int, blend: Int, opacity: Float): Unit = {
		GlStateManager.scale(scale, scale, scale)
		GL11.glEnable(3042)
		GlStateManager.blendFunc(770, blend)
		GlStateManager.color(1f, 1f, 1f, opacity)

		TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX_LMAP_COLOR)
		val wr = TessRenderer.getRenderer
		val bComp = (brightness >> 16 & 65535, brightness & 65535) // brightness composite

		val increment = 0.5D
		wr.pos(-increment, +increment, 0D).tex(0D, 1D).lightmap(
			bComp._1, bComp._2).color(red, green, blue, opacity).endVertex()
		wr.pos(+increment, +increment, 0.0D).tex(1.0D, 1.0D).lightmap(
			bComp._1, bComp._2).color(red, green, blue, opacity).endVertex()
		wr.pos(+increment, -increment, 0.0D).tex(1.0D, 0.0D).lightmap(
			bComp._1, bComp._2).color(red, green, blue, opacity).endVertex()
		wr.pos(-increment, -increment, 0.0D).tex(0.0D, 0.0D).lightmap(
			bComp._1, bComp._2).color(red, green, blue, opacity).endVertex()
		TessRenderer.draw()

		GL11.glDisable(3042)
	}

}
