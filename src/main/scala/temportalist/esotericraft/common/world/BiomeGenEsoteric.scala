package temportalist.esotericraft.common.world

import java.awt.Color
import net.minecraft.world.biome.BiomeGenBase
import net.minecraftforge.fml.relauncher.{SideOnly, Side}
import temportalist.esotericraft.common.ModOptions

/**
  * Created by TheTemportalist on 1/3/2016.
  */
class BiomeGenEsoteric() extends BiomeGenBase(ModOptions.biomeEsotericID) {

	this.setTemperatureRainfall(0.8F, 0.4F)
	this.spawnableCaveCreatureList.clear()
	this.spawnableCreatureList.clear()
	this.spawnableMonsterList.clear()
	this.spawnableWaterCreatureList.clear()

	private def alter(original: Int): Int = new Color(original).brighter().getRGB

	@SideOnly(Side.CLIENT)
	override def getSkyColorByTemp(temp : Float): Int = alter(super.getSkyColorByTemp(temp))

	override def getWaterColorMultiplier: Int = alter(16777215)

	override def getModdedBiomeFoliageColor(original: Int): Int = alter(original)

	override def getModdedBiomeGrassColor(original: Int): Int = alter(original)

}
