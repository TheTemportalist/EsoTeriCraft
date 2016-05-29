package temportalist.esotericraft.galvanization.common.task.ai.world

import com.google.common.base.Predicate
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.{Entity, EntityCreature}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import temportalist.esotericraft.api.galvanize.ai.{EnumTaskType, GalvanizeTask}
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.task.ai.core.TaskBase
import temportalist.esotericraft.galvanization.common.task.ai.interfaces.{ITaskBoundingBoxMixin, ITaskInventory}
import temportalist.origin.api.common.lib.Vect

import scala.collection.{JavaConversions, mutable}

/**
  *
  * Created by TheTemportalist on 5/25/2016.
  *
  * @author TheTemportalist
  */
@GalvanizeTask(modid = Galvanize.MOD_ID,
	name = "itemCollect",
	displayName = "Collect Items"
)
class TaskItemCollect(
		pos: BlockPos, face: EnumFacing
) extends TaskBase(pos, face) with ITaskBoundingBoxMixin with ITaskInventory {

	private val speed: Double = 1D
	private val radiusXZ: Double = 4.5
	private val radiusY: Double = 0.5

	// ~~~~~ Task Info ~~~~~

	override def getTaskType: EnumTaskType = EnumTaskType.WORLD_INTERACTION

	// ~~~~~ Bounding Box ~~~~~

	override def createBoundingBox: AxisAlignedBB = {
		val center = new Vect(this.pos) + Vect.CENTER
		new AxisAlignedBB(
			center.x - this.radiusXZ,
			center.y - this.radiusY,
			center.z - this.radiusXZ,
			center.x + this.radiusXZ,
			center.y + this.radiusY,
			center.z + this.radiusXZ
		)
	}

	// ~~~~~ AI ~~~~~

	override def shouldExecute(entity: EntityCreature): Boolean = {
		val entities = this.findEntitiesInRange(entity)
		entities.nonEmpty
	}

	final def findEntitiesInRange(entity: EntityCreature): mutable.Buffer[Entity] = {
		val entList = entity.getEntityWorld.getEntitiesInAABBexcluding(
			entity, this.getBoundingBox,
			new Predicate[Entity] {
				override def apply(input: Entity): Boolean = {
					input.isInstanceOf[EntityItem]
				}
			}
		)
		JavaConversions.asScalaBuffer(entList)
	}

	override def startExecuting(entity: EntityCreature): Unit = {
		this.checkCanFly(entity)
	}

	override def updateTask(entity: EntityCreature): Unit = {

		val entityList = this.findEntitiesInRange(entity)

		var smallestDistanceToOrigin = -1D
		var closestEntityToOrigin: EntityItem = null
		var distanceToOrigin: Double = 0D
		for (entity <- entityList) {
			entity match {
				case entityItem: EntityItem =>
					distanceToOrigin = this.calculateDistanceToOriginFromEntity(entity)
					if (smallestDistanceToOrigin < 0 ||
							distanceToOrigin < smallestDistanceToOrigin) {
						smallestDistanceToOrigin = distanceToOrigin
						closestEntityToOrigin = entityItem
					}
				case _ =>
			}
		}

		if (closestEntityToOrigin == null) return

		val ownerDistanceToEntity = (new Vect(entity) - new Vect(closestEntityToOrigin)).length
		if (ownerDistanceToEntity <= 1.25D) {
			this.pickUpItem(entity, closestEntityToOrigin)
		}
		else {
			this.moveEntityTowards(entity, closestEntityToOrigin, this.speed, this.getCanFly)
		}

	}

	final def calculateDistanceToOriginFromEntity(entity: Entity): Double = {
		(new Vect(entity) - new Vect(this.pos)).length
	}

	final def pickUpItem(entity: EntityCreature, entityItem: EntityItem): Unit = {
		val stackRemaining = this.addItemToHands(entityItem.getEntityItem.copy(), entity)
		if (stackRemaining == null) entityItem.setDead()
		else entityItem.setEntityItemStack(stackRemaining)
	}

	// ~~~~~ End ~~~~~

}
