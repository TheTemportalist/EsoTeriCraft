package temportalist.esotericraft.galvanization.common.task.ai.active

import net.minecraft.entity.{EntityCreature, EntityLivingBase}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import temportalist.esotericraft.api.galvanize.ai.{EnumTaskType, GalvanizeTask}
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.task.ai.core.TaskBase
import temportalist.esotericraft.galvanization.common.task.ai.interfaces.ITaskBoundingBoxMixin
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 5/25/2016.
  *
  * @author TheTemportalist
  */
@GalvanizeTask(modid = Galvanize.MOD_ID,
	name = "followPlayer",
	displayName = "Follow Player"
)
class TaskFollowPlayer(
		pos: BlockPos, face: EnumFacing
) extends TaskBase(pos, face) with ITaskBoundingBoxMixin {

	private val speed: Double = 1.2D
	private val radius: Double = 16D

	private var followingEntity: EntityLivingBase = null
	private var followingPos: Vect = null

	// ~~~~~ Task Info ~~~~~

	override def getTaskType: EnumTaskType = EnumTaskType.MOVEMENT_ACTIVE

	// ~~~~~ Bounding Box ~~~~~

	override def createBoundingBox: AxisAlignedBB = {
		new AxisAlignedBB(
			pos.getX - 8, pos.getY + 0, pos.getZ - 8,
			pos.getX + 8, pos.getY + 5, pos.getZ + 8
		)
	}

	// ~~~~~ AI ~~~~~

	override def shouldExecute(entity: EntityCreature): Boolean = {
		this.followingEntity = entity.getEntityWorld
				.getClosestPlayerToEntity(entity, this.radius)
		this.followingEntity != null &&
				this.followingEntity.getEntityBoundingBox.intersectsWith(this.getBoundingBox)
	}

	override def startExecuting(entity: EntityCreature): Unit = {
		super.startExecuting(entity)
		if (this.followingEntity != null)
			this.followingPos = new Vect(this.followingEntity)
	}

	override def updateTask(entity: EntityCreature): Unit = {

		entity.getLookHelper.setLookPositionWithEntity(
			this.followingEntity, entity.getHorizontalFaceSpeed + 20,
			entity.getVerticalFaceSpeed
		)

		if (entity.getDistanceSqToEntity(this.followingEntity) < 6.25D) {
			// proximity to player
			if (!this.getCanFly) {
				entity.getNavigator.clearPathEntity()
			}
		}
		else {
			this.moveEntityTowards(entity, this.followingEntity, this.speed, this.getCanFly)
		}

	}

	override def resetTask(entity: EntityCreature): Unit = {
		this.followingEntity = null
		this.followingPos = null
		entity.getNavigator.clearPathEntity()
	}

	// ~~~~~ End ~~~~~

}
