package temportalist.esotericraft.galvanization.common.entity.ai

import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.entity.{EntityCreature, EntityLivingBase}
import net.minecraft.util.math.MathHelper
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
) extends EntityAIBase {

	private var followingEntity: EntityLivingBase = null
	private var followingPos: Vect = null

	override def shouldExecute(): Boolean = {
		this.followingEntity = this.owner.getEntityWorld.getClosestPlayerToEntity(this.owner, this.radius)

		if (this.canFly) {
			val moveHelper = this.owner.getMoveHelper
			/*
			if (moveHelper.isUpdating) {
				val diffX = moveHelper.getX - this.owner.posX
				val diffY = moveHelper.getY - this.owner.posY
				val diffZ = moveHelper.getZ - this.owner.posZ
				val dist = diffX * diffX + diffY * diffY + diffZ + diffZ
				return 3600 < dist || dist < 1D
			}
			//else return true
			*/
		}

		this.followingEntity != null
	}

	override def startExecuting(): Unit = {
		if (this.followingEntity != null)
			this.followingPos = new Vect(this.followingEntity)

		if (this.canFly) {
			if (this.followingPos != null) {
				//Galvanize.log(this.followingPos.toString)
				//if (!this.owner.getMoveHelper.isUpdating)
				///*
				this.owner.getMoveHelper.setMoveTo(
					this.followingPos.x,
					this.followingPos.y,
					this.followingPos.z,
					this.speed
				)
				//*/
			}
		}
	}

	override def continueExecuting(): Boolean = super.continueExecuting()//!this.canFly

	override def updateTask(): Unit = {
		//if (this.canFly) return

		this.owner.getLookHelper.setLookPositionWithEntity(
			this.followingEntity, this.owner.getHorizontalFaceSpeed + 20,
			this.owner.getVerticalFaceSpeed
		)

		if (canFly) {
			val diffX = this.owner.posX - this.followingEntity.posX
			val diffY = this.owner.posY - this.followingEntity.posY
			val diffZ = this.owner.posZ - this.followingEntity.posZ
			var dist = diffX * diffX + diffY * diffY + diffZ + diffZ
			dist = MathHelper.sqrt_double(dist)
			val xDiv = diffX / dist
			val yDiv = diffY / dist
			val zDiv = diffZ / dist

			this.owner.motionX -= xDiv * 0.1D * this.speed
			this.owner.motionY -= yDiv * 0.1D * this.speed
			this.owner.motionZ -= zDiv * 0.1D * this.speed
			//this.owner.moveEntityWithHeading(0, 0)

			val aabb = this.owner.getEntityBoundingBox.offset(xDiv, yDiv, zDiv)
			if (this.owner.worldObj.getCollisionBoxes(this.owner, aabb).isEmpty) {
				//this.owner.getNavigator.clearPathEntity()
				// is not colliding
				//this.owner.motionX -= xDiv * 0.1D * this.speed
				//this.owner.motionY -= yDiv * 0.1D * this.speed
				//this.owner.motionZ -= zDiv * 0.1D * this.speed
				//this.owner.motionY = -yDiv * 0.1D * this.speed
				//this.owner.moveEntity(0, -yDiv, 0)
				//this.owner.moveEntityWithHeading(0, 0)
			}
			else {
				//this.owner.getNavigator.tryMoveToEntityLiving(this.followingEntity, this.speed)
			}

		}
		else {
			if (this.owner.getDistanceSqToEntity(this.followingEntity) < 6.25D) // proximity to player
				this.owner.getNavigator.clearPathEntity()
			else {
				this.owner.getNavigator.tryMoveToEntityLiving(this.followingEntity, this.speed)
			}
		}



	}

	override def resetTask(): Unit = {
		this.followingEntity = null
		this.followingPos = null
		this.owner.getNavigator.clearPathEntity()
	}

}
