package temportalist.esotericraft.sorcery.common.capability

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider}
import temportalist.esotericraft.api.sorcery.ApiSorcery
import temportalist.esotericraft.api.sorcery.ApiSorcery.ISorceryPlayer
import temportalist.origin.foundation.common.capability.ExtendedHandler.ExtendedEntity
import temportalist.origin.foundation.common.network.PacketExtendedSync

/**
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
object HandlerSorceryPlayer extends ExtendedEntity[NBTTagCompound, ISorceryPlayer, SorceryPlayer, EntityPlayer] (
	classOf[ApiSorcery.ISorceryPlayer], classOf[SorceryPlayer], classOf[EntityPlayer]
){

	override def isValid(e: ICapabilityProvider): Boolean = e.isInstanceOf[EntityPlayer]

	override def cast(e: ICapabilityProvider): EntityPlayer = e.asInstanceOf[EntityPlayer]

	/**
	  * @return the @CapabilityInject object (which should have passed the class of [[getInterfaceClass]])
	  */
	override def getCapabilityObject: Capability[ISorceryPlayer] = ApiSorcery.getCapabilityObject

	/**
	  * @return the implementation of [[getInterfaceClass]]
	  */
	override def getDefaultImplementation: ISorceryPlayer = null

	override def getNewImplementation(obj: EntityPlayer): ISorceryPlayer = new SorceryPlayer(obj)

	override def getPacketHandlingClass: Class[_ <: Handler] = classOf[Handler]

	class Handler extends PacketExtendedSync.Handler {
		override protected def deserialize(entity: Entity, nbt: NBTTagCompound): Unit =
			entity.getCapability(getCapabilityObject, null).deserializeNBT(nbt)
	}

}
