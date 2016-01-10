package temportalist.enhancing.common.network

import net.minecraft.server.MinecraftServer
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.Side
import temportalist.enhancing.common.tile.TEEnhancingTable
import temportalist.origin.api.common.lib.V3O
import temportalist.origin.foundation.common.network.IPacket

/**
  * Created by TheTemportalist on 1/9/2016.
  */
class PacketEnhancingTable_Server extends IPacket {

	def this(tile: TEEnhancingTable, func: Int) {
		this()
		this.add(tile)
		this.add(func)
	}

	override def getReceivableSide: Side = Side.SERVER
}
object PacketEnhancingTable_Server {
	object PacketType {
		val UNLOCK = 0
		val SELECT = 1
	}
	class Handler extends IMessageHandler[PacketEnhancingTable_Server, IMessage] {
		override def onMessage(message: PacketEnhancingTable_Server,
				ctx: MessageContext): IMessage = {
			message.get[V3O].getTile(MinecraftServer.getServer.getEntityWorld) match {
				case tile: TEEnhancingTable =>
					message.get[Int] match {
						case PacketType.UNLOCK => tile.setGuiNotOpen(true)
						case PacketType.SELECT =>
							tile.enhanceStackWithGlobalID(message.get[Int])
						case _ =>
					}
				case _ =>
			}
			null
		}
	}
}
