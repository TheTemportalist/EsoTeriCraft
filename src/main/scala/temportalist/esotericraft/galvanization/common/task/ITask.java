package temportalist.esotericraft.galvanization.common.task;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import temportalist.esotericraft.api.galvanize.ai.IGalvanizeTask;

/**
 * Created by TheTemportalist on 5/24/2016.
 *
 * @author TheTemportalist
 */
public interface ITask extends INBTSerializable<NBTTagCompound> {

	String getModID();

	String getName();

	ResourceLocation getIconLocation();

	World getWorld();

	BlockPos getPosition();

	EnumFacing getFace();

	void setPosition(BlockPos position, EnumFacing face);

	void onUpdateServer();

	void onSpawn(World world, BlockPos pos, EnumFacing face);

	void onBreak(World world, BlockPos pos, EnumFacing face);

	void onBroken(boolean doDrop);

	void setInfoAI(String modid, String name, String displayName, Class<? extends IGalvanizeTask> classAI);

	IGalvanizeTask getAI();

	boolean isValid();

}
