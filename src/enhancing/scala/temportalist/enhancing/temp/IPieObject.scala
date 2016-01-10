package temportalist.enhancing.temp

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * Created by TheTemportalist on 1/8/2016.
  */
trait IPieObject {

	@SideOnly(Side.CLIENT)
	def draw(mc: Minecraft, x: Double, y: Double): Unit

}
