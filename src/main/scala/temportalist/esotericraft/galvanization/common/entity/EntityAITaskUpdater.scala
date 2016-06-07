package temportalist.esotericraft.galvanization.common.entity

import java.util

import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.world.World
import temportalist.esotericraft.api.galvanize.ai.{EnumTaskType, IGalvanizeTask}
import temportalist.esotericraft.galvanization.common.task.ITask
import temportalist.esotericraft.galvanization.common.task.core.ControllerTask

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 5/25/2016.
  *
  * @author TheTemportalist
  */
class EntityAITaskUpdater(
		private val owner: EntityEmpty
) extends EntityAIBase {

	private val currentTasks = new util.EnumMap[EnumTaskType, ITask](classOf[EnumTaskType])

	def getWorld: World = this.owner.getEntityWorld

	def getCurrentTasks: Iterable[ITask] = {
		JavaConversions.collectionAsScalaIterable(this.currentTasks.values())
	}

	override def shouldExecute(): Boolean = true

	override def updateTask(): Unit = {

		var currentTask: ITask = null
		var currentTaskType: EnumTaskType = null
		var lowestPriorityNumber = -1
		for (taskType <- EnumTaskType.values()) {

			if (!this.currentTasks.containsKey(taskType)) {
				this.findNextTask(taskType) match {
					case task: ITask =>
						this.currentTasks.put(taskType, task)
						task.getAI.startExecuting(this.owner)
					case _ => // null
				}
			}

			if (this.currentTasks.containsKey(taskType)) {
				val priority = this.currentTasks.get(taskType).getAI.getTaskType.ordinal()
				if (lowestPriorityNumber < 0 || priority < lowestPriorityNumber) {
					if (!this.currentTasks.get(taskType).isValid)
						this.currentTasks.remove(taskType)
					else {
						lowestPriorityNumber = priority
						currentTask = this.currentTasks.get(taskType)
						currentTaskType = taskType
					}
				}
			}

		}

		if (currentTask == null) return

		val currentAI = currentTask.getAI

		currentAI.updateTask(this.owner)

		if (!currentAI.shouldExecute(this.owner)) {
			currentAI.resetTask(this.owner)
			this.currentTasks.remove(currentTaskType)
		}

	}

	def findNextTask(taskType: EnumTaskType): ITask = {
		val taskPositions = this.owner.getTaskPositionsAsSeq
		for (posFace <- taskPositions) {
			ControllerTask.getTaskAt(this.getWorld, posFace._1, posFace._2) match {
				case task: ITask =>
					task.getAI match {
						case aiTask: IGalvanizeTask =>
							//Galvanize.log("" + aiTask.getClass.getSimpleName + " " + taskType)
							if (aiTask.getTaskType == taskType) {
								if (aiTask.shouldExecute(this.owner))
									return task
							}
						case _ => // null
					}
				case _ =>
					this.owner.removeTask(posFace._1, posFace._2)
			}
		}
		null
	}

}
