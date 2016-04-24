package temportalist.esotericsorcery.common.network

import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.Side
import temportalist.esotericsorcery.client.EnumKeyAction
import temportalist.esotericsorcery.common.Sorcery
import temportalist.origin.foundation.common.network.IPacket

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
class PacketKeyPressed extends IPacket {

	def this(action: EnumKeyAction) {
		this()
		this.add(action.ordinal())
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketKeyPressed {
	class Handler extends IMessageHandler[PacketKeyPressed, IMessage] {
		override def onMessage(req: PacketKeyPressed, messageContext: MessageContext): IMessage = {
			Sorcery.doAction(
				EnumKeyAction.values()(req.get[Int]), messageContext.getServerHandler.playerEntity
			)
			null
		}
	}
}
