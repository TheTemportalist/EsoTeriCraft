package temportalist.esotericraft.sorcery.common.network

import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.api.sorcery.ApiSorcery
import temportalist.esotericraft.api.sorcery.ApiSorcery.ISorceryPlayer
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

			ApiSorcery.get(this.getPlayer(ctx)) match {
				case player: ISorceryPlayer => player.cast()
				case _ =>
			}

			null
		}

		def getPlayer(ctx: MessageContext): EntityPlayer = {

			ctx.side match {
				case Side.CLIENT => this.getPlayerClient
				case Side.SERVER => ctx.getServerHandler.playerEntity
			}

		}

		@SideOnly(Side.CLIENT)
		def getPlayerClient: EntityPlayer = Minecraft.getMinecraft.thePlayer

	}

}
