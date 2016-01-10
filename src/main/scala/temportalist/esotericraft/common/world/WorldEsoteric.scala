package temportalist.esotericraft.common.world

import net.minecraft.world.biome.BiomeGenBase
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.origin.foundation.common.register.{Registry, Register}

/**
  * Created by TheTemportalist on 1/3/2016.
  */
object WorldEsoteric extends Register.Post {

	var biomeEsoteric: BiomeGenBase = null
	var biomeUmbra: BiomeGenBase = null

	override def register(): Unit = {
		this.biomeEsoteric = new BiomeGenEsoteric().setBiomeName("Esoteric")
		this.biomeUmbra = new BiomeGenUmbra().setBiomeName("Umbra")
		GameRegistry.registerWorldGenerator(WorldGenEsoteric, -999)
		Registry.registerHandler(WorldGenEsoteric)

	}

}
