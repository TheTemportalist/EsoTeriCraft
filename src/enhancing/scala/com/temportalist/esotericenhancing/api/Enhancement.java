package com.temportalist.esotericenhancing.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by TheTemportalist on 12/31/2015.
 */
public class Enhancement {

	private final String identifier;

	public Enhancement(String name) {
		this.identifier = name;

		EnhancingAPI.register(this);
	}

	public final String getName() {
		return this.identifier;
	}


	public final  int getGlobalID() {
		return EnhancingAPI.getGlobalID(this);
	}

	public float computePower(float[] powers) {
		return 0;
	}

	public void onPlayerTick(EntityPlayer player, float power) {}

	public void onPlayerAttacking(EntityPlayer player, Entity entity, float power) {}

	public void onPlayerAttacked(EntityPlayer player, Entity entity, float power) {}

	public void onEquipChange(boolean isEquipped) {}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Enhancement && this.getGlobalID() == ((Enhancement)obj).getGlobalID();
	}

	public static class Additive extends Enhancement {

		public Additive(String name) {
			super(name);
		}

		@Override
		public float computePower(float[] powers) {
			float sum = 0f;
			for (float power : powers) sum += power;
			return sum;
		}

	}

}
