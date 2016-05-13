package temportalist.esotericraft.galvanization.common.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 * Created by TheTemportalist on 5/7/2016.
 *
 * @author TheTemportalist
 */
public class HelperGalvanize {

	@CapabilityInject(IPlayerGalvanize.class)
	private static Capability<IPlayerGalvanize> CAPABILITY = null;

	public static Capability<IPlayerGalvanize> getCapabilityObject() {
		return CAPABILITY;
	}

	public static IPlayerGalvanize get(EntityPlayer player) {
		return player.getCapability(CAPABILITY, null);
	}

}
