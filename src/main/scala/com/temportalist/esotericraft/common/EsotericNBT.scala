package com.temportalist.esotericraft.common

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
  * Created by TheTemportalist on 12/31/2015.
  */
class EsotericNBT {

	def checkEsotericNBT(stack: ItemStack): Unit = {
		if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound)
		if (!stack.getTagCompound.hasKey(EsotericNBT.ESOTERIC_TAG_KEY))
			stack.getTagCompound.setTag(EsotericNBT.ESOTERIC_TAG_KEY, new NBTTagCompound)
	}

	final def getEsotericTag(stack: ItemStack): NBTTagCompound = {
		stack.getTagCompound.getTag(EsotericNBT.ESOTERIC_TAG_KEY).asInstanceOf[NBTTagCompound]
	}

}
object EsotericNBT {
	final val INSTANCE = new EsotericNBT
	final val ESOTERIC_TAG_KEY = "esoteric"
}
