package temportalist.esotericraft.transmorigification.common.network

import java.util.UUID

import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.transmorigification.common.capability.{HelperGalvanize, IPlayerGalvanize}
import temportalist.origin.foundation.common.network.IPacket

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 5/31/2016.
  *
  * @author TheTemportalist
  */
class PacketUpdateClientModels extends IPacket {

	def this(tag: NBTTagCompound) {
		this()
		this.add(tag)
	}

	override def getReceivableSide: Side = Side.CLIENT

}
object PacketUpdateClientModels {

	class Handler extends IMessageHandler[PacketUpdateClientModels, IMessage] {

		override def onMessage(message: PacketUpdateClientModels,
				ctx: MessageContext): IMessage = {

			val tag = message.get[NBTTagCompound]

			val world = this.getWorld
			for (playerName <- JavaConversions.asScalaSet(tag.getKeySet)) {
				val uuid = new UUID(tag.getLong("bits_most"), tag.getLong("bits_least"))
				val nbt = tag.getCompoundTag("galvanized_tag")
				world.getPlayerEntityByUUID(uuid) match {
					case player: EntityPlayer =>
						HelperGalvanize.get(player) match {
							case galvanized: IPlayerGalvanize =>
								galvanized.deserializeNBT(nbt)
							case _ => // null
						}
					case _ => // null
				}
			}

			null
		}

		@SideOnly(Side.CLIENT)
		def getWorld: World = Minecraft.getMinecraft.theWorld

	}

}
