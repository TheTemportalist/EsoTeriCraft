package temportalist.esotericraft.galvanization.common.entity.ai

import com.google.common.base.Predicate
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.{Entity, EntityCreature}
import net.minecraft.util.math.AxisAlignedBB
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.origin.api.common.lib.Vect

import scala.collection.{JavaConversions, mutable}

/**
  *
  * Created by TheTemportalist on 5/19/2016.
  *
  * @author TheTemportalist
  */
class EntityAIItemPickUp[O <: EntityCreature](
		private val owner: O,
		private var origin: Vect,
		private val radiusXZ: Double,
		private val radiusY: Double,
		private val speed: Double = 1D,
		private val canFly: Boolean = false
) extends EntityAIHelper with IEntityAIInventory {

	this.setMutexBits(EnumAIMutex.EVERYTHING_OKAY)

	def getCollectionArea: AxisAlignedBB = {
		if (this.origin == null) null
		else {
			new AxisAlignedBB(
				this.origin.x - this.radiusXZ,
				this.origin.y + 0.5 - this.radiusY,
				this.origin.z - this.radiusXZ,
				this.origin.x + this.radiusXZ,
				this.origin.y + 0.5 + this.radiusY,
				this.origin.z + this.radiusXZ
			)
		}
	}

	override def shouldExecute(): Boolean = {
		if (this.origin == null) return false

		val entities = this.findEntitiesInRange
		val exec = entities.nonEmpty
		exec
	}

	override def updateTask(): Unit = {
		val entityList = this.findEntitiesInRange

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

		//Galvanize.log("Going for an item")

		val ownerDistanceToEntity = (new Vect(this.owner) - new Vect(closestEntityToOrigin)).length
		if (ownerDistanceToEntity <= 1.25D) {
			this.pickUpItem(closestEntityToOrigin)
		}
		else {
			this.moveEntityTowards(this.owner, closestEntityToOrigin, this.speed, this.canFly)
		}
	}

	final def findEntitiesInRange: mutable.Buffer[Entity] = {
		val entList = this.owner.getEntityWorld.getEntitiesInAABBexcluding(
			this.owner, this.getCollectionArea,
			new Predicate[Entity] {
				override def apply(input: Entity): Boolean = {
					input.isInstanceOf[EntityItem]
				}
			}
		)
		JavaConversions.asScalaBuffer(entList)
	}

	final def calculateDistanceToOriginFromEntity(entity: Entity): Double = {
		(new Vect(entity) - this.origin).length
	}

	final def pickUpItem(entityItem: EntityItem): Unit = {
		val stackRemaining = this.addItemToHands(entityItem.getEntityItem.copy(), this.owner)
		if (stackRemaining == null) entityItem.setDead()
		else entityItem.setEntityItemStack(stackRemaining)
	}

}
