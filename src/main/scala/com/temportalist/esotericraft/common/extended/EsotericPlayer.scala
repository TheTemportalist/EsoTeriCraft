package com.temportalist.esotericraft.common.extended

import com.temportalist.origin.foundation.common.extended.ExtendedEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound

/**
  * Created by TheTemportalist on 1/6/2016.
  */
class EsotericPlayer(p: EntityPlayer) extends ExtendedEntity(p) {

	override def saveNBTData(tagCom: NBTTagCompound): Unit = {

	}

	override def loadNBTData(tagCom: NBTTagCompound): Unit = {

	}

}
object EsotericPlayer {
	val EXTENDED_KEY = "esoteric"
}
