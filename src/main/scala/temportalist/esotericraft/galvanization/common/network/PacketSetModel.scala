package temportalist.esotericraft.galvanization.common.network

import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.Side
import temportalist.esotericraft.galvanization.common.capability.{HelperGalvanize, IPlayerGalvanize}
import temportalist.origin.foundation.common.network.IPacket

/**
  *
  * Created by TheTemportalist on 5/22/2016.
  *
  * @author TheTemportalist
  */
class PacketSetModel extends IPacket {

	def this(selected: Int, func: Int) {
		this()
		this.add(selected)
		this.add(func)
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketSetModel {
	class Handler extends IMessageHandler[PacketSetModel, IMessage] {
		override def onMessage(message: PacketSetModel, ctx: MessageContext): IMessage = {
			val index = message.get[Int]
			val func = message.get[Int]

			val player = ctx.getServerHandler.playerEntity
			HelperGalvanize.get(player) match {
				case galvanized: IPlayerGalvanize =>

					func match {
						case 0 => // select
							if (index == 0) galvanized.clearEntityState(player.getEntityWorld)
							else {
								val state = galvanized.getModelEntities.get(index - 1)
								galvanized.setEntityState(state)
							}
						case 1 => // delete
							if (index != 0) {
								galvanized.removeModelEntity(index - 1)
							}
						case _ =>
					}



				case _ =>
			}
			null
		}
	}
}
