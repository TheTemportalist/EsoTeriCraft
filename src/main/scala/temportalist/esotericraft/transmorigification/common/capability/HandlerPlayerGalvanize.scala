package temportalist.esotericraft.transmorigification.common.capability

import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EntityDamageSource
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider}
import net.minecraftforge.event.entity.living.{LivingDeathEvent, LivingHurtEvent}
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import temportalist.esotericraft.transmorigification.common.Transform
import temportalist.esotericraft.transmorigification.common.network.PacketUpdateClientModels
import temportalist.origin.foundation.common.capability.ExtendedHandler.ExtendedEntity
import temportalist.origin.foundation.common.network.PacketExtendedSync

import scala.collection.JavaConversions

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

	override def doesDataPersistDeath: Boolean = true

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

	override def onEntityJoinWorld(world: World, entity: Entity): Unit = {
		if (!world.isRemote) {
			entity match {
				case player: EntityPlayerMP =>
					this.sendOtherModelsToClient(world, player)
				case _ => // non MP, shouldnt happen if world is not remote (client)
			}
		}
	}

	@SubscribeEvent
	def onEntityHurt(event: LivingHurtEvent): Unit = {
		event.getEntityLiving match {
			case player: EntityPlayer =>
				HelperGalvanize.get(player) match {
					case galvanized: IPlayerGalvanize =>
						if (!galvanized.canTakeDamage(event.getSource))
							event.setCanceled(true)
					case _ =>
				}
			case _ =>
		}
	}

	def sendOtherModelsToClient(world: World, player: EntityPlayerMP): Unit = {
		val serverPlayerList = FMLCommonHandler.instance().getMinecraftServerInstance.getPlayerList
		val playerList = JavaConversions.asScalaBuffer(serverPlayerList.getPlayerList)

		val nbtOfGalvanized = new NBTTagCompound
		for (player <- playerList) {
			player match {
				case galvanized: IPlayerGalvanize =>
					nbtOfGalvanized.setTag(player.getName, {
						val tag = new NBTTagCompound
						tag.setLong("bits_least", player.getUniqueID.getLeastSignificantBits)
						tag.setLong("bits_most", player.getUniqueID.getMostSignificantBits)
						tag.setTag("galvanized_tag", galvanized.serializeNBT())
						tag
					})
				case _ =>
			}
		}

		new PacketUpdateClientModels(nbtOfGalvanized).sendToPlayer(Transform, player)
	}

}
