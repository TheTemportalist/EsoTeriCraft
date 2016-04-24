package temportalist.esotericraft.main.common.capability.api

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.world.World

/**
  *
  * @tparam T The class type this capability is attached to (EntityPlayer for players)
  * @tparam N the NBT type this capability saves data as
  *
  * Created by TheTemportalist on 4/23/2016.
  * @author TheTemportalist
  */
trait ICapability[T, N <: NBTBase] {

	// ~~~~~~~~~~ Capability ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private var owner: T = _

	def initEntity(world: World, t: T): Unit = {
		this.owner = t
	}

	def initItem(t: T, stack: ItemStack): Unit = {
		this.owner = t
	}

	final def getOwner: T = this.owner

	// ~~~~~~~~~~ NBT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def getNewNBT: N

	def serializeNBT: N = {
		val nbt = this.getNewNBT
		this.writeToNBT(nbt)
		nbt
	}

	def writeToNBT(nbt: N): Unit = {}

	def deserializeNBT(nbt: N): Unit = {
		this.readFromNBT(nbt)
	}

	def readFromNBT(nbt: N): Unit = {}

	// ~~~~~~~~~~ Packets ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final def markDirtyInit(): Unit = this.markDirty(EnumDirty.INIT, null, null)

	final def markDirtyData(getNBT: (Any*) => NBTBase, data: Any*): Unit =
		this.markDirty(EnumDirty.DATA, getNBT, data:_*)

	final def markDirty(state: EnumDirty, getNBT: (Any*) => NBTBase, data: Any*): Unit = {
		state match {
			case EnumDirty.INIT => this.sendNBTToClient(this.serializeNBT)
			case EnumDirty.DATA => this.sendNBTToClient(getNBT(data:_*))
		}
	}

	def sendNBTToClient(nbt: NBTBase): Unit

}
