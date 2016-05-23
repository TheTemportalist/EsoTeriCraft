package temportalist.esotericraft.galvanization.common.capability

import java.util

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.{NBTTagCompound, NBTTagList, NBTTagString}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.entity.emulator.{EntityState, EntityType, IEntityEmulator}
import temportalist.origin.api.common.utility.NBTHelper
import temportalist.origin.foundation.common.capability.IExtendedEntitySync
import temportalist.origin.foundation.common.network.NetworkMod

import scala.collection.JavaConversions
import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
class PlayerGalvanize(
		private val player: EntityPlayer
) extends IPlayerGalvanize
		with IExtendedEntitySync[NBTTagCompound, EntityPlayer]
		with IEntityEmulator {

	// ~~~~~~~~~~ NBT Serializable

	override def serializeNBT(): NBTTagCompound = {
		Galvanize.log("serialize")

		val nbt = new NBTTagCompound

		if (this.getEntityName != null)
			nbt.setString("entityName", this.getEntityName)
		if (this.getEntityState != null)
			nbt.setTag("entity_state", this.getEntityState.serializeNBT())
		nbt.setTag("emulator", this.serializeNBTEmulator)

		val tagEntityStates = new NBTTagList
		for (entityState <- this.availableEntityStates) {
			tagEntityStates.appendTag(entityState.serializeNBT())
		}
		nbt.setTag("entity_states", tagEntityStates)

		nbt
	}

	override def deserializeNBT(nbt: NBTTagCompound): Unit = {

		Galvanize.log("Deserialize " + nbt.toString)

		if (nbt.hasKey("main")) {
			nbt.getTag("main") match {
				case tagStr: NBTTagString =>
					this.setEntityState(tagStr.getString, this.getWorld)
				case tagCom: NBTTagCompound =>
					if (tagCom.hasKey("addModelEntity_entityState"))
						this.availableEntityStates +=
								new EntityState(tagCom.getCompoundTag("addModelEntity_entityState"))
					else {
						this.deserializeNBT(tagCom)
					}
				case _ =>
			}
			return
		}

		if (nbt.hasKey("entityName")) {
			this.setEntityState(nbt.getString("entityName"), this.getWorld)
		}
		else this.setEntityName(null)

		var entityState: EntityState = null
		if (nbt.hasKey("entity_state")) {
			entityState = new EntityState(nbt.getCompoundTag("entity_state"))
		}
		this.setEntityState(entityState)

		this.deserializeNBTEmulator(nbt.getCompoundTag("emulator"))

		this.availableEntityStates.clear()
		val tagEntityStates = NBTHelper.getTagList[NBTTagCompound](nbt, "entity_states")
		for (i <- 0 until tagEntityStates.tagCount()) {
			this.availableEntityStates += new EntityState(tagEntityStates.getCompoundTagAt(i))
		}
		Galvanize.log("Loaded " + this.availableEntityStates.length + " states")

		// TODO
		/** NOTE
			Serialize and Deserialize are only EVER called on the server-side
		    Task: get the data loaded on the server-side to be sent to the client side to be loaded
		 */

	}

	// ~~~~~~~~~~ IExtendedEntitySync

	override def getNetworkMod: NetworkMod = Galvanize

	// ~~~~~~~~~~ IEntityEmulator

	override def getSelfEntityInstance: EntityLivingBase = this.player

	override protected def syncEntityNameToClient(name: String): Unit = {
		this.sendNBTToClient(this.player, new NBTTagString(name))
	}

	override def onEntityStateCleared(world: World): Unit = {
		this.player.eyeHeight = this.player.getDefaultEyeHeight
	}

	// ~~~~~~~~~~ IPlayerGalvanize

	def getWorld: World = this.player.getEntityWorld

	@SideOnly(Side.CLIENT)
	override def onTickClient(): Unit = this.onTickClient(this.getWorld)

	override def onTickServer(): Unit = this.onTickServer(this.getWorld)

	// ~~~~~~~~~~ Entity Type Storage

	private val availableEntityStates = ListBuffer[EntityState]()

	override def addModelEntity(entity: EntityLivingBase): Unit = {
		if (!this.getWorld.isRemote) {
			val state = new EntityState(EntityType.create(entity))
			this.availableEntityStates += state
			this.sendNBTToClient(this.player, {
				val ret = new NBTTagCompound
				ret.setTag("addModelEntity_entityState", state.serializeNBT())
				ret
			})
		}
	}

	override def getModelEntities: util.List[EntityState] =
		JavaConversions.seqAsJavaList(this.availableEntityStates)

}
