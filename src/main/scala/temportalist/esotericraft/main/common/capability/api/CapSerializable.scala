package temportalist.esotericraft.main.common.capability.api

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, ICapabilitySerializable}

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
class CapSerializable[N <: NBTBase, C <: ICapability[_, N]](
		private val injected: Capability[C], private val cap: C
) extends ICapabilitySerializable[N] {

	override def serializeNBT(): N = cap.serializeNBT

	override def deserializeNBT(nbt: N): Unit = cap.deserializeNBT(nbt)

	override def hasCapability(capability: Capability[_], enumFacing: EnumFacing): Boolean = {
		capability == this.injected
	}

	override def getCapability[T](capability: Capability[T], enumFacing: EnumFacing): T = {
		if (this.hasCapability(capability, enumFacing)) injected.cast(this.cap)
		else null.asInstanceOf[T]
	}

}
