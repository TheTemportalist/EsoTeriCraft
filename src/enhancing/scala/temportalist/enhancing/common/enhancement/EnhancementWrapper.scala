package temportalist.enhancing.common.enhancement

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.enhancing.api.Enhancement
import temportalist.enhancing.temp.IPieObject

/**
  * Created by TheTemportalist on 1/8/2016.
  */
class EnhancementWrapper(private val enhancement: Enhancement) extends IPieObject {

	def getGlobalID: Int = this.enhancement.getGlobalID

	@SideOnly(Side.CLIENT)
	override def draw(mc: Minecraft, x: Double, y: Double): Unit = this.enhancement.draw(mc, x, y)

}
