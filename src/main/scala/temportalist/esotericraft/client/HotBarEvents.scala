package temportalist.esotericraft.client

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.client.event.{MouseEvent, RenderGameOverlayEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11
import temportalist.esotericraft.api.ApiEsotericraft
import temportalist.esotericraft.common.EsoTeriCraft
import temportalist.esotericraft.common.network.PacketEsoTeriCraft_Server
import temportalist.origin.api.client.utility.{TessRenderer, Rendering}
import temportalist.origin.api.common.lib.V3O

/**
  * Created by TheTemportalist on 1/12/2016.
  */
@SideOnly(Side.CLIENT)
object HotBarEvents {

	@SubscribeEvent
	def mouseEvent(event: MouseEvent): Unit = {
		if (event.dwheel == 0) return
		val player = Rendering.mc.thePlayer
		if (player == null) return
		if (ApiEsotericraft.Spells.shouldSwitchSpell(player)) {
			PacketEsoTeriCraft_Server.switchSpell(event.dwheel < 0)
			event.setCanceled(true)
		}
	}

	@SubscribeEvent
	def overlay(event: RenderGameOverlayEvent.Post): Unit = {
		if (event.`type` != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return
		val player = Rendering.mc.thePlayer
		if (!ApiEsotericraft.Spells.isValidForSpellCasting(player.getCurrentEquippedItem)) return
		val esoteric = EsoTeriCraft.getEsotericPlayer(player)

		val w = event.resolution.getScaledWidth_double
		val h = event.resolution.getScaledHeight_double
		val centerX = w / 2
		val centerY = h / 2
		val center = new V3O(centerX, centerY)

		val hot_bar = esoteric.getHotBar
		val currentSlot = esoteric.getCurrent
		val halfBarSize = new V3O(82, 22)
		val slotSizeVec = new V3O(20, 22)
		val imgSize = new V3O(184, 22)

		// backgrounds
		if (player.isSneaking) {
			Rendering.push_gl()
			Rendering.bindResource(EsoTeriCraft.getResource("overlaySlots"))

			// left slot background
			this.renderTexture(center - (halfBarSize + slotSizeVec / 2).suppressedYAxis(),
				textureSize = halfBarSize,
				imageSize = imgSize,
				renderSize = halfBarSize)

			// right slot background
			this.renderTexture(center + (slotSizeVec / 2).suppressedYAxis(),
				startPixel = (halfBarSize + slotSizeVec).suppressedYAxis(),
				textureSize = halfBarSize,
				imageSize = imgSize,
				renderSize = halfBarSize)

			// highlight on slot
			var selectionOffset = slotSizeVec * currentSlot
			selectionOffset += (if (currentSlot < 4) 1 else if (currentSlot == 4) 2 else 3)
			this.renderTexture(center +
					(selectionOffset - slotSizeVec / 2 - halfBarSize).suppressedYAxis(),
				startPixel = halfBarSize.suppressedYAxis(),
				textureSize = slotSizeVec,
				imageSize = imgSize,
				renderSize = slotSizeVec)
			Rendering.pop_gl()

			// render icons in hot bar
			//val spacing =
			for (i <- hot_bar.indices) if (hot_bar(i) != null) {
			//	val offset =
			}
			Rendering.push_gl()

			Rendering.pop_gl()

		}
		else {
			// todo render current if not null

		}

	}

	def renderTexture(pos: V3O, startPixel: V3O = V3O.ZERO, textureSize: V3O = new V3O(256, 256),
			imageSize: V3O = new V3O(256, 256),
			renderSize: V3O = new V3O(256, 256),
			scale: Float = 1F, rgb: (Float, Float, Float) = (1F, 1F, 1F),
			opacity: Float = 1F, brightness: Int = 15728880, blend: Int = 771): Unit = {
		// scale render
		GlStateManager.scale(scale, scale, scale)
		// blend func
		GL11.glEnable(3042)
		GlStateManager.blendFunc(770, blend)
		// coloring
		GlStateManager.color(rgb._1, rgb._2, rgb._3, opacity)
		// brightness part A
		val bComp = (brightness >> 16 & 65535, brightness & 65535) // brightness composite

		val uvMin = startPixel / imageSize
		val uvMax = textureSize / imageSize + uvMin
		val w = renderSize.x
		val h = renderSize.y
		val wr = TessRenderer.getRenderer
		TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX_LMAP_COLOR)
		// min max
		wr.pos(pos.x + 0, pos.y + h, 0D).tex(uvMin.x, uvMax.y)
				.lightmap(bComp._1, bComp._2).color(rgb._1, rgb._2, rgb._3, opacity).endVertex()
		// max max
		wr.pos(pos.x + w, pos.y + h, 0D).tex(uvMax.x, uvMax.y)
				.lightmap(bComp._1, bComp._2).color(rgb._1, rgb._2, rgb._3, opacity).endVertex()
		// max min
		wr.pos(pos.x + w, pos.y + 0, 0D).tex(uvMax.x, uvMin.y)
				.lightmap(bComp._1, bComp._2).color(rgb._1, rgb._2, rgb._3, opacity).endVertex()
		// min min
		wr.pos(pos.x + 0, pos.y + 0, 0D).tex(uvMin.x, uvMin.y)
				.lightmap(bComp._1, bComp._2).color(rgb._1, rgb._2, rgb._3, opacity).endVertex()
		TessRenderer.draw()
		GL11.glDisable(3042)
	}

}
