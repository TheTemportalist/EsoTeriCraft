package com.temportalist.esotericenhancing.common

import com.temportalist.esotericenhancing.api.{Enhancement, EnhancingAPI}
import com.temportalist.esotericraft.common.EsotericNBT
import com.temportalist.origin.api.common.utility.NBTHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraftforge.fml.common.ModAPIManager

import scala.collection.mutable.ListBuffer

/**
  * Created by TheTemportalist on 12/31/2015.
  */
object EnhancingNBT extends EsotericNBT {

	final val ENHANCEMENTS_TAG_KEY = "enhancements"

	override def checkEsotericNBT(stack: ItemStack): Unit = {
		super.checkEsotericNBT(stack)
		if (!this.isEnhanced(stack))
			this.getEsotericTag(stack).setTag(EnhancingNBT.ENHANCEMENTS_TAG_KEY, new NBTTagList)
	}

	def isEnhanced(stack: ItemStack): Boolean = {
		this.getEsotericTag(stack).hasKey(EnhancingNBT.ENHANCEMENTS_TAG_KEY) &&
		this.getEnhancementsTag(stack).tagCount() > 0
	}

	def getEnhancementsTag(stack: ItemStack): NBTTagList = {
		this.checkEsotericNBT(stack)
		this.getEsotericTag(stack).getTagList(
			EnhancingNBT.ENHANCEMENTS_TAG_KEY, NBTHelper.getNBTType[NBTTagCompound])
	}

	def setEnhancementsTag(stack: ItemStack, tagList: NBTTagList): Unit = {
		if (tagList.getTagType != 0 && tagList.getTagType != NBTHelper.getNBTType[NBTTagCompound]) return
		this.checkEsotericNBT(stack)
		this.getEsotericTag(stack).setTag(EnhancingNBT.ENHANCEMENTS_TAG_KEY, tagList)
	}

	def getEnhancements(stack: ItemStack): Array[(Enhancement, Float)] = {
		val ret = ListBuffer[(Enhancement, Float)]()
		val tagList = this.getEnhancementsTag(stack)
		for (i <- 0 until tagList.tagCount()) {
			val tagCom = tagList.getCompoundTagAt(i)
			val enhancement = EnhancingAPI.getEnhancement(tagCom.getInteger("globalID"))
			if (enhancement != null) ret += ((enhancement, tagCom.getFloat("power")))
		}
		ret.toArray
	}

	def enhance(stack: ItemStack, enhancement: Enhancement, power: Float): Unit = {
		val enhancements = this.getEnhancementsTag(stack)
		enhancements.appendTag({
			val tagCom = new NBTTagCompound
			tagCom.setInteger("globalID", enhancement.getGlobalID)
			tagCom.setFloat("power", power)
			tagCom
		})
		this.setEnhancementsTag(stack, enhancements)
	}

	def getAllEnhancements(player: EntityPlayer): Array[(Enhancement, Float)] = {
		import scala.collection.mutable
		val enhancements = mutable.Map[Enhancement, ListBuffer[Float]]()

		// define method to add enhancements found in stack
		def appendEnhancements(stack: ItemStack): Unit = {
			if (stack != null && this.isEnhanced(stack))
				this.getEnhancements(stack).foreach(enhancementPower => {
					val powers = enhancements.getOrElse(enhancementPower._1, ListBuffer[Float]())
					powers += enhancementPower._2
					enhancements(enhancementPower._1) = powers
				})
		}

		// search for enhancements
		player.inventory.armorInventory.foreach(appendEnhancements)
		if (ModAPIManager.INSTANCE.hasAPI("Baubles|API")) {
			val inv = baubles.api.BaublesApi.getBaubles(player)
			for (i <- 0 until inv.getSizeInventory) appendEnhancements(inv.getStackInSlot(i))
		}

		// compute final power of enhancements
		val enhancementList = ListBuffer[(Enhancement, Float)]()
		enhancements.foreach(enhancementAndPowers => {
			enhancementList += ((enhancementAndPowers._1,
					enhancementAndPowers._1.computePower(enhancementAndPowers._2.toArray)))
		})

		// return
		enhancementList.toArray
	}

}
