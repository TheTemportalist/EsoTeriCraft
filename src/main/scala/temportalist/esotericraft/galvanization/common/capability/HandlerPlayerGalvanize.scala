package temportalist.esotericraft.galvanization.common.capability

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider}
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.entity.EntityEmpty
import temportalist.origin.foundation.common.capability.ExtendedHandler.ExtendedEntity
import temportalist.origin.foundation.common.network.PacketExtendedSync

/**
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
object HandlerPlayerGalvanize
		extends ExtendedEntity[NBTTagCompound, IPlayerGalvanize, PlayerGalvanize, EntityPlayer](
			classOf[IPlayerGalvanize], classOf[PlayerGalvanize], classOf[EntityPlayer]
		) {

	override def isValid(e: ICapabilityProvider): Boolean = e.isInstanceOf[EntityPlayer]

	override def cast(e: ICapabilityProvider): EntityPlayer = e.asInstanceOf[EntityPlayer]

	/**
	  * @return the @CapabilityInject object (which should have passed the class of [[getInterfaceClass]])
	  */
	override def getCapabilityObject: Capability[IPlayerGalvanize] = HelperGalvanize.getCapabilityObject

	/**
	  * @return the implementation of [[getInterfaceClass]]
	  */
	override def getDefaultImplementation: IPlayerGalvanize = null

	override def getNewImplementation(obj: EntityPlayer): IPlayerGalvanize = new PlayerGalvanize(obj)

	override def getPacketHandlingClass: Class[_ <: Handler] = classOf[Handler]

	class Handler extends PacketExtendedSync.Handler {
		override protected def deserialize(entity: Entity, nbt: NBTTagCompound): Unit = {
			entity match {
				case player: EntityPlayer =>
					player.getCapability(getCapabilityObject, null).deserializeNBT(nbt)
				case _ =>
			}
		}
	}

}
