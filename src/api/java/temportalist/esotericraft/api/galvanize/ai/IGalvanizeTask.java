package temportalist.esotericraft.api.galvanize.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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

	boolean canEntityUse(World world, ItemStack stack);

}
