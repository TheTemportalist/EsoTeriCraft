package temportalist.esotericraft.api.galvanize.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Created by TheTemportalist on 5/22/2016.
 *
 * @author TheTemportalist
 */
public interface IGalvanizeTask {

	AxisAlignedBB constructBoundingBox();

	boolean shouldExecute(EntityCreature entity);

	void startExecuting(EntityCreature entity);

	void updateTask(EntityCreature entity);

	void resetTask(EntityCreature entity);

}
