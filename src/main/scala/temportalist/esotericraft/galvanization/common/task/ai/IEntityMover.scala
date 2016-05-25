package temportalist.esotericraft.galvanization.common.task.ai

import net.minecraft.entity.{Entity, EntityCreature}
import net.minecraft.util.math.Vec3d

/**
  *
  * Created by TheTemportalist on 5/25/2016.
  *
  * @author TheTemportalist
  */
trait IEntityMover {

	def moveEntityTowards(entity: EntityCreature, target: Entity, speed: Double, canFly: Boolean) {
		if (canFly)
			this.moveEntityFlyingTowards(entity, target.posX, target.posY, target.posZ, speed)
		else entity.getNavigator.tryMoveToEntityLiving(target, speed)
	}

	def moveEntityTowards(entity: EntityCreature, x: Double, y: Double, z: Double, speed: Double,
			canFly: Boolean) {
		if (canFly) this.moveEntityFlyingTowards(entity, x, y, z, speed)
		else entity.getNavigator.tryMoveToXYZ(x, y, z, speed)
	}

	def moveEntityFlyingTowards(entity: EntityCreature, x: Double, y: Double, z: Double,
			speed: Double) {
		val diffVect: Vec3d = new Vec3d(entity.posX - x, entity.posY - y, entity.posZ - z)
		val diffNormalized: Vec3d = diffVect.normalize
		val scale: Double = 0.3D * speed
		entity.motionX = -diffNormalized.xCoord * scale
		entity.motionY = -diffNormalized.yCoord * scale
		entity.motionZ = -diffNormalized.zCoord * scale
	}

}
