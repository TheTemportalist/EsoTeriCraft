package temportalist.esotericraft.galvanization.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.client.ClientTask
import temportalist.esotericraft.galvanization.common.task.{ITask, Task}
import temportalist.origin.api.common.utility.NBTHelper
import temportalist.origin.foundation.common.network.IPacket

import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 5/24/2016.
  *
  * @author TheTemportalist
  */
class PacketUpdateClientTasks extends IPacket {

	def this(func: Int, task: ITask) {
		this()
		this.add(func)
		this.add(task.serializeNBT())
	}

	def this(tasks: Iterable[ITask]) {
		this()
		this.add(PacketUpdateClientTasks.LOAD)
		this.add({
			val tag = new NBTTagCompound
			val list = new NBTTagList
			for (task <- tasks) list.appendTag(task.serializeNBT())
			tag.setTag("Packet_taskList", list)
			tag
		})
	}

	override def getReceivableSide: Side = Side.CLIENT

}
object PacketUpdateClientTasks {

	val SPAWN = 0
	val BREAK = 1
	val LOAD = 2

	class Handler extends IMessageHandler[PacketUpdateClientTasks, IMessage] {
		override def onMessage(message: PacketUpdateClientTasks,
				ctx: MessageContext): IMessage = {
			val func = message.get[Int]
			val taskNBT = message.get[NBTTagCompound]
			updateClientTasks(func, taskNBT)
			null
		}
	}

	@SideOnly(Side.CLIENT)
	def updateClientTasks(func: Int, taskNBT: NBTTagCompound): Unit = {
		if (taskNBT.hasKey("Packet_taskList")) {
			val tasks = ListBuffer[ITask]()
			val taskNBTList = NBTHelper.getTagList[NBTTagCompound](taskNBT, "Packet_taskList")
			for (i <- 0 until taskNBTList.tagCount()) {
				val task = new Task(Minecraft.getMinecraft.theWorld)
				task.deserializeNBT(taskNBTList.getCompoundTagAt(i))
				tasks += task
			}
			ClientTask.updateTasks(tasks:_*)
		}
		else {
			val task = new Task(Minecraft.getMinecraft.theWorld)
			task.deserializeNBT(taskNBT)
			ClientTask.updateTasks(func, task)
		}
	}


}
