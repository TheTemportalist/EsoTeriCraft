package temportalist.esotericraft.galvanization.common.entity.ai

import net.minecraft.entity.{EntityCreature, EntityLivingBase}
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 5/19/2016.
  *
  * @author TheTemportalist
  */
class EntityAIFollowPlayer(
		private val owner: EntityCreature,
		private val speed: Double = 1.2D,
		private val radius: Double = 16D,
		private val canFly: Boolean = false
) extends EntityAIHelper {

	this.setMutexBits(EnumAIMutex.SWIMMING_NOT_WATCHING)

	private var followingEntity: EntityLivingBase = null
	private var followingPos: Vect = null

	override def shouldExecute(): Boolean = {
		this.followingEntity = this.owner.getEntityWorld.getClosestPlayerToEntity(this.owner, this.radius)
		this.followingEntity != null
	}

	override def startExecuting(): Unit = {
		if (this.followingEntity != null)
			this.followingPos = new Vect(this.followingEntity)
	}

	override def updateTask(): Unit = {

		this.owner.getLookHelper.setLookPositionWithEntity(
			this.followingEntity, this.owner.getHorizontalFaceSpeed + 20,
			this.owner.getVerticalFaceSpeed
		)

		if (this.owner.getDistanceSqToEntity(this.followingEntity) < 6.25D) { // proximity to player
			if (!this.canFly) {
				this.owner.getNavigator.clearPathEntity()
			}
		}
		else {
			this.moveEntityTowards(this.owner, this.followingEntity, this.speed, this.canFly)
		}

	}

	override def resetTask(): Unit = {
		this.followingEntity = null
		this.followingPos = null
		this.owner.getNavigator.clearPathEntity()
	}

}
