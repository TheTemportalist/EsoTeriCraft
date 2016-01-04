package com.temportalist.esotericraft.common.world

import com.temportalist.esotericraft.common.ModOptions
import com.temportalist.origin.foundation.common.register.{Register, Registry}
import net.minecraft.world.biome.BiomeGenBase
import net.minecraftforge.fml.common.registry.GameRegistry

/**
  * Created by TheTemportalist on 1/3/2016.
  */
object WorldEsoteric extends Register.Post {

	var biomeEsoteric: BiomeGenBase = null

	override def register(): Unit = {
		this.biomeEsoteric = new BiomeGenEsoteric(ModOptions.biomeEsotericID)
		GameRegistry.registerWorldGenerator(WorldGenEsoteric, -999)
		Registry.registerHandler(WorldGenEsoteric)

	}

}
