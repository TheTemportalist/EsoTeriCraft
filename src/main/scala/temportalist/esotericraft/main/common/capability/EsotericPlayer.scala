package temportalist.esotericraft.main.common.capability

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.{NBTBase, NBTTagCompound}
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint
import temportalist.esotericraft.main.common.capability.Handler.HandlerEntity
import temportalist.esotericraft.main.common.network.PacketSyncEsotericPlayer
import temportalist.origin.api.common.capability.ICapability
import temportalist.origin.foundation.common.IModPlugin

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
abstract class EsotericPlayer extends ICapability[EntityPlayer, NBTTagCompound] {

	// ~~~~~~~~~~ Capability ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override final def initEntity(world: World, entity: EntityPlayer): Unit = {
		super.initEntity(world, entity)
		this.init(world, entity)
		this.markDirtyInit()
	}

	def init(world: World, player: EntityPlayer): Unit

	// ~~~~~~~~~~ NBT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getNewNBT: NBTTagCompound = new NBTTagCompound

	// ~~~~~~~~~~ Packets ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def getPacketTargetPoint: TargetPoint = {
		val player = this.getOwner
		new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 128)
	}

	override def sendNBTToClient(nbt: NBTBase): Unit = {
		new PacketSyncEsotericPlayer(this.getOwner.getEntityId, nbt.asInstanceOf[NBTTagCompound]).
				sendToAllAround(this.getModForPacketSync, this.getPacketTargetPoint)
	}

	def getModForPacketSync: IModPlugin

	def onDataReceived(nbt: NBTTagCompound): Unit

}
object EsotericPlayer {

	abstract class Handler[T <: EsotericPlayer]
			extends HandlerEntity[EntityPlayer, NBTTagCompound, T] {

		// ~~~~~~~~~~ Validation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		override def isValid(e: AnyRef): Boolean = e.isInstanceOf[EntityPlayer]

		override def cast(e: AnyRef): EntityPlayer = e.asInstanceOf[EntityPlayer]

	}

}
