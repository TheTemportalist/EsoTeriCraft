package temportalist.esotericraft.api.galvanize;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.INBTSerializable;

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

	void parseMappingArguments(Object[] args);

	String[] encodeMappingArguments();

	void onApplicationTo(EntityLivingBase entity);

	void onUpdate(EntityLivingBase entity);

	void onRemovalFrom(EntityLivingBase entity);

	void deserialize(NBTBase nbt);
}
