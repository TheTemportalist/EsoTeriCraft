package temportalist.esotericraft.galvanization.common.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

/**
 * Created by TheTemportalist on 5/19/2016.
 *
 * @author TheTemportalist
 */
public class EntityMoveHelperFly extends EntityMoveHelper {

	private int courseChangeCooldown;

	public EntityMoveHelperFly(EntityLiving entity) {
		super(entity);
	}

	@Override
	public void onUpdateMoveHelper() {
		if (this.action == Action.MOVE_TO) {
			if (this.courseChangeCooldown-- <= 0) {
				this.courseChangeCooldown += this.entity.getRNG().nextInt(5) + 2;
				double diffX = this.posX - this.entity.posX;
				double diffY = this.posY - this.entity.posY;
				double diffZ = this.posZ - this.entity.posZ;
				double dist = diffX * diffX + diffY * diffY + diffZ + diffZ;
				dist = MathHelper.sqrt_double(dist);

				if (this.isNotColliding(diffX, diffY, diffZ, dist)) {
					this.entity.motionX += diffX / dist * 0.1D;
					this.entity.motionY += diffY / dist * 0.1D;
					this.entity.motionZ += diffZ / dist * 0.1D;
				}
				else {
					this.action = Action.WAIT;
				}
			}
		}
		//else super.onUpdateMoveHelper();
	}

	private boolean isNotColliding(double diffX, double diffY, double diffZ, double dist) {
		double x1 = diffX / dist;
		double y1 = diffY / dist;
		double z1 = diffZ / dist;
		AxisAlignedBB aabb = this.entity.getEntityBoundingBox();

		for (int i = 1; (double)i < dist; ++i) {
			aabb = aabb.offset(x1, y1, z1);
			if (!this.entity.worldObj.getCollisionBoxes(this.entity, aabb).isEmpty())
				return false;
		}

		return true;
	}

}
