package temportalist.esotericraft.galvanization.common.task.core

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint
import temportalist.esotericraft.api.galvanize.ai.IGalvanizeTask
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.init.ModItems
import temportalist.esotericraft.galvanization.common.network.PacketUpdateClientTasks
import temportalist.esotericraft.galvanization.common.task.ITask
import temportalist.esotericraft.galvanization.common.task.ai.core.LoaderTask

/**
  *
  * Created by TheTemportalist on 5/24/2016.
  *
  * @author TheTemportalist
  */
object ControllerTask {

	private val WORLD_DATA_KEY_TASK = Galvanize.getModId + ":task_data"

	def getData(world: World): WorldDataTask = {
		world.loadItemData(classOf[WorldDataTask], WORLD_DATA_KEY_TASK) match {
			case data: WorldDataTask => data
			case _ => // null or not WorldDataTask
				val data = new WorldDataTask(WORLD_DATA_KEY_TASK)
				data.setDimension(world.provider.getDimension)
				world.setItemData(WORLD_DATA_KEY_TASK, data)
				data
		}
	}

	def spawnTask(world: World, pos: BlockPos, face: EnumFacing, task: ITask): Boolean = {
		if (!world.isRemote && this.getData(world).spawnTask(world, pos, face, task)) {
			new PacketUpdateClientTasks(
				PacketUpdateClientTasks.SPAWN, task
			).sendToAllAround(Galvanize,
				new TargetPoint(world.provider.getDimension, pos.getX, pos.getY, pos.getZ, 128)
			)
			true
		}
		else false
	}

	def breakTask(world: World, pos: BlockPos, face: EnumFacing, drop: Boolean = true): Boolean = {
		if (world.isRemote) return false
		this.getData(world).breakTask(world, pos, face) match {
			case task: ITask =>
				task.onBroken(drop)
				new PacketUpdateClientTasks(
					PacketUpdateClientTasks.BREAK, task
				).sendToAllAround(Galvanize,
					new TargetPoint(world.provider.getDimension, pos.getX, pos.getY, pos.getZ, 128)
				)
				true
			case _ => // no task broken
				false
		}
	}

	def getNewItemStackForAIClass(aiClass: Class[_ <: IGalvanizeTask],
			stack: ItemStack = new ItemStack(ModItems.taskItem)): ItemStack = {
		stack.setTagCompound(new NBTTagCompound)
		val info = LoaderTask.getAnnotationInfo(aiClass)
		val display = info.getOrElse("displayName", null)
		if (display != null) stack.getTagCompound.setString("displayName", display.toString)
		val name = info.getOrElse("name", null)
		if (name != null) stack.getTagCompound.setString("name", name.toString)
		stack.getTagCompound.setString("className", aiClass.getName)
		stack
	}

	def getTaskAt(world: World, pos: BlockPos, face: EnumFacing): ITask = {
		this.getData(world).getTaskAt(pos, face)
	}

	@SubscribeEvent
	def onJoinWorld(event: EntityJoinWorldEvent): Unit = {
		if (event.getWorld.isRemote) return
		event.getEntity match {
			case player: EntityPlayerMP =>
				val tasks = this.getData(event.getWorld).getTasks
				if (tasks.nonEmpty) {
					for (task <- tasks)
					///* TODO make packet more efficient
					new PacketUpdateClientTasks(PacketUpdateClientTasks.LOAD, task).sendToPlayer(Galvanize, player)
					//*/
				}
			case _ =>
		}
	}

	@SubscribeEvent
	def onTickWorld(event: WorldTickEvent): Unit = {
		this.getData(event.world).onTickServer(event)
	}

}
