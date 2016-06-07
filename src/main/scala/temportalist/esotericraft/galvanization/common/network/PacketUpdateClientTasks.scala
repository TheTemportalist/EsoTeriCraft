package temportalist.esotericraft.galvanization.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
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

	def this(func: Int) {
		this()
		this.add(func)
	}

	def this(func: Int, task: ITask) {
		this(func)
		this.add(task.serializeNBT())
		//Galvanize.log("Constructed")
	}

	override def getReceivableSide: Side = Side.CLIENT

}
object PacketUpdateClientTasks {

	val SPAWN = 0
	val BREAK = 1
	val CLEAR = 2

	class Handler extends IMessageHandler[PacketUpdateClientTasks, IMessage] {
		override def onMessage(message: PacketUpdateClientTasks,
				ctx: MessageContext): IMessage = {
			Minecraft.getMinecraft.addScheduledTask(new Runnable {
				override def run(): Unit = {
					val func = message.get[Int]
					if (func == PacketUpdateClientTasks.CLEAR) {
						ClientTask.clear()
						return
					}
					val taskNBT = message.get[NBTTagCompound]
					val task = new Task(Minecraft.getMinecraft.theWorld)
					task.deserializeNBT(taskNBT)
					ClientTask.updateTasks(func, task)
				}
			})
			null
		}
	}

	@SideOnly(Side.CLIENT)
	def updateClientTasks(message: PacketUpdateClientTasks): Unit = {


		/*
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

		}
		*/
	}


}
