package temportalist.esotericraft.galvanization.common.task.ai

import net.minecraft.util.math.AxisAlignedBB
import temportalist.esotericraft.api.galvanize.ai.ITaskBoundingBox

/**
  *
  * Created by TheTemportalist on 5/26/2016.
  *
  * @author TheTemportalist
  */
trait ITaskBoundingBoxMixin extends ITaskBoundingBox {

	private var boundingBox: AxisAlignedBB = null

	def createBoundingBox: AxisAlignedBB

	override final def updateBoundingBox(): Unit = {
		this.boundingBox = this.createBoundingBox
	}

	override final def getBoundingBox: AxisAlignedBB = this.boundingBox

}
