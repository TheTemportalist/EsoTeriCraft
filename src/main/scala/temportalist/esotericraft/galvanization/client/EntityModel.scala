package temportalist.esotericraft.galvanization.client

import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  *
  * Created by TheTemportalist on 5/8/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
class EntityModel[C <: EntityLivingBase, R <: EntityLivingBase](
		private val entClass: Class[C],
		private val renderer: Render[R],
		private val model: ModelBase
) {

	@SideOnly(Side.CLIENT)
	def forceRender(entity: EntityLivingBase, x: Double, y: Double, z: Double, yaw: Float, partialTicks: Float): Unit = {
		if (Minecraft.getMinecraft.getRenderManager.renderEngine != null &&
				Minecraft.getMinecraft.getRenderManager.renderViewEntity != null) {
			try {
				this.renderer.doRender(entity.asInstanceOf[R], x, y, z, yaw, partialTicks)
			}
			catch {
				case e: Exception =>
					e.printStackTrace()
			}
		}
	}

}
