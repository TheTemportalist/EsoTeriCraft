package temportalist.esotericraft.sorcery.common;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import temportalist.esotericraft.api.capability.api.StorageBlank;

import java.util.concurrent.Callable;

/**
 * Created by TheTemportalist on 4/24/2016.
 *
 * @author TheTemportalist
 */
public class CapabilitySorcery {

	@CapabilityInject(SorceryPlayer.class)
	public static final Capability<SorceryPlayer> CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(
			SorceryPlayer.class,
			new StorageBlank<SorceryPlayer>(),
			new Callable<SorceryPlayer>() {
				@Override
				public SorceryPlayer call() throws Exception {
					return null;
				}
			}
		);
	}

}
