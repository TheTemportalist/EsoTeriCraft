package temportalist.esotericraft.main.common.capability

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
class PacketCapabilityPlayer extends IPacket {

	def this(entityID: Int, nbt: NBTTagCompound) {
		this()
		this.add(entityID, nbt)
	}

	override def getReceivableSide: Side = Side.CLIENT

}
object PacketCapabilityPlayer {
	class Handler extends IMessageHandler[PacketCapabilityPlayer, IMessage] {
		override def onMessage(req: PacketCapabilityPlayer,
				messageContext: MessageContext): IMessage = {

			val entityID = req.get[Int]
			val data = req.get[NBTTagCompound]
			Minecraft.getMinecraft.addScheduledTask(new SyncData(entityID, data))

			null
		}
	}
	class SyncData(private val eID: Int, private val data: NBTTagCompound) extends Runnable {

		override def run(): Unit = {
			val world = Minecraft.getMinecraft.theWorld
			val player = world.getEntityByID(this.eID).asInstanceOf[EntityPlayer]
			CapabilityPlayer.get(player).onDataReceived(this.data)
		}

	}
}
