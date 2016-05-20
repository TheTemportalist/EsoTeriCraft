package temportalist.esotericraft.api.sorcery;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by TheTemportalist on 5/7/2016.
 *
 * @author TheTemportalist
 */
public class ApiSorcery {

	@CapabilityInject(ISorceryPlayer.class)
	private static Capability<ISorceryPlayer> CAPABILITY = null;

	public static Capability<ISorceryPlayer> getCapabilityObject() {
		return CAPABILITY;
	}

	public static ISorceryPlayer get(EntityPlayer player) {
		return player.getCapability(CAPABILITY, null);
	}

	public interface ISorceryPlayer extends INBTSerializable<NBTTagCompound> {

		void cast();

	}

}
