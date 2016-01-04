package com.temportalist.esotericraft.common.world

import java.awt.Color

import net.minecraft.world.biome.BiomeGenBase
import net.minecraftforge.fml.relauncher.{SideOnly, Side}

/**
  * Created by TheTemportalist on 1/3/2016.
  */
class BiomeGenEsoteric(id: Int) extends BiomeGenBase(id) {

	this.setTemperatureRainfall(0.8F, 0.4F)

	private def brighter(original: Int): Int = new Color(original).brighter().getRGB

	@SideOnly(Side.CLIENT)
	override def getSkyColorByTemp(temp : Float): Int = brighter(super.getSkyColorByTemp(temp))

	override def getWaterColorMultiplier: Int = brighter(16777215)

	override def getModdedBiomeFoliageColor(original: Int): Int = brighter(original)

	override def getModdedBiomeGrassColor(original: Int): Int = brighter(original)

}
