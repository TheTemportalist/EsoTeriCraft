package temportalist.esotericraft.galvanization.common.entity

import net.minecraft.world.World
import temportalist.esotericraft.api.galvanize.ai.IGalvanizeTask
import temportalist.esotericraft.galvanization.common.entity.ai.EntityAIHelper
import temportalist.esotericraft.galvanization.common.task.ITask
import temportalist.esotericraft.galvanization.common.task.ai.{IEntityMover, IFlyCheck}
import temportalist.esotericraft.galvanization.common.task.core.ControllerTask

/**
  *
  * Created by TheTemportalist on 5/25/2016.
  *
  * @author TheTemportalist
  */
class EntityAITaskUpdater(
		private val owner: EntityEmpty
) extends EntityAIHelper with IEntityMover with IFlyCheck {

	private var currentTask: ITask = null
	private var currentAI: IGalvanizeTask = null

	def getWorld: World = this.owner.getEntityWorld

	override def shouldExecute(): Boolean = true

	override def updateTask(): Unit = {

		if (this.currentAI == null) {
			this.findNextTask()
			if (this.currentAI != null) this.currentAI.startExecuting(this.owner)
		}

		if (this.currentTask != null && !this.currentTask.isValid) {
			this.currentTask = null
			this.currentAI = null
		}

		if (this.currentAI == null) return

		this.currentAI.updateTask(this.owner)

		if (!this.currentAI.shouldExecute(this.owner)) {
			this.currentAI.resetTask(this.owner)
			this.currentAI = null
		}

	}

	def findNextTask(): Unit = {
		val taskPositions = this.owner.getTaskPositionsAsSeq
		for (posFace <- taskPositions) {
			ControllerTask.getTaskAt(this.getWorld, posFace._1, posFace._2) match {
				case task: ITask =>
					task.getAI match {
						case aiTask: IGalvanizeTask =>
							val shouldExec = aiTask.shouldExecute(this.owner)
							if (shouldExec) {
								this.currentTask = task
								this.currentAI = aiTask
								return
							}
						case _ => // null
					}
				case _ =>
					this.owner.removeTask(posFace._1, posFace._2)
			}
		}
	}

}
