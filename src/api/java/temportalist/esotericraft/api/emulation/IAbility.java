package temportalist.esotericraft.api.emulation;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by TheTemportalist on 5/17/2016.
 *
 * @author TheTemportalist
 */
public interface IAbility<N extends NBTBase> extends INBTSerializable<N> {

	@interface Ability {

		String id();

	}

	String getName();

	void parseMappingArguments(Object[] args, String entry);

	String[] encodeMappingArguments();

	void onApplicationTo(EntityLivingBase entity);

	void onUpdate(EntityLivingBase entity);

	void onRemovalFrom(EntityLivingBase entity);

	boolean hasNBT();

	void deserialize(NBTBase nbt);

	@SideOnly(Side.CLIENT)
	void renderPost(EntityLivingBase entity);

}
