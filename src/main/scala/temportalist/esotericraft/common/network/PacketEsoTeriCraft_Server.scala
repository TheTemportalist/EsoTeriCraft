package temportalist.esotericraft.common.network

import net.minecraftforge.fml.common.network.simpleimpl.{MessageContext, IMessage, IMessageHandler}
import net.minecraftforge.fml.relauncher.Side
import temportalist.esotericraft.api.ApiEsotericraft
import temportalist.esotericraft.common.EsoTeriCraft
import temportalist.origin.foundation.common.network.IPacket

/**
  * Created by TheTemportalist on 1/12/2016.
  */
class PacketEsoTeriCraft_Server extends IPacket {

	def this(id: Byte) {
		this()
		this.add(id)
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketEsoTeriCraft_Server {

	def switchSpell(increment: Boolean): Unit = {
		new PacketEsoTeriCraft_Server(0).add(increment).sendToServer(EsoTeriCraft)
	}

	class Handler extends IMessageHandler[PacketEsoTeriCraft_Server, IMessage] {
		override def onMessage(message: PacketEsoTeriCraft_Server,
				ctx: MessageContext): IMessage = {
			val player = ctx.getServerHandler.playerEntity
			message.get[Byte] match {
				case 0 =>
					ApiEsotericraft.Spells.switchSpell(player, message.get[Boolean])
				case _ =>
			}
			null
		}
	}
}
