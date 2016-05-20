package temportalist.esotericraft.galvanization.common.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;

/**
 * Created by TheTemportalist on 5/19/2016.
 *
 * @author TheTemportalist
 */
public abstract class EntityAIHelper extends EntityAIBase {

	public void setMutexBits(EnumAIMutex mutex) {
		super.setMutexBits(mutex.getMutexBits());
	}

	public final void moveEntityTowards(EntityCreature entity, Entity target,
			double speed, boolean canFly) {
		if (canFly) this.moveEntityFlyingTowards(entity, target.posX, target.posY, target.posZ, speed);
		else entity.getNavigator().tryMoveToEntityLiving(target, speed);
	}

	public final void moveEntityTowards(EntityCreature entity, double x, double y, double z,
			double speed, boolean canFly) {
		if (canFly) this.moveEntityFlyingTowards(entity, x, y, z, speed);
		else entity.getNavigator().tryMoveToXYZ(x, y, z, speed);
	}

	public final void moveEntityFlyingTowards(EntityCreature entity, double x, double y, double z, double speed) {
		Vec3d diffVect = new Vec3d(entity.posX - x, entity.posY - y, entity.posZ - z);
		Vec3d diffNormalized = diffVect.normalize();
		double scale = 0.3D * speed;
		entity.motionX = -diffNormalized.xCoord * scale;
		entity.motionY = -diffNormalized.yCoord * scale;
		entity.motionZ = -diffNormalized.zCoord * scale;
	}

}
