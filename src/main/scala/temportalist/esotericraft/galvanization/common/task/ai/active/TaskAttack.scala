package temportalist.esotericraft.galvanization.common.task.ai.active

import com.google.common.base.Predicate
import net.minecraft.entity.monster.{EntityCreeper, IMob}
import net.minecraft.entity.{EntityCreature, EntityLiving, EntityLivingBase}
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.util.{DamageSource, EnumFacing}
import temportalist.esotericraft.api.galvanize.ai.{EnumTaskType, GalvanizeTask}
import temportalist.esotericraft.emulation.common.IEntityEmulator
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.task.ai.core.TaskBase
import temportalist.esotericraft.galvanization.common.task.ai.interfaces.{ITargetEntity, ITaskBoundingBoxMixin}
import temportalist.origin.api.common.lib.Vect

import scala.collection.{JavaConversions, mutable}

/**
  *
  * Created by TheTemportalist on 5/26/2016.
  *
  * @author TheTemportalist
  */
@GalvanizeTask(modid = Galvanize.MOD_ID,
	name = "attack",
	displayName = "Attack"
)
class TaskAttack(
		pos: BlockPos, face: EnumFacing
) extends TaskBase(pos, face) with ITaskBoundingBoxMixin with ITargetEntity {

	private val speed: Double = 1.2D
	private val posVect = new Vect(this.pos) + new Vect(this.face) * 0.5

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
		true
	}

	def getNearbyEntities(entity: EntityCreature): mutable.Buffer[EntityLiving] = {
		JavaConversions.asScalaBuffer(
			entity.getEntityWorld.getEntitiesWithinAABB(classOf[EntityLiving], this.getBoundingBox,
				new Predicate[EntityLiving] {
					override def apply(living: EntityLiving): Boolean = {
						living != null &&
								IMob.VISIBLE_MOB_SELECTOR.apply(living) &&
								!living.isInstanceOf[EntityCreeper]
					}
				}
			)
		)
	}

	override def startExecuting(entity: EntityCreature): Unit = {
		super.startExecuting(entity)
	}

	override def updateTask(entity: EntityCreature): Unit = {

		if (entity.getAITarget != null) this.setTarget(entity.getAITarget)

		if (this.getTarget != null) {
			if (!this.getTarget.getEntityBoundingBox.intersectsWith(this.getBoundingBox))
				this.setTarget(null)
		}

		if (this.getTarget == null) {
			var target: EntityLivingBase = null
			var leastDistanceToOriginSq = -1D
			for (targetEnt <- this.getNearbyEntities(entity)) {
				// val distanceSq = (this.posVect - new Vect(targetEnt)).magnitude
				val distanceSq = (new Vect(targetEnt) - new Vect(targetEnt)).magnitude
				if (leastDistanceToOriginSq < 0 || distanceSq < leastDistanceToOriginSq) {
					leastDistanceToOriginSq = distanceSq
					target = targetEnt
				}
			}
			this.setTarget(target)
		}

		if (this.getTarget == null) return

		val distToTarget = entity.getDistanceToEntity(this.getTarget)
		val reqDist = 3.5D
		if (distToTarget <= reqDist) {
			entity match {
				case emulator: IEntityEmulator =>
					emulator.attackEntity(this.getTarget)
				case _ =>
					this.getTarget.attackEntityFrom(
						DamageSource.causeMobDamage(entity), 5F
					)
			}
			if (this.getTarget.isDead) this.setTarget(null)
		}
		else {
			this.moveEntityTowards(entity, this.getTarget, this.speed, this.getCanFly)
		}

	}

	override def resetTask(entity: EntityCreature): Unit =  {
		this.setTarget(null)
		entity.getNavigator.clearPathEntity()
	}

}
