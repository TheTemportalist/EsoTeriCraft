package temportalist.enhancing.client

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import temportalist.enhancing.common.tile.TEEnhancingTable
import temportalist.origin.api.client.utility.Rendering

/**
  * Created by TheTemportalist on 1/9/2016.
  */
object TERenderEnhancingTable extends TileEntitySpecialRenderer[TEEnhancingTable] {

	override def renderTileEntityAt(te: TEEnhancingTable, x: Double, y: Double, z: Double,
			partialTicks: Float, destroyStage: Int): Unit = {
		val renderStack = te.getEnhancingStackForRender
		if (renderStack != null) {
			Rendering.push_gl()
			Rendering.translate_gl(x, y, z)
			val scale = 0.75
			Rendering.translate_gl(0.5, 1.0 + (scale / 4), 0.5)
			GlStateManager.scale(scale, scale, scale)
			val time = te.getWorld.getTotalWorldTime
			GlStateManager.rotate((time % 360) * 4, 0, 1, 0)
			Rendering.mc.getRenderItem.renderItem(
				renderStack, ItemCameraTransforms.TransformType.GUI)
			Rendering.pop_gl()
		}
	}

}
