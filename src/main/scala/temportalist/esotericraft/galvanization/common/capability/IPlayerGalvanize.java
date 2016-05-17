package temportalist.esotericraft.galvanization.common.capability;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import temportalist.esotericraft.galvanization.client.EntityModel;
import temportalist.esotericraft.galvanization.common.entity.emulator.EntityState;

import javax.annotation.Nullable;

/**
 * Created by TheTemportalist on 5/7/2016.
 *
 * @author TheTemportalist
 */
public interface IPlayerGalvanize extends INBTSerializable<NBTTagCompound> {

	void onTickClient();

	void onTickServer();

	void setEntityState(String entityName, World world);

	void setEntityStateEntity(EntityLivingBase entity);

	EntityState getEntityState();

	void clearEntityState();

	@SideOnly(Side.CLIENT)
	@Nullable
	EntityModel<? extends EntityLivingBase, ? extends EntityLivingBase> getEntityModelInstance(World world);

}
