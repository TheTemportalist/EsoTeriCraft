package com.temportalist.esotericraft.api;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TheTemportalist on 1/6/2016.
 */
public final class ModuleTrigger {

	private final ItemStack triggerStack;
	private final boolean detectMeta, detectNBT;

	ModuleTrigger(ItemStack trigger, boolean metaSensitive, boolean nbtSensitive) {
		if (trigger == null) throw new IllegalArgumentException("Trigger cannot be null.");
		this.triggerStack = trigger;
		this.detectMeta = metaSensitive;
		this.detectNBT = nbtSensitive;
	}

	public Item getItem() {
		return this.triggerStack.getItem();
	}

	public boolean doesStackMatch(ItemStack stack) {
		if (stack == null) return false;
		boolean matchItem = this.triggerStack.getItem().equals(stack.getItem());
		boolean matchMeta = !this.detectMeta ||
				this.triggerStack.getItemDamage() == stack.getItemDamage();
		boolean matchNBT = !this.detectNBT ||
				ItemStack.areItemStackTagsEqual(this.triggerStack, stack);
		return matchItem && matchMeta && matchNBT;
	}

	public static ModuleTrigger create(ItemStack triggerStack,
			boolean compareDamage, boolean compareNBT) {
		return new ModuleTrigger(triggerStack, compareDamage, compareNBT);
	}

	public static ModuleTrigger create(Item item, boolean compareDamage, boolean compareNBT) {
		return ModuleTrigger.create(new ItemStack(item), compareDamage, compareNBT);
	}

	public static ModuleTrigger create(Block block, boolean compareDamage) {
		return ModuleTrigger.create(Item.getItemFromBlock(block), compareDamage, false);
	}

	public static ModuleTrigger create(IBlockState state, boolean compareStates) {
		return ModuleTrigger.create(
				new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)),
				compareStates, false);
	}

	public static final class MapTrigger<T> {

		private final Map<Item, Map<ModuleTrigger, T>> itemTriggerMap;

		public MapTrigger() {
			this.itemTriggerMap = new HashMap<Item, Map<ModuleTrigger, T>>();
		}

		public void add(ModuleTrigger trigger, T value) {
			if (!this.itemTriggerMap.containsKey(trigger.getItem()))
				this.itemTriggerMap.put(trigger.getItem(), new HashMap<ModuleTrigger, T>());
			this.itemTriggerMap.get(trigger.getItem()).put(trigger, value);
		}

		public T getValueFromTriggerStack(ItemStack stack) {
			if (stack == null || !this.itemTriggerMap.containsKey(stack.getItem())) return null;
			Map<ModuleTrigger, T> triggerMap = this.itemTriggerMap.get(stack.getItem());
			for (ModuleTrigger trigger : triggerMap.keySet()) {
				if (trigger.doesStackMatch(stack)) return triggerMap.get(trigger);
			}
			return null;
		}

	}

}
