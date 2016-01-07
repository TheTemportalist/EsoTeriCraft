package com.temportalist.esotericraft.api;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheTemportalist on 1/4/2016.
 */
public class ApiEsotericraft {

	private static final List<IEsotericraftModule> modules =
			new ArrayList<IEsotericraftModule>();
	private static final ModuleTrigger.MapTrigger<IEsotericraftModule> moduleMap =
			new ModuleTrigger.MapTrigger<IEsotericraftModule>();

	public static void registerModule(IEsotericraftModule module, ModuleTrigger... triggers) {
		ApiEsotericraft.modules.add(module);
		for (ModuleTrigger trigger : triggers) ApiEsotericraft.moduleMap.add(trigger, module);
	}

	public static IEsotericraftModule getModuleForTrigger(ItemStack trigger) {
		return ApiEsotericraft.moduleMap.getValueFromTriggerStack(trigger);
	}

	public static int getID(IEsotericraftModule module) {
		return ApiEsotericraft.modules.indexOf(module);
	}

	public static IEsotericraftModule getModule(int index) {
		return (index < 0 || index >= ApiEsotericraft.modules.size()) ? null
				: ApiEsotericraft.modules.get(index);
	}

}
