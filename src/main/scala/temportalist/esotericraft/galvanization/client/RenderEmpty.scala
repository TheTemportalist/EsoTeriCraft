package temportalist.esotericraft.galvanization.client

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.registry.IRenderFactory
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.emulation.client.EntityModel
import temportalist.esotericraft.galvanization.common.entity.EntityEmpty

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
class RenderEmpty(manager: RenderManager) extends Render[EntityEmpty](manager) {

	override def getEntityTexture(entity: EntityEmpty): ResourceLocation = null

	override def doRender(empty: EntityEmpty,
			x: Double, y: Double, z: Double, yaw: Float, partialTicks: Float): Unit = {

		GlStateManager.pushMatrix()
		empty.getEntityModelInstance(empty.getEntityWorld) match {
			case model: EntityModel[_, _] =>
				model.forceRender(empty.getEntityStateInstance, x, y, z, yaw, partialTicks)
			case _ =>
		}
		GlStateManager.popMatrix()

		super.doRender(empty, x, y, z, yaw, partialTicks)
	}

}
@SideOnly(Side.CLIENT)
object RenderEmpty extends IRenderFactory[EntityEmpty] {
	override def createRenderFor(manager: RenderManager): Render[_ >: EntityEmpty] = {
		new RenderEmpty(manager)
	}
}
