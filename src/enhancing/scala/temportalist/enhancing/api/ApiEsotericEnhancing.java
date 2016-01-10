package temportalist.enhancing.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by TheTemportalist on 12/31/2015.
 */
public class ApiEsotericEnhancing {

	private static final HashMap<Enhancement, Integer> objectToID =
			new HashMap<Enhancement, Integer>();
	private static final HashMap<Integer, Enhancement> idToObject =
			new HashMap<Integer, Enhancement>();

	public static final String KEY_EXTENDED = "esoteric_enhancing";

	public static void register(Enhancement enhancement) {
		int id = ApiEsotericEnhancing.objectToID.size();
		ApiEsotericEnhancing.objectToID.put(enhancement, id);
		ApiEsotericEnhancing.idToObject.put(id, enhancement);
		MinecraftForge.EVENT_BUS.register(enhancement);
	}

	public static int getGlobalID(Enhancement enhancement) {
		if (!ApiEsotericEnhancing.objectToID.containsKey(enhancement)) return -1;
		return ApiEsotericEnhancing.objectToID.get(enhancement);
	}

	public static Enhancement getEnhancement(int globalID) {
		return ApiEsotericEnhancing.idToObject.get(globalID);
	}

	public static Collection<Enhancement> getAllEnhancements() {
		return ApiEsotericEnhancing.idToObject.values();
	}

	public static IEnhancingPlayer getProps(EntityPlayer player) {
		IExtendedEntityProperties extendedPlayer =
				player.getExtendedProperties(ApiEsotericEnhancing.KEY_EXTENDED);
		if (extendedPlayer instanceof IEnhancingPlayer) return (IEnhancingPlayer) extendedPlayer;
		else return null;
	}

}
