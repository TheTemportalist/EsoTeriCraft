package temportalist.esotericraft.galvanization.common.network

import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.Side
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.capability.{HelperGalvanize, IPlayerGalvanize}
import temportalist.origin.foundation.common.network.IPacket

/**
  *
  * Created by TheTemportalist on 5/22/2016.
  *
  * @author TheTemportalist
  */
class PacketSetModel extends IPacket {

	def this(selected: Int) {
		this()
		this.add(selected)
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketSetModel {
	class Handler extends IMessageHandler[PacketSetModel, IMessage] {
		override def onMessage(message: PacketSetModel, ctx: MessageContext): IMessage = {
			val index = message.get[Int]
			val player = ctx.getServerHandler.playerEntity
			HelperGalvanize.get(player) match {
				case galvanized: IPlayerGalvanize =>
					Galvanize.log(galvanized.getModelEntities.size() + "")
					val state = galvanized.getModelEntities.get(index)
					Galvanize.log(state.getName)
					galvanized.setEntityState(state)
				case _ =>
			}
			null
		}
	}
}
