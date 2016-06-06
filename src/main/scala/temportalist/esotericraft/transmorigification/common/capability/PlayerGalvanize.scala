package temportalist.esotericraft.transmorigification.common.capability

import java.util

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.emulation.common.{EntityState, EntityType, IEntityEmulator}
import temportalist.esotericraft.transmorigification.common.Transform
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
) extends IPlayerGalvanize // General interface for API
		with IExtendedEntitySync[NBTTagCompound, EntityPlayer] // Provides easy syncing
		with IEntityEmulator // Provides morph body functionality
{

	// ~~~~~~~~~~ NBT Serializable

	override def serializeNBT(): NBTTagCompound = {

		val nbt = new NBTTagCompound

		if (this.getEntityName != null)
			nbt.setString("entity_name", this.getEntityName)
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

		if (nbt.hasKey("addModelEntity_entityState"))
			this.availableEntityStates +=
					new EntityState(nbt.getCompoundTag("addModelEntity_entityState"))
		if (nbt.hasKey("removeModelEntity_index"))
			this.removeModelEntity(nbt.getInteger("removeModelEntity_index"))

		if (nbt.hasKey("entity_data")) {
			val data = nbt.getCompoundTag("entity_data")
			if (data.hasNoTags) this.clearEntityState(this.getWorld)
			else {
				val state = new EntityState()
				state.deserializeNBT(data)
				this.setEntityState(state)
			}
			return
		}

		if (nbt.hasKey("entity_name")) {
			this.setEntityState(nbt.getString("entity_name"), this.getWorld)
		}

		if (nbt.hasKey("entity_state")) {
			var entityState: EntityState = null
			entityState = new EntityState(nbt.getCompoundTag("entity_state"))
			this.setEntityState(entityState)
		}

		if (nbt.hasKey("emulator"))
			this.deserializeNBTEmulator(nbt.getCompoundTag("emulator"))

		if (nbt.hasKey("entity_states")) {
			this.availableEntityStates.clear()
			val tagEntityStates = NBTHelper.getTagList[NBTTagCompound](nbt, "entity_states")
			for (i <- 0 until tagEntityStates.tagCount()) {
				this.availableEntityStates += new EntityState(tagEntityStates.getCompoundTagAt(i))
			}
		}

	}

	// ~~~~~~~~~~ IExtendedEntitySync

	override def getNetworkMod: NetworkMod = Transform

	// ~~~~~~~~~~ IEntityEmulator

	override def getSelfEntityInstance: EntityLivingBase = this.player

	override protected def syncEntityDataToClient(tag: NBTTagCompound): Unit = {
		this.sendNBTToClient(this.player, {
			val nbt = new NBTTagCompound
			nbt.setTag("entity_data", tag)
			nbt
		})
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

	override def removeModelEntity(index: Int): Unit = {
		this.availableEntityStates.remove(index)
		if (!this.getWorld.isRemote) {
			///*
			this.sendNBTToClient(this.player, {
				val ret = new NBTTagCompound
				ret.setInteger("removeModelEntity_index", index)
				ret
			})
			//*/
		}
	}

}
