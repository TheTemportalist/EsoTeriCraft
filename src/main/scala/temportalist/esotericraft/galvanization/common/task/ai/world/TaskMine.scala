package temportalist.esotericraft.galvanization.common.task.ai.world

import net.minecraft.entity.EntityCreature
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import temportalist.esotericraft.api.galvanize.ai.{EnumTaskType, GalvanizeTask}
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.task.ai.core.TaskBase
import temportalist.esotericraft.galvanization.common.task.ai.interfaces.ITaskBoundingBoxMixin

/**
  *
  * Created by TheTemportalist on 5/27/2016.
  *
  * @author TheTemportalist
  */
@GalvanizeTask(modid = Galvanize.MOD_ID,
	name = "mine",
	displayName = "Mine"
)
class TaskMine(
		pos: BlockPos, face: EnumFacing
) extends TaskBase(pos, face) with ITaskBoundingBoxMixin {

	override def getTaskType: EnumTaskType = EnumTaskType.WORLD_INTERACTION

	override def createBoundingBox: AxisAlignedBB = {
		new AxisAlignedBB(
			pos.getX - 2,
			pos.getY + 0,
			pos.getZ - 2,
			pos.getX + 2,
			pos.getY + 3,
			pos.getZ + 2
		)
	}

	override def shouldExecute(entity: EntityCreature): Boolean = {
		false // TODO
	}

}
