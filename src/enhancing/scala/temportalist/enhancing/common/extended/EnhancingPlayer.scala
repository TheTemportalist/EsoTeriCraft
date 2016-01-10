package temportalist.enhancing.common.extended

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import temportalist.enhancing.api.{ApiEsotericEnhancing, IEnhancingPlayer}
import temportalist.enhancing.common.enhancement.EnhancementWrapper
import temportalist.origin.foundation.common.extended.ExtendedEntity

import scala.collection.JavaConversions
import scala.collection.mutable.ListBuffer

/**
  * Created by TheTemportalist on 1/4/2016.
  */
class EnhancingPlayer(p: EntityPlayer) extends ExtendedEntity(p) with IEnhancingPlayer {

	def getValidEnhancementsFor(stack: ItemStack): Array[EnhancementWrapper] = {
		// TODO make enhancements discoverable
		val ret = ListBuffer[EnhancementWrapper]()
		JavaConversions.collectionAsScalaIterable(
			ApiEsotericEnhancing.getAllEnhancements).foreach(enhancement => {
				ret += new EnhancementWrapper(enhancement)
		})
		ret.toArray
	}

	override def saveNBTData(tagCom: NBTTagCompound): Unit = {

	}

	override def loadNBTData(tagCom: NBTTagCompound): Unit = {

	}

}
