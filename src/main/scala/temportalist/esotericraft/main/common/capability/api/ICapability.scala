package temportalist.esotericraft.main.common.capability.api

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

	def getObjectAs(obj: AnyRef): T = obj.asInstanceOf[T]

	def init(world: World, t: T): Unit

	def getNew: N

	def serializeNBT: N = {
		val nbt = this.getNew
		this.writeToNBT(nbt)
		nbt
	}

	def writeToNBT(nbt: N): Unit = {}

	def deserializeNBT(nbt: N): Unit = {
		this.readFromNBT(nbt)
	}

	def readFromNBT(nbt: N): Unit = {}

}
