package com.temportalist.esotericraft.common.world

import com.temportalist.esotericraft.common.ModOptions
import com.temportalist.origin.foundation.common.register.Register
import net.minecraft.world.biome.BiomeGenBase
import net.minecraftforge.fml.common.registry.GameRegistry

/**
  * Created by TheTemportalist on 1/3/2016.
  */
object WorldEsoteric extends Register.Unusual {

	var biomeEsoteric: BiomeGenBase = null

	override def register(): Unit = {
		GameRegistry.registerWorldGenerator(WorldGenEsoteric, 0)
		this.biomeEsoteric = new BiomeGenEsoteric(ModOptions.biomeEsotericID)

	}

}
