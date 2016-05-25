package temportalist.esotericraft.galvanization.common.task.core

import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{World, WorldSavedData}
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent
import temportalist.esotericraft.galvanization.common.task.{INBTCreator, ITask, Task}

import scala.collection.mutable

/**
  *
  * Created by TheTemportalist on 5/24/2016.
  *
  * @author TheTemportalist
  */
final class WorldDataTask(key: String) extends WorldSavedData(key) with INBTCreator {

	// ~~~~~ World ~~~~~

	private var dimension: Int = 0

	def setDimension(i: Int): Unit = this.dimension = i

	def getDimension: Int = this.dimension

	def getWorld: World = DimensionManager.getWorld(this.getDimension)

	// ~~~~~ Tasks ~~~~~

	/**
	  * Map Structure:
	  * pos
	  * -> face
	  * -> task 1
	  * -> task 2
	  * -> ...
	  */
	private val taskObjects = mutable.Map[BlockPos, mutable.Map[EnumFacing, ITask]]()

	def spawnTask(world: World, pos: BlockPos, face: EnumFacing, task: ITask): Boolean = {

		if (!this.taskObjects.contains(pos))
			this.taskObjects.put(pos, mutable.Map[EnumFacing, ITask]())

		if (this.taskObjects(pos).contains(face))
			return false

		this.taskObjects(pos)(face) = task

		task.onSpawn(world, pos, face)

		this.markDirty()
		true
	}

	def breakTask(world: World, pos: BlockPos, face: EnumFacing): ITask = {

		if (this.taskObjects.contains(pos) && this.taskObjects(pos).contains(face)) {
			val task = this.taskObjects(pos)(face)
			task.onBreak(world, pos, face)

			val removed = this.taskObjects(pos).remove(face).get

			this.markDirty()
			removed
		}
		else null
	}

	def getTasks: Iterable[ITask] = {
		for {
			faceToListTaskMap <- this.taskObjects.values
			task <- faceToListTaskMap.values
		} yield task
	}

	def getTaskAt(pos: BlockPos, face: EnumFacing): ITask = {
		if (this.taskObjects.contains(pos) &&
				this.taskObjects(pos).contains(face))
			this.taskObjects(pos)(face)
		else null
	}

	// ~~~~~ NBT ~~~~~

	override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {

		tag.setInteger("dimension", this.getDimension)

		val listTasks = new NBTTagList
		this.doIterableTask((task: ITask) => {
			listTasks.appendTag(task.serializeNBT())
		})
		tag.setTag("tasks", listTasks)

		tag
	}

	override def readFromNBT(nbt: NBTTagCompound): Unit = {

		this.dimension = nbt.getInteger("dimension")

		this.taskObjects.clear()
		val listTasks = this.getTagList[NBTTagCompound](nbt, "tasks")
		for (taskNBT <- this.getTagListAsIterable[NBTTagCompound](listTasks)) {
			val world = this.getWorld
			val task = new Task(world)
			task.deserializeNBT(taskNBT)
			this.spawnTask(world, task.getPosition, task.getFace, task)
		}

	}

	// ~~~~~ Ticking ~~~~~

	def onTickServer(event: WorldTickEvent): Unit = {
		this.doIterableTask((task: ITask) => {
			task.onUpdateServer()
		})
	}

	// ~~~~~ Other ~~~~~

	def doIterableTask[U](f: (ITask) => U): Unit = {
		for {
			faceToListTaskMap <- this.taskObjects.values
			task <- faceToListTaskMap.values
		} f(task)
	}

	// ~~~~~ End ~~~~~

}
