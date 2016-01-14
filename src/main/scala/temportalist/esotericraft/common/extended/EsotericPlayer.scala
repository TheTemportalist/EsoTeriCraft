package temportalist.esotericraft.common.extended

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.{NBTTagCompound, NBTTagInt, NBTTagList}
import net.minecraftforge.fml.relauncher.Side
import temportalist.esotericraft.api.{ApiEsotericraft, Spell, EsotericModule, IEsotericPlayer}
import temportalist.origin.api.common.utility.{NBTHelper, WorldHelper}
import temportalist.origin.foundation.common.extended.ExtendedEntity
import temportalist.origin.foundation.common.network.PacketExtendedSync

import scala.collection.mutable.ListBuffer

/**
  * Created by TheTemportalist on 1/6/2016.
  */
class EsotericPlayer(p: EntityPlayer) extends ExtendedEntity(p) with IEsotericPlayer {

	private val impartedModules = ListBuffer[Int]()

	override def impart(module: EsotericModule): Unit = {
		if (this.getEntity.getEntityWorld.isRemote) return
		if (this.canImpart(module)) this.impartedModules += module.getID
		if (WorldHelper.isServer) this.syncEntity("impartedModules", this.impartedModules.toArray)
	}

	override def canImpart(module: EsotericModule): Boolean = !this.hasKnowledgeOf(module)

	override def hasKnowledgeOf(module: EsotericModule): Boolean = {
		this.impartedModules contains module.getID
	}

	// ~~~~~~~~~~~ Spells (Generic) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	  * Length of the available hot bar slots
	  * (full length - 1, due to one being open for an empty slot)
	  */
	private val HOT_BAR_LENGTH = 8
	/**
	  * The current index in the hot bar (length of [HOT_BAR_LENGTH + 1])
	  * Default is (HOT_BAR_LENGTH / 2 + 1)
	  */
	private var currentSlot = this.HOT_BAR_LENGTH / 2 + 1
	private val hot_bar = new Array[Spell](this.HOT_BAR_LENGTH)

	override def switchSpell(increment: Boolean): Unit = {
		if (this.isClientCheck) return
		// increment/decrement
		this.currentSlot += (if (increment) 1 else -1)
		// this line makes sure it is at least non-negative
		// (always adding the length of the full hot bar)
		this.currentSlot += this.HOT_BAR_LENGTH + 1
		// this line makes sure it is not above the max by getting the remainder
		// (0%9 = 0, 8%9 = 8, 9%9 = 0, 15%9 = 6)
		this.currentSlot = this.currentSlot % (this.HOT_BAR_LENGTH + 1)
		this.syncEntity("currentSlot", this.currentSlot)
	}

	override def setSpell(index: Int, spell: Spell): Unit = {
		if (this.isClientCheck) return
		if (index < 0 || index >= this.HOT_BAR_LENGTH) return
		this.hot_bar(index) = spell
		this.syncEntity("hot bar", this.getHotBarAsIDs)
	}

	private def getHotBarAsIDs: Array[Int] =
		for (elem <- this.hot_bar) yield if (elem == null) -1 else elem.getGlobalID

	private def setHotBarFromIDs(array: Array[Int]): Unit =
		for (i <- array.indices) this.hot_bar(i) = ApiEsotericraft.Spells.getSpell(array(i))

	def getHotBar: Array[Spell] = this.hot_bar

	def getCurrent: Int = this.currentSlot

	// ~~~~~~~~~~~ Handling NBT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def writeNBT(tagCom: NBTTagCompound): Unit = {
		val tag_impartedModules = new NBTTagList
		this.impartedModules.foreach(id => tag_impartedModules.appendTag(new NBTTagInt(id)))
		tagCom.setTag("impartedModules", tag_impartedModules)

		tagCom.setInteger("currentSlot", this.currentSlot)
		tagCom.setIntArray("hot_bar", this.getHotBarAsIDs)

	}

	override def readNBT(tagCom: NBTTagCompound): Unit = {
		this.impartedModules.clear()
		val tag_impartedModules = tagCom.getTagList("impartedModules", NBTHelper.getNBTType[Int])
		for (i <- 0 until tag_impartedModules.tagCount())
			this.impartedModules += tag_impartedModules.get(i).asInstanceOf[NBTTagInt].getInt

		this.currentSlot = tagCom.getInteger("currentSlot")
		this.setHotBarFromIDs(tagCom.getIntArray("hot_bar"))

	}

	// ~~~~~~~~~~~ Entity Syncing ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def handleSyncPacketData(uniqueIdentifier: String, packet: PacketExtendedSync,
			side: Side): Unit = {
		uniqueIdentifier match {
			case "impartedModules" =>
				this.impartedModules.clear()
				this.impartedModules ++= packet.get[Array[Int]]
			case "currentSlot" => this.currentSlot = packet.get[Int]
			case "hot bar" => this.setHotBarFromIDs(packet.get[Array[Int]])
			case _ =>
		}
	}

}
