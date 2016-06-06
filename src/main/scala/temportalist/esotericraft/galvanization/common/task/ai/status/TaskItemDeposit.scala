package temportalist.esotericraft.galvanization.common.task.ai.status

import net.minecraft.entity.EntityCreature
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import temportalist.esotericraft.api.galvanize.ai.{EnumTaskType, GalvanizeTask}
import temportalist.esotericraft.api.init.Details
import temportalist.esotericraft.galvanization.common.task.ai.core.TaskBase
import temportalist.esotericraft.galvanization.common.task.ai.interfaces.ITaskInventory
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 5/25/2016.
  *
  * @author TheTemportalist
  */
@GalvanizeTask(modid = Details.MOD_ID,
	name = "itemDeposit",
	displayName = "Deposit Items"
)
class TaskItemDeposit(
		pos: BlockPos, face: EnumFacing
) extends TaskBase(pos, face) with ITaskInventory {

	private val speed: Double = 1.2D
	private val posVec = new Vect(this.pos) + new Vect(this.face) * 0.5

	// ~~~~~ Task Info ~~~~~

	override def getTaskType: EnumTaskType = EnumTaskType.STATUS_RELIANT

	// ~~~~~ AI ~~~~~

	override def shouldExecute(entity: EntityCreature): Boolean = {
		entity.getEntityWorld.getTileEntity(this.pos) match {
			case inv: IInventory => // Block at target pos is an inventory
			case _ => return false
		}

		//Galvanize.log("should?")

		// Check if there is SOMETHING being held
		for (hand <- EnumHand.values())
			if (entity.getHeldItem(hand) != null) {
				return true
			}

		false
	}

	override def updateTask(entity: EntityCreature): Unit = {

		var hasItem = false
		for (hand <- EnumHand.values()) {
			if (entity.getHeldItem(hand) != null) hasItem = true
		}
		if (!hasItem) return

		val position = this.posVec
		val ownerDistanceToInventory = (new Vect(entity) - position).length
		if (ownerDistanceToInventory > 1.25D)
			this.moveEntityTowards(entity,
				position.x, position.y, position.z,
				this.speed, this.getCanFly)
		else {
			this.depositItems(entity)
		}

	}

	final def depositItems(entity: EntityCreature): Unit = {
		entity.getEntityWorld.getTileEntity(this.pos) match {
			case toInv: IInventory =>
				var fromStack: ItemStack = null
				for (hand <- EnumHand.values()) {
					fromStack = entity.getHeldItem(hand)
					if (fromStack != null) {
						fromStack = this.transferStackTo(fromStack.copy(), toInv)
						entity.setHeldItem(hand, fromStack)
					}
				}
			case _ =>
		}
	}

	// ~~~~~ End ~~~~~

}
