package com.temportalist.esotericenhancing.api;

import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;

/**
 * Created by TheTemportalist on 12/31/2015.
 */
public class EnhancingAPI {

	private static final HashMap<Enhancement, Integer> objectToID =
			new HashMap<Enhancement, Integer>();
	private static final HashMap<Integer, Enhancement> idToObject =
			new HashMap<Integer, Enhancement>();

	public static void register(Enhancement enhancement) {
		int id = EnhancingAPI.objectToID.size();
		EnhancingAPI.objectToID.put(enhancement, id);
		EnhancingAPI.idToObject.put(id, enhancement);
		MinecraftForge.EVENT_BUS.register(enhancement);
	}

	public static int getGlobalID(Enhancement enhancement) {
		if (!EnhancingAPI.objectToID.containsKey(enhancement)) return -1;
		return EnhancingAPI.objectToID.get(enhancement);
	}

	public static Enhancement getEnhancement(int globalID) {
		return EnhancingAPI.idToObject.get(globalID);
	}

}
