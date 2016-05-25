package temportalist.esotericraft.galvanization.common.task.core

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.network.PacketUpdateClientTasks
import temportalist.esotericraft.galvanization.common.task.ITask

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
				true, world, task
			).sendToDimension(Galvanize, world.provider.getDimension)
			true
		} else false
	}

	def breakTask(world: World, pos: BlockPos, face: EnumFacing): Boolean = {
		this.getData(world).breakTask(world, pos, face) match {
			case task: ITask =>
				new PacketUpdateClientTasks(
					false, world, task
				).sendToDimension(Galvanize, world.provider.getDimension)
				true
			case _ => // no task broken
				false
		}
	}

	@SubscribeEvent
	def onTickWorld(event: WorldTickEvent): Unit = {
		this.getData(event.world).onTickServer(event)
	}

}
