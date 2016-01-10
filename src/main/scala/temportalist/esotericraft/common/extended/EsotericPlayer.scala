package temportalist.esotericraft.common.extended

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.{NBTTagCompound, NBTTagInt, NBTTagList}
import net.minecraftforge.fml.relauncher.Side
import temportalist.esotericraft.api.{EsotericraftModule, IEsotericPlayer}
import temportalist.esotericraft.common.EsoTeriCraft
import temportalist.origin.api.common.utility.{NBTHelper, WorldHelper}
import temportalist.origin.foundation.common.extended.ExtendedEntity
import temportalist.origin.foundation.common.network.PacketExtendedSync

import scala.collection.mutable.ListBuffer

/**
  * Created by TheTemportalist on 1/6/2016.
  */
class EsotericPlayer(p: EntityPlayer) extends ExtendedEntity(p) with IEsotericPlayer {

	private val impartedModules = ListBuffer[Int]()

	override def impart(module: EsotericraftModule): Unit = {
		if (this.canImpart(module)) this.impartedModules += module.getID
		if (WorldHelper.isServer) this.syncEntity("impartedModules", this.impartedModules.toArray)
	}

	override def canImpart(module: EsotericraftModule): Boolean = !this.hasKnowledgeOf(module)

	override def hasKnowledgeOf(module: EsotericraftModule): Boolean = {
		this.impartedModules contains module.getID
	}

	// ~~~~~~~~~~~ Handling NBT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def saveNBTData(tagCom: NBTTagCompound): Unit = {
		val tag_impartedModules = new NBTTagList
		this.impartedModules.foreach(id => tag_impartedModules.appendTag(new NBTTagInt(id)))
		EsoTeriCraft.log("Save ETC Player: " + tag_impartedModules.toString)
		tagCom.setTag("impartedModules", tag_impartedModules)

	}

	override def loadNBTData(tagCom: NBTTagCompound): Unit = {
		this.impartedModules.clear()
		val tag_impartedModules = tagCom.getTagList("impartedModules", NBTHelper.getNBTType[Int])
		EsoTeriCraft.log("Load ETC Player: " + tag_impartedModules.toString)
		for (i <- 0 until tag_impartedModules.tagCount())
			this.impartedModules += tag_impartedModules.get(i).asInstanceOf[NBTTagInt].getInt

	}

	// ~~~~~~~~~~~ Entity Syncing ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def handleSyncPacketData(uniqueIdentifier: String, packet: PacketExtendedSync,
			side: Side): Unit = {
		uniqueIdentifier match {
			case "impartedModules" =>
				this.impartedModules.clear()
				this.impartedModules ++= packet.get[Array[Int]]
			case _ =>
		}
	}

}
