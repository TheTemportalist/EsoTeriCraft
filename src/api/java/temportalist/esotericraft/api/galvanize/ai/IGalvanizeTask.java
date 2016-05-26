package temportalist.esotericraft.api.galvanize.ai;

import net.minecraft.entity.EntityCreature;

/**
 * Created by TheTemportalist on 5/22/2016.
 *
 * @author TheTemportalist
 */
public interface IGalvanizeTask {

	EnumTaskType getTaskType();

	boolean shouldExecute(EntityCreature entity);

	void startExecuting(EntityCreature entity);

	void updateTask(EntityCreature entity);

	void resetTask(EntityCreature entity);

}
