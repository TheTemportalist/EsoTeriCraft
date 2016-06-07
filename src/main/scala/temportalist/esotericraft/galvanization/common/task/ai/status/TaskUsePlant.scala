package temportalist.esotericraft.galvanization.common.task.ai.status

import net.minecraft.entity.EntityCreature
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.util.EnumFacing.Axis
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable
import temportalist.esotericraft.api.galvanize.ai.{EnumTaskType, GalvanizeTask}
import temportalist.esotericraft.api.init.Details
import temportalist.esotericraft.galvanization.common.task.ai.core.TaskBase
import temportalist.esotericraft.galvanization.common.task.ai.interfaces.{ITaskInventory, ITaskSized}
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 6/7/2016.
  *
  * @author TheTemportalist
  */
@GalvanizeTask(modid = Details.MOD_ID,
	name = "placePlant",
	displayName = "Plant a Thing"
)
class TaskUsePlant(
		pos: BlockPos, face: EnumFacing
) extends TaskBase(pos, face) with ITaskInventory with ITaskSized {

	private val speed: Double = 1.2D

	// ~~~~~ Task Info ~~~~~

	override def getTaskType: EnumTaskType = EnumTaskType.STATUS_RELIANT

	// ~~~~~ Bounding Box ~~~~~

	override def getRadius(axis: Axis): Double = axis match {
		case Axis.X => 4.5
		case Axis.Y => 0.5
		case Axis.Z => 4.5
		case _ => 0
	}

	// ~~~~~ AI ~~~~~

	override def canEntityUse(world: World, stack: ItemStack): Boolean = {
		this.getPositionForStack(world, stack) != null
	}

	def getPositionForStack(world: World, stack: ItemStack): BlockPos = {

		if (stack == null) return null

		val plantable: IPlantable = this.getPlantable(stack)
		if (plantable == null) return null

		val aabb = this.getBoundingBox
		for {
			x <- aabb.minX.toInt until aabb.maxX.toInt
			z <- aabb.minZ.toInt until aabb.maxZ.toInt
			y <- aabb.minY.toInt until aabb.maxY.toInt
		} {
			val posPlant = new BlockPos(x, y, z)
			val posSoil = posPlant.down()
			val soilState = world.getBlockState(posSoil)
			val canSustain = soilState.getBlock.canSustainPlant(
				soilState, world, posSoil, EnumFacing.UP, plantable)
			if (canSustain && world.isAirBlock(posPlant)) return posPlant
		}

		null
	}

	def getPlantable(stack: ItemStack): IPlantable = {
		stack.getItem match {
			case plantable: IPlantable => plantable
			case itemBlock: ItemBlock =>
				itemBlock.getBlock match {
					case plantable: IPlantable => plantable
					case _ => null
				}
			case _ =>
				if (stack.getItem == Items.REEDS) Blocks.REEDS
				else null
		}
	}

	override def shouldExecute(entity: EntityCreature): Boolean = {

		var stackHolding: ItemStack = null
		for (hand <- EnumHand.values()) {
			entity.getHeldItem(hand) match {
				case stack: ItemStack =>
					stackHolding = stack
				case _ => // null
			}
		}
		if (stackHolding == null) return false

		val posPlant = this.getPositionForStack(entity.getEntityWorld, stackHolding)
		if (posPlant != null) return true

		false
	}

	override def updateTask(entity: EntityCreature): Unit = {

		var stackHolding: ItemStack = null
		var stackHand: EnumHand = null
		for (hand <- EnumHand.values()) {
			entity.getHeldItem(hand) match {
				case stack: ItemStack =>
					stackHolding = stack
					stackHand = hand
				case _ => // null
			}
		}
		if (stackHolding == null) return

		val posPlant = this.getPositionForStack(entity.getEntityWorld, stackHolding)
		if (posPlant == null) return

		val vectPlant = new Vect(posPlant)
		val targetPosMove = vectPlant + Vect.CENTER
		val targetPosDist = vectPlant + Vect.CENTER.suppressAxisGet(Axis.Y)
		val ownerDistance = (new Vect(entity) - targetPosDist).length
		if (ownerDistance > 0.75D) {
			this.moveEntityTowards(entity,
				targetPosMove.x, targetPosMove.y, targetPosMove.z,
				this.speed, this.getCanFly)
		}
		else {
			val remainder = this.plantItem(entity.getEntityWorld, posPlant, stackHolding.copy())
			entity.setHeldItem(stackHand, remainder)
		}

	}

	def plantItem(world: World, pos: BlockPos, stack: ItemStack): ItemStack = {
		if (stack == null) return stack
		if (!world.isAirBlock(pos)) return stack

		this.getPlantable(stack) match {
			case plantable: IPlantable =>
				world.setBlockState(pos, plantable.getPlant(world, pos))
				stack.stackSize -= 1
				if (stack.stackSize <= 0) return null
				else return stack
			case _ => // null
		}

		stack
	}

	// ~~~~~ End ~~~~~

}
