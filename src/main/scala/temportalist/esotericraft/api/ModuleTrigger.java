package temportalist.esotericraft.api;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TheTemportalist on 1/6/2016.
 */
public final class ModuleTrigger {

	private ItemStack triggerStack;
	private IBlockState triggerState;
	private final boolean detectMeta, detectNBT;

	ModuleTrigger(ItemStack trigger, boolean metaSensitive, boolean nbtSensitive) {
		if (trigger == null) throw new IllegalArgumentException("Trigger cannot be null.");
		this.triggerStack = trigger;
		this.detectMeta = metaSensitive;
		this.detectNBT = nbtSensitive;
	}

	ModuleTrigger(IBlockState trigger, boolean propertySensitive, boolean unlistedSensitive) {
		if (trigger == null) throw new IllegalArgumentException("Trigger cannot be null.");
		this.triggerState = trigger;
		this.detectMeta = propertySensitive;
		this.detectNBT = unlistedSensitive;
	}

	public Object getKey() {
		return this.triggerStack != null ? this.triggerStack.getItem() : this.triggerState;
	}

	public boolean doesMatch(Object trigger) {
		return this.matchesType(trigger) &&
				((trigger instanceof ItemStack && this.doesMatch((ItemStack)trigger)) ||
						(trigger instanceof IBlockState && this.doesMatch((IBlockState)trigger)));
	}

	private boolean matchesType(Object trigger) {
		return (trigger instanceof ItemStack && this.triggerStack != null) ||
				(trigger instanceof IBlockState && this.triggerState != null);
	}

	private boolean doesMatch(ItemStack stack) {
		if (stack == null) return false;
		boolean matchItem = this.triggerStack.getItem().equals(stack.getItem());
		boolean matchMeta = !this.detectMeta ||
				this.triggerStack.getItemDamage() == stack.getItemDamage();
		boolean matchNBT = !this.detectNBT ||
				ItemStack.areItemStackTagsEqual(this.triggerStack, stack);
		return matchItem && matchMeta && matchNBT;
	}

	private boolean doesMatch(IBlockState state) {
		if (state == null) return false;
		boolean matchBlock = this.triggerState.getBlock() == state.getBlock();
		boolean matchMeta = !this.detectMeta ||
				this.doPropertiesMatch(this.triggerState.getProperties(), state.getProperties());
		boolean matchNBT = !this.detectNBT;
		if (matchNBT && this.triggerState instanceof IExtendedBlockState &&
				state instanceof IExtendedBlockState) {
			IExtendedBlockState triggerExt = (IExtendedBlockState)this.triggerState;
			IExtendedBlockState stateExt = (IExtendedBlockState)state;
			matchNBT = this.doPropertiesMatch_Un(
					triggerExt.getUnlistedProperties(), stateExt.getUnlistedProperties());
		}

		return matchBlock && matchMeta && matchNBT;
	}

	private boolean doPropertiesMatch(ImmutableMap<IProperty, Comparable> propsA,
			ImmutableMap<IProperty, Comparable> propsB) {
		List<IProperty> checked = new ArrayList<IProperty>();
		for (IProperty key : propsA.keySet()) {
			if (!checked.contains(key)) {
				if (!propsB.containsKey(key)) return false;
				else if (propsA.get(key) != propsB.get(key)) return false;
				checked.add(key);
			}
		}
		for (IProperty key : propsB.keySet()) {
			if (!checked.contains(key)) {
				if (!propsA.containsKey(key)) return false;
				else if (propsB.get(key) != propsA.get(key)) return false;
				//checked.add(key);
			}
		}
		return true;
	}

	private boolean doPropertiesMatch_Un(ImmutableMap<IUnlistedProperty<?>, Optional<?>> propsA,
			ImmutableMap<IUnlistedProperty<?>, Optional<?>> propsB) {
		List<IUnlistedProperty> checked = new ArrayList<IUnlistedProperty>();
		for (IUnlistedProperty key : propsA.keySet()) {
			if (!checked.contains(key)) {
				if (!propsB.containsKey(key)) return false;
				else if (propsA.get(key) != propsB.get(key)) return false;
				checked.add(key);
			}
		}
		for (IUnlistedProperty key : propsB.keySet()) {
			if (!checked.contains(key)) {
				if (!propsA.containsKey(key)) return false;
				else if (propsB.get(key) != propsA.get(key)) return false;
				//checked.add(key);
			}
		}
		return true;
	}

	public static ModuleTrigger create(ItemStack triggerStack,
			boolean compareDamage, boolean compareNBT) {
		return new ModuleTrigger(triggerStack, compareDamage, compareNBT);
	}

	public static ModuleTrigger create(Item item, boolean compareDamage, boolean compareNBT) {
		return ModuleTrigger.create(new ItemStack(item), compareDamage, compareNBT);
	}

	public static ModuleTrigger create(Block block, boolean compareListed, boolean compareUnlisted) {
		return ModuleTrigger.create(block.getBlockState().getBaseState(), compareListed, compareUnlisted);
	}

	public static ModuleTrigger create(IBlockState state, boolean compareListed, boolean compareUnlisted) {
		return new ModuleTrigger(state, compareListed, compareUnlisted);
	}

	public static final class MapTrigger<T> {

		private final Map<Object, Map<ModuleTrigger, T>> objectTriggerMap;

		public MapTrigger() {
			this.objectTriggerMap = new HashMap<Object, Map<ModuleTrigger, T>>();
		}

		public void add(ModuleTrigger trigger, T value) {
			if (!this.objectTriggerMap.containsKey(trigger.getKey()))
				this.objectTriggerMap.put(trigger.getKey(), new HashMap<ModuleTrigger, T>());
			this.objectTriggerMap.get(trigger.getKey()).put(trigger, value);
		}

		public Object getKey(Object trigger) {
			return (trigger instanceof ItemStack) ? ((ItemStack)trigger).getItem() : trigger;
		}

		public boolean containsKey(Object trigger) {
			return this.objectTriggerMap.containsKey(this.getKey(trigger));
		}

		public T getValueFromTriggerStack(Object trigger) {
			if (trigger == null || !this.containsKey(trigger)) return null;
			Map<ModuleTrigger, T> triggerMap = this.objectTriggerMap.get(this.getKey(trigger));
			for (ModuleTrigger moduleTrigger : triggerMap.keySet()) {
				if (moduleTrigger.doesMatch(trigger)) return triggerMap.get(moduleTrigger);
			}
			return null;
		}

	}

}
