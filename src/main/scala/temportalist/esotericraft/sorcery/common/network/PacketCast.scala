package temportalist.esotericraft.sorcery.common.network

import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.Side
import temportalist.esotericraft.sorcery.common.capability.HandlerSorceryPlayer
import temportalist.origin.foundation.common.network.IPacket

/**
  *
  * Created by TheTemportalist on 5/6/2016.
  *
  * @author TheTemportalist
  */
class PacketCast extends IPacket {

	override def getReceivableSide: Side = null

}
object PacketCast {
	class Handler extends IMessageHandler[PacketCast, IMessage] {
		override def onMessage(message: PacketCast, ctx: MessageContext): IMessage = {

			null
		}
	}
}
