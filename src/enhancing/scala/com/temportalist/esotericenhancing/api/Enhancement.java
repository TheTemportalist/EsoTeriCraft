package com.temportalist.esotericenhancing.api;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by TheTemportalist on 12/31/2015.
 */
public class Enhancement {

	private final String identifier;
	private final float powerConstant;

	public Enhancement(String name, float powerBase) {
		this.identifier = name;
		this.powerConstant = powerBase;

		EnhancingAPI.register(this);
	}

	public Enhancement(String name) {
		this(name, 0);
	}

	public final String getName() {
		return this.identifier;
	}

	public final float getPower() {
		return this.powerConstant;
	}

	public final  int getGlobalID() {
		return EnhancingAPI.getGlobalID(this);
	}

	public float computePower(float[] powers) {
		return this.powerConstant;
	}

	public void onPlayerTick(EntityPlayer player, float power) {}

	public void onEquipChange(boolean isEquipped) {}



}
