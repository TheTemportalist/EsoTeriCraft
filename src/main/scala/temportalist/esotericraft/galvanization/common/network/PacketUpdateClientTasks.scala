package temportalist.esotericraft.galvanization.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.client.ClientTask
import temportalist.esotericraft.galvanization.common.task.{ITask, Task}
import temportalist.origin.foundation.common.network.IPacket

/**
  *
  * Created by TheTemportalist on 5/24/2016.
  *
  * @author TheTemportalist
  */
class PacketUpdateClientTasks extends IPacket {

	def this(doSpawn: Boolean, world: World, task: ITask) {
		this()
		this.add(doSpawn)
		this.add(world.provider.getDimension)
		this.add(task.serializeNBT())
	}

	override def getReceivableSide: Side = Side.CLIENT

}
object PacketUpdateClientTasks {
	class Handler extends IMessageHandler[PacketUpdateClientTasks, IMessage] {
		override def onMessage(message: PacketUpdateClientTasks,
				ctx: MessageContext): IMessage = {
			val doSpawn = message.get[Boolean]
			val dimension = message.get[Int]
			val taskNBT = message.get[NBTTagCompound]
			updateClientTasks(doSpawn, dimension, taskNBT)
			null
		}
	}

	@SideOnly(Side.CLIENT)
	def updateClientTasks(doSpawn: Boolean, dimension: Int, taskNBT: NBTTagCompound): Unit = {
		val task = new Task(Minecraft.getMinecraft.theWorld)
		task.deserializeNBT(taskNBT)
		ClientTask.updateTasks(doSpawn, dimension, task)
	}

}
