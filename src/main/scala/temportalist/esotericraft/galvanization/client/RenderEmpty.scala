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

	override def doRender(empty: EntityEmpty,
			x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float): Unit = {

		GlStateManager.pushMatrix()
		empty.getModelEntity match {
			case e: EntityLivingBase =>
				//GlStateManager.translate(x, y, z)

				e.hurtTime = empty.hurtTime
				e.maxHurtTime = empty.maxHurtTime
				e.attackedAtYaw = empty.attackedAtYaw

				e.posX = empty.posX
				e.prevPosX = empty.prevPosX
				e.posY = empty.posY
				e.prevPosY = empty.prevPosY
				e.posZ = empty.posZ
				e.prevPosZ = empty.prevPosZ
				e.rotationYaw = empty.rotationYaw
				e.prevRotationYaw = empty.prevRotationYaw
				e.renderYawOffset = empty.renderYawOffset
				e.prevRenderYawOffset = empty.prevRenderYawOffset
				e.rotationYawHead = empty.rotationYawHead
				e.prevRotationYawHead = empty.prevRotationYawHead
				e.rotationPitch = empty.rotationPitch
				e.prevRotationPitch = empty.prevRotationPitch

				e.swingingHand = empty.swingingHand
				e.swingProgress = empty.swingProgress
				e.prevSwingProgress = e.prevSwingProgress
				e.swingProgressInt = empty.swingProgressInt
				e.limbSwing = empty.limbSwing
				e.limbSwingAmount = e.limbSwingAmount
				e.prevLimbSwingAmount = e.prevLimbSwingAmount

				e.motionX = empty.motionX
				e.motionY = empty.motionY
				e.motionZ = empty.motionZ

				e.onGround = empty.onGround

				val render: Render[EntityLivingBase] = this.getRenderManager.getEntityClassRenderObject(e.getClass)
				try {
					render.doRender(e, x, y, z, 0, partialTicks)
				}
				catch {
					case e: Exception => e.printStackTrace()
				}

			case _ =>
		}
		GlStateManager.popMatrix()

		super.doRender(empty, x, y, z, entityYaw, partialTicks)
	}

}
object RenderEmpty extends IRenderFactory[EntityEmpty] {
	override def createRenderFor(manager: RenderManager): Render[_ >: EntityEmpty] = {
		new RenderEmpty(manager)
	}
}
