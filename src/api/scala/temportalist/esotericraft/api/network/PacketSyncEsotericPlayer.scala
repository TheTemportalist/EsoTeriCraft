package temportalist.esotericraft.api.network

import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.Side
import temportalist.origin.foundation.common.network.IPacket

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
class PacketSyncEsotericPlayer extends IPacket {

	def this(entityID: Int, nbt: NBTTagCompound) {
		this()
		this.add(entityID, nbt)
	}

	override def getReceivableSide: Side = Side.CLIENT

}
object PacketSyncEsotericPlayer {
	abstract class Handler extends IMessageHandler[PacketSyncEsotericPlayer, IMessage] {
		override def onMessage(req: PacketSyncEsotericPlayer,
				messageContext: MessageContext): IMessage = {

			val entityID = req.get[Int]
			val data = req.get[NBTTagCompound]
			Minecraft.getMinecraft.addScheduledTask(this.getSyncData(entityID, data))

			null
		}

		def getSyncData(entityID: Int, data: NBTTagCompound): Runnable

	}
	abstract class SyncData(private val eID: Int, private val data: NBTTagCompound) extends Runnable {

		def getPlayer: EntityPlayer = {
			Minecraft.getMinecraft.theWorld.getEntityByID(this.eID).asInstanceOf[EntityPlayer]
		}

	}
}
