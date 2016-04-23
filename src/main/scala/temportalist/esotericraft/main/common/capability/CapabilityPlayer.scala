package temportalist.esotericraft.main.common.capability

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.{NBTBase, NBTTagCompound}
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject}
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint
import temportalist.esotericraft.main.common.EsoTeriCraft
import temportalist.esotericraft.main.common.capability.api.Handler.HandlerEntity
import temportalist.esotericraft.main.common.capability.api.ICapability

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
class CapabilityPlayer extends ICapability[EntityPlayer, NBTTagCompound] {

	// ~~~~~~~~~~ Capability ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def initEntity(world: World, entity: EntityPlayer): Unit = {
		super.initEntity(world, entity)



		this.markDirtyInit()
	}

	// ~~~~~~~~~~ NBT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getNewNBT: NBTTagCompound = new NBTTagCompound

	override def writeToNBT(nbt: NBTTagCompound): Unit = {

	}

	override def readFromNBT(nbt: NBTTagCompound): Unit = {

	}

	// ~~~~~~~~~~ Packets ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def getPacketTargetPoint: TargetPoint = {
		val player = this.getOwner
		new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 128)
	}

	override def sendNBTToClient(nbt: NBTBase): Unit = {
		new PacketCapabilityPlayer(this.getOwner.getEntityId, nbt.asInstanceOf[NBTTagCompound]).
				sendToAllAround(EsoTeriCraft, this.getPacketTargetPoint)
	}

	def onDataReceived(nbt: NBTTagCompound): Unit = {

	}

}
object CapabilityPlayer extends HandlerEntity[EntityPlayer, NBTTagCompound, CapabilityPlayer] {

	// ~~~~~~~~~~ Setup And Register ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@CapabilityInject(classOf[CapabilityPlayer])
	var CAPABILITY: Capability[CapabilityPlayer] = null

	def register(): Unit = {
		super.register(EsoTeriCraft, "EsotericPlayer")
		EsoTeriCraft.log("Registered Capability EsotericPlayer")
	}

	// ~~~~~~~~~~ Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getClassCapability: Class[CapabilityPlayer] = classOf[CapabilityPlayer]

	override def getCapability: Capability[CapabilityPlayer] = this.CAPABILITY

	override def getNewCapabilityInstance: CapabilityPlayer = new CapabilityPlayer

	// ~~~~~~~~~~ Validation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def isValid(e: AnyRef): Boolean = e.isInstanceOf[EntityPlayer]

	override def cast(e: AnyRef): EntityPlayer = e.asInstanceOf[EntityPlayer]

	// ~~~~~~~~~~ Getting per player ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def get(player: EntityPlayer): CapabilityPlayer = player.getCapability(this.CAPABILITY, null)

	// ~~~~~~~~~~ Death Persistence ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@SubscribeEvent
	def onPlayerClone(event: PlayerEvent.Clone): Unit = {
		if (!event.isWasDeath) return
		val data = this.get(event.getOriginal).serializeNBT
		this.get(event.getEntityPlayer).deserializeNBT(data)
	}

}
