package temportalist.enhancing.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraftforge.fml.common.Loader
import temportalist.enhancing.api.{ApiEsotericEnhancing, Enhancement}
import temportalist.esotericraft.common.EsotericNBT
import temportalist.origin.api.common.utility.NBTHelper

import scala.collection.mutable.ListBuffer

/**
  * Created by TheTemportalist on 12/31/2015.
  */
object EnhancingNBT extends EsotericNBT {

	final val ENHANCEMENTS_TAG_KEY = "enhancements"

	override def checkEsotericNBT(stack: ItemStack): Boolean = {
		if (super.checkEsotericNBT(stack) &&
				stack.getTagCompound.getTag(EsotericNBT.ESOTERIC_TAG_KEY).
						asInstanceOf[NBTTagCompound].hasKey(EnhancingNBT.ENHANCEMENTS_TAG_KEY)) {
			this.getEsotericTag(stack).setTag(EnhancingNBT.ENHANCEMENTS_TAG_KEY, new NBTTagList)
			true
		}
		else false
	}

	def isEnhanced(stack: ItemStack): Boolean = {
		if (this.checkEsotericNBT(stack)) {
			val tag = this.getEnhancementsTag(stack)
			tag != null && tag.tagCount() > 0
		} else false
	}

	def getEnhancementsTag(stack: ItemStack): NBTTagList = {
		if (this.checkEsotericNBT(stack))
			this.getEsotericTag(stack).getTagList(
				EnhancingNBT.ENHANCEMENTS_TAG_KEY, NBTHelper.getNBTType[NBTTagCompound])
		else null
	}

	def setEnhancementsTag(stack: ItemStack, tagList: NBTTagList): Unit = {
		if (tagList.getTagType != 0 && tagList.getTagType != NBTHelper.getNBTType[NBTTagCompound]) return
		if (this.checkEsotericNBT(stack))
			this.getEsotericTag(stack).setTag(EnhancingNBT.ENHANCEMENTS_TAG_KEY, tagList)
	}

	def getEnhancements(stack: ItemStack): Array[(Enhancement, Float)] = {
		val ret = ListBuffer[(Enhancement, Float)]()
		val tagList = this.getEnhancementsTag(stack)
		if (tagList != null) for (i <- 0 until tagList.tagCount()) {
			val tagCom = tagList.getCompoundTagAt(i)
			val enhancement = ApiEsotericEnhancing.getEnhancement(tagCom.getInteger("globalID"))
			if (enhancement != null) ret += ((enhancement, tagCom.getFloat("power")))
		}
		ret.toArray
	}

	def enhance(stack: ItemStack, enhancement: Enhancement, power: Float): Unit = {
		var enhancements = this.getEnhancementsTag(stack)
		if (enhancements != null) enhancements = new NBTTagList
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
		if (Loader.isModLoaded("Baubles")) { //ModAPIManager.INSTANCE.hasAPI("Baubles|API")) {
			val inv = baubles.api.BaublesApi.getBaubles(player)
			if (inv != null)
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

	def hasEnhancement(stack: ItemStack, enhancement: Enhancement): Boolean = {
		val targetID = enhancement.getGlobalID
		val tagList = this.getEnhancementsTag(stack)
		if (tagList != null) for (i <- 0 until tagList.tagCount()) {
			val tagCom = tagList.getCompoundTagAt(i)
			if (tagCom.getInteger("globalID") == targetID) return true
		}
		false
	}

	def hasEnhancement(player: EntityPlayer, enhancement: Enhancement): Float = {
		val targetID = enhancement.getGlobalID
		this.getAllEnhancements(player).foreach(enhancementPower => {
			if (enhancementPower._1.getGlobalID == targetID) return enhancementPower._2
		})
		-1f
	}

	def getPower(stack: ItemStack, enhancement: Enhancement): Float = {
		val targetID = enhancement.getGlobalID
		val tagList = this.getEnhancementsTag(stack)
		if (tagList != null) for (i <- 0 until tagList.tagCount()) {
			val tagCom = tagList.getCompoundTagAt(i)
			if (tagCom.getInteger("globalID") == targetID) return tagCom.getFloat("power")
		}
		-1f
	}

	def foreachEnhancement[U](player: EntityPlayer, f: (Enhancement, Float) => U): Unit = {
		EnhancingNBT.getAllEnhancements(player).foreach(enhancementPower => {
			f(enhancementPower._1, enhancementPower._2)
		})
	}

}
