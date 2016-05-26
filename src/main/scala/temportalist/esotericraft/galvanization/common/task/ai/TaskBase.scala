package temportalist.esotericraft.galvanization.common.task.ai

import net.minecraft.entity.EntityCreature
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import temportalist.esotericraft.api.galvanize.ai.IGalvanizeTask

/**
  *
  * Created by TheTemportalist on 5/26/2016.
  *
  * @author TheTemportalist
  */
abstract class TaskBase(
		private val pos: BlockPos, private val face: EnumFacing
) extends IGalvanizeTask with ITaskFlyCheck with ITaskEntityMover {

	// ~~~~~ AI ~~~~~

	override def startExecuting(entity: EntityCreature): Unit = {
		this.checkCanFly(entity)
	}

	override def updateTask(entity: EntityCreature): Unit = {}

	override def resetTask(entity: EntityCreature): Unit = {}

	// ~~~~~ End ~~~~~

}
