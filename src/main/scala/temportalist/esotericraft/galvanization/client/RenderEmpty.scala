package temportalist.esotericraft.galvanization.client

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.registry.IRenderFactory
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.entity.EntityEmpty

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class RenderEmpty(manager: RenderManager) extends Render[EntityEmpty](manager) {

	override def getEntityTexture(entity: EntityEmpty): ResourceLocation = null

	override def doRender(entity: EntityEmpty,
			x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float): Unit = {

		GlStateManager.pushMatrix()
		entity.getModelEntity match {
			case e: EntityLivingBase =>
				GlStateManager.translate(x, y, z)
				this.getRenderManager.doRenderEntity(e, 0, 0, 0, entityYaw, 0, true)
			case _ =>
		}
		GlStateManager.popMatrix()

		super.doRender(entity, x, y, z, entityYaw, partialTicks)
	}

}
object RenderEmpty extends IRenderFactory[EntityEmpty] {
	override def createRenderFor(manager: RenderManager): Render[_ >: EntityEmpty] = {
		new RenderEmpty(manager)
	}
}
