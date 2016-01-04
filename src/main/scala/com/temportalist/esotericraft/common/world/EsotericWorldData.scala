package com.temportalist.esotericraft.common.world

import com.temportalist.esotericraft.common.EsoTeriCraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.storage.MapStorage
import net.minecraft.world.{World, WorldSavedData}

/**
  * Created by TheTemportalist on 1/3/2016.
  */
class EsotericWorldData(str: String) extends WorldSavedData(str) {

	override def writeToNBT(nbt: NBTTagCompound): Unit = {
		EsoTeriCraft.log("Saving Esoteric World Data")
		// ~~~~~ Save Biome/Structure data ~~~~~~~~~~~~~~~
		WorldGenEsoteric.writeEsotericChunks(nbt)

	}

	override def readFromNBT(nbt: NBTTagCompound): Unit = {
		EsoTeriCraft.log("Loading Esoteric World Data")
		// ~~~~~ Load Biome/Structure data ~~~~~~~~~~~~~~~
		WorldGenEsoteric.readEsotericChunks(nbt)

	}

}
object EsotericWorldData {

	val MAPPING_KEY = "esoteric_data"

	def forWorld(world: World): WorldSavedData = {
		if (world.provider.getDimensionId != 0) return null
		val storage: MapStorage = world.getPerWorldStorage
		var data = storage.loadData(classOf[EsotericWorldData], EsotericWorldData.MAPPING_KEY)
		if (data == null) {
			EsoTeriCraft.log("Adding esoteric world data")
			data = new EsotericWorldData(EsotericWorldData.MAPPING_KEY)
			storage.setData(EsotericWorldData.MAPPING_KEY, data)
		}
		data
	}

}
