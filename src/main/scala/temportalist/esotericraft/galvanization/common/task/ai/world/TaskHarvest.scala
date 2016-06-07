package temportalist.esotericraft.galvanization.common.task.ai.world

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityCreature
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.Axis
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import temportalist.esotericraft.api.galvanize.ai.EnumTaskType
import temportalist.esotericraft.galvanization.common.task.ai.core.TaskBase
import temportalist.esotericraft.galvanization.common.task.ai.interfaces.ITaskSized
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 5/26/2016.
  *
  * @author TheTemportalist
  */
abstract class TaskHarvest(
		pos: BlockPos, face: EnumFacing
) extends TaskBase(pos, face) with ITaskSized {

	// ~~~~~ Task Info ~~~~~

	override def getTaskType: EnumTaskType = EnumTaskType.WORLD_INTERACTION

	// ~~~~~ Bounding Box ~~~~~

	override def getRadius(axis: Axis): Double = axis match {
		case Axis.X => 4.5
		case Axis.Y => 0.5
		case Axis.Z => 4.5
		case _ => 0
	}

	// ~~~~~ AI ~~~~~

	private var destinationPos: Vect = null

	def isBlockValid(world: World, pos: BlockPos, state: IBlockState): Boolean

	def harvestState(world: World, pos: BlockPos, state: IBlockState, entity: EntityCreature): Unit

	override def shouldExecute(entity: EntityCreature): Boolean = {
		val world = entity.getEntityWorld
		val aabb = this.getBoundingBox
		for {
			x <- aabb.minX.toInt to aabb.maxX.toInt
			y <- aabb.minY.toInt to aabb.maxY.toInt
			z <- aabb.minZ.toInt to aabb.maxZ.toInt
		} {
			val pos = new BlockPos(x, y, z)
			if (this.isBlockValid(world, pos, world.getBlockState(pos)))
				return true
		}
		false
	}

	override def updateTask(entity: EntityCreature): Unit = {

		if (this.destinationPos == null)
			this.getClosestValidBlockToOrigin(entity.getEntityWorld) match {
				case pos: BlockPos => this.destinationPos = new Vect(pos)
				case _ => // null pos
			}

		if (this.destinationPos == null) return

		val distToDestination = (new Vect(entity) - this.destinationPos).length
		if (distToDestination <= 2.0) {
			val pos = this.destinationPos.toBlockPos
			val targetState = entity.getEntityWorld.getBlockState(pos)
			if (this.isBlockValid(entity.getEntityWorld, pos, targetState))
				this.harvestState(entity.getEntityWorld, pos, targetState, entity)
			this.destinationPos = null
		}
		else {
			this.moveEntityTowards(entity,
				this.destinationPos.x, this.destinationPos.y, this.destinationPos.z,
				1F, this.getCanFly)
		}

	}

	def getClosestValidBlockToOrigin(world: World): BlockPos = {
		val originBlockPos = new Vect(this.pos)
		val aabb = this.getBoundingBox

		// the distance from the target block to the origin
		var leastDistance = -1D
		// the block pos of the target block
		var posTarget: BlockPos = null
		for {
			x <- aabb.minX.toInt to aabb.maxX.toInt
			y <- aabb.minY.toInt to aabb.maxY.toInt
			z <- aabb.minZ.toInt to aabb.maxZ.toInt
		} {
			val posBlock = new BlockPos(x, y, z)
			if (this.isBlockValid(world, posBlock, world.getBlockState(posBlock))) {
				val posBlockVect = new Vect(posBlock) + Vect.CENTER
				val posDiff = originBlockPos - posBlockVect
				val dist = posDiff.length
				if (leastDistance < 0 || dist < leastDistance) {
					leastDistance = dist
					posTarget = posBlock
				}
			}
		}

		posTarget
	}

	// ~~~~~ End ~~~~~

}
