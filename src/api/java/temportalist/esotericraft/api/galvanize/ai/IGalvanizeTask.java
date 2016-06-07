package temportalist.esotericraft.api.galvanize.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by TheTemportalist on 5/22/2016.
 *
 * @author TheTemportalist
 */
public interface IGalvanizeTask extends INBTSerializable<NBTTagCompound> {

	EnumTaskType getTaskType();

	void onSpawn(World world);

	boolean shouldExecute(EntityCreature entity);

	void startExecuting(EntityCreature entity);

	void updateTask(EntityCreature entity);

	void resetTask(EntityCreature entity);

	boolean canEntityUse(World world, ItemStack stack);

}
