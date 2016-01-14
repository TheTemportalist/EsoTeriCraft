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

	private static final List<EsotericModule> modules =
			new ArrayList<EsotericModule>();
	private static final ModuleTrigger.MapTrigger<EsotericModule> moduleMap =
			new ModuleTrigger.MapTrigger<EsotericModule>();

	public static final String KEY_EXTENDED = "esoteric";
	public static final String KEY_ESOTERICRAFT_MODULES = "modules";

	public static void registerModule(EsotericModule module) {
		ApiEsotericraft.modules.add(module);
		for (ModuleTrigger trigger : module.createTriggers())
			ApiEsotericraft.moduleMap.add(trigger, module);
	}

	public static EsotericModule getModuleForTrigger(Object trigger) {
		return ApiEsotericraft.moduleMap.getValueFromTriggerStack(trigger);
	}

	public static int getID(EsotericModule module) {
		return ApiEsotericraft.modules.indexOf(module);
	}

	public static EsotericModule getModule(int index) {
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

		public static void impart(EntityPlayer player, EsotericModule module) {
			IEsotericPlayer esoteric = Player.getProps(player);
			if (esoteric != null && esoteric.canImpart(module)) esoteric.impart(module);
		}

		public static boolean hasKnowledgeOf(EntityPlayer player, EsotericModule module) {
			IEsotericPlayer esoteric = Player.getProps(player);
			return esoteric != null && esoteric.hasKnowledgeOf(module);
		}

	}

	public static class Spells {

		private static final List<Spell> spells = new ArrayList<Spell>();

		public static void register(Spell spell) {
			if (Spells.spells.contains(spell)) throw new IllegalArgumentException(
					"Spell of class " + spell.getClass().getCanonicalName() + " with name " +
							spell.getName() + " is already registered.");
			Spells.spells.add(spell);
		}

		public static int getGlobalID(Spell spell) {
			return Spells.spells.indexOf(spell);
		}

		public static Spell getSpell(int globalID) {
			return globalID < 0 ? null : Spells.spells.get(globalID);
		}

		public static boolean shouldSwitchSpell(EntityPlayer player) {
			return player.isSneaking() &&
					Spells.isValidForSpellCasting(player.getCurrentEquippedItem());
		}

		public static boolean isValidForSpellCasting(ItemStack stack) {
			return stack == null || stack.getItem() instanceof ISpellCastingItem;
		}

		public static void switchSpell(EntityPlayer player, boolean increment) {
			IEsotericPlayer esoteric = Player.getProps(player);
			if (esoteric != null) esoteric.switchSpell(increment);
		}

	}

}
