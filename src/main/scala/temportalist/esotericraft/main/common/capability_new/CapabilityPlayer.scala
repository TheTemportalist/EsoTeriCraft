package temportalist.esotericraft.main.common.capability_new

import net.minecraft.nbt.{NBTBase, NBTTagCompound}
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.Capability.IStorage
import temportalist.esotericraft.main.common.capability_new.api.CapabilityWrapper

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
class CapabilityPlayer {

}
object CapabilityPlayer extends CapabilityWrapper[CapabilityPlayer] {

	override def getCapability: Class[CapabilityPlayer] = classOf[CapabilityPlayer]

	override def getStorage: IStorage[CapabilityPlayer] = Storage

	override def getImplementation: CapabilityPlayer = null

	object Storage extends IStorage[CapabilityPlayer] {

		override def writeNBT(capability: Capability[CapabilityPlayer], t: CapabilityPlayer,
				enumFacing: EnumFacing): NBTBase = {
			new NBTTagCompound
		}

		override def readNBT(capability: Capability[CapabilityPlayer], t: CapabilityPlayer,
				enumFacing: EnumFacing, nbtBase: NBTBase): Unit = {

		}

	}

}
