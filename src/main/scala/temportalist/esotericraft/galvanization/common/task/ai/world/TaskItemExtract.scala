package temportalist.esotericraft.galvanization.common.task.ai.world

import net.minecraft.entity.{EntityCreature, EntityLivingBase}
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing.Axis
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraftforge.items.IItemHandler
import temportalist.esotericraft.api.galvanize.ai.{EnumTaskType, GalvanizeTask}
import temportalist.esotericraft.api.init.Details
import temportalist.esotericraft.galvanization.common.entity.IEntityItemUser
import temportalist.esotericraft.galvanization.common.task.ai.core.TaskBase
import temportalist.esotericraft.galvanization.common.task.ai.interfaces.ITaskInventory
import temportalist.origin.api.common.lib.Vect
import temportalist.origin.api.common.utility.Capabilities

/**
  *
  * Created by TheTemportalist on 6/6/2016.
  *
  * @author TheTemportalist
  */
@GalvanizeTask(modid = Details.MOD_ID,
	name = "itemExtract",
	displayName = "Take Items"
)
class TaskItemExtract(
		pos: BlockPos, face: EnumFacing
) extends TaskBase(pos, face) with ITaskInventory {

	private val speed: Double = 1.2D
	private val posVec = new Vect(this.pos) + Vect.CENTER + new Vect(this.face)

	// ~~~~~ Task Info ~~~~~

	override def getTaskType: EnumTaskType = EnumTaskType.WORLD_INTERACTION

	// ~~~~~ AI ~~~~~

	override def shouldExecute(entity: EntityCreature): Boolean = {

		for (hand <- EnumHand.values()) {
			if (entity.getHeldItem(hand) != null) return false
		}

		this.getFirstUsageStackSlot(entity) >= 0
	}

	def canEntityUseItem(entity: EntityLivingBase, slot: Int, inventory: IItemHandler = null): Boolean = {
		val inv = if (inventory != null) inventory else {
			val targetTile = entity.getEntityWorld.getTileEntity(this.getPosition)
			if (Capabilities.isInventory(targetTile, this.getFace))
				Capabilities.getInventory(targetTile, this.getFace)
			else null
		}
		if (inv == null) return false
		inv.getStackInSlot(slot) match {
			case stack: ItemStack => this.canEntityUseItem(entity, stack)
			case _ => // null
				false
		}
	}

	def canEntityUseItem(entity: EntityLivingBase, stack: ItemStack): Boolean = {
		entity match {
			case itemUser: IEntityItemUser =>
				val use = itemUser.canUse(stack)
				use
			case _ => false
		}
	}

	def getFirstUsageStackSlot(entity: EntityLivingBase): Int = {
		val targetTile = entity.getEntityWorld.getTileEntity(this.getPosition)
		if (Capabilities.isInventory(targetTile, this.getFace)) {
			val inventory = Capabilities.getInventory(targetTile, this.getFace)
			for (slot <- 0 until inventory.getSlots) {
				if (this.canEntityUseItem(entity, slot, inventory))
					return slot
			}
		}
		-1
	}

	override def updateTask(entity: EntityCreature): Unit = {

		val slot = this.getFirstUsageStackSlot(entity)
		if (slot < 0) return

		val position = new Vect(this.getPosition)
		val targetPosMove = position + Vect.CENTER
		val targetPosDist = position + Vect.CENTER.suppressAxisGet(Axis.Y)
		val ownerDistanceToInventory = (new Vect(entity) - targetPosDist).length
		if (ownerDistanceToInventory > 2D)
			this.moveEntityTowards(entity,
				targetPosMove.x, targetPosMove.y, targetPosMove.z,
				this.speed, this.getCanFly)
		else {
			if (this.canEntityUseItem(entity, slot))
				this.extractItem(entity, slot)
		}

	}

	def extractItem(entity: EntityLivingBase, slot: Int): Unit = {
		val targetTile = entity.getEntityWorld.getTileEntity(this.getPosition)
		if (Capabilities.isInventory(targetTile, this.getFace)) {
			val inventory = Capabilities.getInventory(targetTile, this.getFace)
			this.extractItem(entity, inventory, slot)
		}
	}

	def extractItem(entity: EntityLivingBase, inventory: IItemHandler, slot: Int): Unit ={
		var simStack = inventory.extractItem(slot, 1, true)
		if (simStack != null) {
			simStack = inventory.extractItem(slot, 1, false)
			entity.setHeldItem(EnumHand.MAIN_HAND, simStack.copy())
		}
	}

	// ~~~~~ End ~~~~~

}
