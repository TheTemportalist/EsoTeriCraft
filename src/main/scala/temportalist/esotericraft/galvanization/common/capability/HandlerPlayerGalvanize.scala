package temportalist.esotericraft.galvanization.common.capability

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EntityDamageSource
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider}
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import temportalist.esotericraft.galvanization.common.Galvanize
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

	@SubscribeEvent
	def onEntityKilled(event: LivingDeathEvent): Unit = {
		event.getSource match {
			case entityDS: EntityDamageSource =>
				entityDS.getEntity match {
					case player: EntityPlayer =>
						this.onEntityKilledByPlayer(event.getEntityLiving, player)
					case _ =>
				}
			case _ =>
		}
	}

	def onEntityKilledByPlayer(target: EntityLivingBase, player: EntityPlayer): Unit = {
		HelperGalvanize.get(player) match {
			case galvanized: IPlayerGalvanize => galvanized.addModelEntity(target)
			case _ =>
		}
	}

	@SubscribeEvent
	def onEntityJoinWorld2(event: EntityJoinWorldEvent): Unit = {
		if (event.getWorld.isRemote) return

		val entity = event.getEntity
		if (this.isValid(entity)) {
			val data = this.get(entity.asInstanceOf[EntityPlayer]).serializeNBT()
			new PacketExtendedSync(
				entity.getEntityId, data
			).sendToDimension(Galvanize, event.getWorld.provider.getDimension)
		}

	}

}
