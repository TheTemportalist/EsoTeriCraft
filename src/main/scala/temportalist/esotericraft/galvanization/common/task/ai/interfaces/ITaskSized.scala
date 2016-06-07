package temportalist.esotericraft.galvanization.common.task.ai.interfaces

import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.Axis
import net.minecraft.util.math.AxisAlignedBB
import temportalist.esotericraft.galvanization.common.task.ai.core.TaskBase
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 6/6/2016.
  *
  * @author TheTemportalist
  */
trait ITaskSized extends TaskBase with ITaskBoundingBoxMixin {

	def getRadius(axis: Axis): Double

	def getCenter: Vect = {
		val face = this.getFace
		var pos = new Vect(this.getPosition) + Vect.CENTER
		if (face == EnumFacing.DOWN) pos += face
		pos
	}

	override def createBoundingBox: AxisAlignedBB = {
		val center = this.getCenter
		val radius = (this.getRadius(Axis.X), this.getRadius(Axis.Y), this.getRadius(Axis.Z))
		new AxisAlignedBB(
			center.x - radius._1, center.y - radius._2, center.z - radius._3,
			center.x + radius._1, center.y + radius._2, center.z + radius._3
		)
	}
}
