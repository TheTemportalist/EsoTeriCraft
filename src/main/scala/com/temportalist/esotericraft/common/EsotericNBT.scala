package com.temportalist.esotericraft.common

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
  * Created by TheTemportalist on 12/31/2015.
  */
class EsotericNBT {

	def checkEsotericNBT(stack: ItemStack): Boolean = {
		if (stack == null) return false
		if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound)
		if (!stack.getTagCompound.hasKey(EsotericNBT.ESOTERIC_TAG_KEY))
			stack.getTagCompound.setTag(EsotericNBT.ESOTERIC_TAG_KEY, new NBTTagCompound)
		true
	}

	final def getEsotericTag(stack: ItemStack): NBTTagCompound = {
		if (this.checkEsotericNBT(stack)) {
			stack.getTagCompound.getTag(EsotericNBT.ESOTERIC_TAG_KEY).asInstanceOf[NBTTagCompound]
		}
		null
	}

}
object EsotericNBT {
	final val INSTANCE = new EsotericNBT
	final val ESOTERIC_TAG_KEY = "esoteric"
}
