package temportalist.esotericraft.galvanization.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.client.ClientTask
import temportalist.esotericraft.galvanization.common.task.{ITask, Task}
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
		this.add(1)
		this.add(task.serializeNBT())
	}

	def this(tasks: Iterable[ITask]) {
		this()
		this.add(PacketUpdateClientTasks.LOAD)
		this.add(tasks.size)
		for (task <- tasks)
			this.add(task.serializeNBT())
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
			val quantity = message.get[Int]
			val taskNBTs = ListBuffer[NBTTagCompound]()
			for (i <- 0 until quantity)
				taskNBTs += message.get[NBTTagCompound]
			if (quantity > 0) updateClientTasks(func, taskNBTs:_*)
			null
		}
	}

	@SideOnly(Side.CLIENT)
	def updateClientTasks(func: Int, taskNBTs: NBTTagCompound*): Unit = {
		if (taskNBTs.size > 1) {
			val tasks = ListBuffer[ITask]()
			for (taskNBT <- taskNBTs) {
				val task = new Task(Minecraft.getMinecraft.theWorld)
				task.deserializeNBT(taskNBTs(0))
				tasks += task
			}
			ClientTask.updateTasks(tasks: _*)
		}
		else {
			val task = new Task(Minecraft.getMinecraft.theWorld)
			task.deserializeNBT(taskNBTs(0))
			ClientTask.updateTasks(func, task)
		}
	}


}
