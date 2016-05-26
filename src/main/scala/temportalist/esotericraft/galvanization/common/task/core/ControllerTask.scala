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
		if (this.getData(world).spawnTask(world, pos, face, task)) {
			new PacketUpdateClientTasks(
				PacketUpdateClientTasks.SPAWN, task
			).sendToDimension(Galvanize, world.provider.getDimension)
			true
		} else false
	}

	def breakTask(world: World, pos: BlockPos, face: EnumFacing, drop: Boolean = true): Boolean = {
		this.getData(world).breakTask(world, pos, face) match {
			case task: ITask =>
				task.onBroken(drop)
				new PacketUpdateClientTasks(
					PacketUpdateClientTasks.BREAK, task
				).sendToDimension(Galvanize, world.provider.getDimension)
				true
			case _ => // no task broken
				false
		}
	}

	def getTaskItemForAIClass(aiClass: Class[_ <: IGalvanizeTask],
			stack: ItemStack = new ItemStack(ModItems.taskItem)): ItemStack = {
		stack.setTagCompound(new NBTTagCompound)
		val name = LoaderTask.getAnnotationInfo(aiClass).getOrElse("displayName", null)
		if (name != null) stack.getTagCompound.setString("displayName", name.toString)
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
				new PacketUpdateClientTasks(
					this.getData(event.getWorld).getTasks
				).sendToPlayer(Galvanize, player)
			case _ =>
		}
	}

	@SubscribeEvent
	def onTickWorld(event: WorldTickEvent): Unit = {
		this.getData(event.world).onTickServer(event)
	}

}
