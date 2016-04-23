package temportalist.esotericraft.main.common.capability.api

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.Capability.IStorage

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
class StorageBlank[T] extends IStorage[T] {

	override def writeNBT(capability: Capability[T], t: T, enumFacing: EnumFacing): NBTBase = null

	override def readNBT(capability: Capability[T], t: T,
			enumFacing: EnumFacing, nbtBase: NBTBase): Unit = {}

}
