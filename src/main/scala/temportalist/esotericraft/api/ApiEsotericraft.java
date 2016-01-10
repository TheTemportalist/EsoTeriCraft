package temportalist.esotericraft.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IExtendedEntityProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheTemportalist on 1/4/2016.
 */
public class ApiEsotericraft {

	private static final List<EsotericraftModule> modules =
			new ArrayList<EsotericraftModule>();
	private static final ModuleTrigger.MapTrigger<EsotericraftModule> moduleMap =
			new ModuleTrigger.MapTrigger<EsotericraftModule>();

	public static final String KEY_EXTENDED = "esoteric";
	public static final String KEY_ESOTERICRAFT_MODULES = "modules";

	public static void registerModule(EsotericraftModule module, ModuleTrigger... triggers) {
		ApiEsotericraft.modules.add(module);
		for (ModuleTrigger trigger : triggers) ApiEsotericraft.moduleMap.add(trigger, module);
	}

	public static EsotericraftModule getModuleForTrigger(Object trigger) {
		return ApiEsotericraft.moduleMap.getValueFromTriggerStack(trigger);
	}

	public static int getID(EsotericraftModule module) {
		return ApiEsotericraft.modules.indexOf(module);
	}

	public static EsotericraftModule getModule(int index) {
		return (index < 0 || index >= ApiEsotericraft.modules.size()) ? null
				: ApiEsotericraft.modules.get(index);
	}

	public static class Player {

		private static IEsotericPlayer getProps(EntityPlayer player) {
			IExtendedEntityProperties extendedPlayer =
					player.getExtendedProperties(ApiEsotericraft.KEY_EXTENDED);
			if (extendedPlayer instanceof IEsotericPlayer) return (IEsotericPlayer) extendedPlayer;
			else return null;
		}

		public static void impart(EntityPlayer player, EsotericraftModule module) {
			IEsotericPlayer esoteric = Player.getProps(player);
			if (esoteric != null && esoteric.canImpart(module)) esoteric.impart(module);
		}

		public static boolean hasKnowledgeOf(EntityPlayer player, EsotericraftModule module) {
			IEsotericPlayer esoteric = Player.getProps(player);
			return esoteric != null && esoteric.hasKnowledgeOf(module);
		}

	}

}
