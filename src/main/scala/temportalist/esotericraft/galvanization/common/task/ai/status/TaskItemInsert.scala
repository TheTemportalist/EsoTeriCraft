package temportalist.esotericraft.galvanization.common.task.ai.status

import net.minecraft.entity.EntityCreature
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler
import temportalist.esotericraft.api.galvanize.ai.{EnumTaskType, GalvanizeTask}
import temportalist.esotericraft.api.init.Details
import temportalist.esotericraft.galvanization.common.task.ai.core.TaskBase
import temportalist.esotericraft.galvanization.common.task.ai.interfaces.ITaskInventory
import temportalist.esotericraft.main.common.api.Capabilities
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
class TaskItemInsert(
		pos: BlockPos, face: EnumFacing
) extends TaskBase(pos, face) with ITaskInventory {

	private val speed: Double = 1.2D
	private val posVec = new Vect(this.pos) + Vect.CENTER + new Vect(this.face)

	// ~~~~~ Task Info ~~~~~

	override def getTaskType: EnumTaskType = EnumTaskType.STATUS_RELIANT

	// ~~~~~ AI ~~~~~

	override def canEntityUse(world: World, stack: ItemStack): Boolean = true

	override def shouldExecute(entity: EntityCreature): Boolean = {

		val tile = entity.getEntityWorld.getTileEntity(this.getPosition)
		if (tile == null ||
				!tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.getFace)) {
			return false
		}

		// Check if there is SOMETHING being held
		for (hand <- EnumHand.values()) {
			if (entity.getHeldItem(hand) != null) return true
			entity.getHeldItem(hand) match {
				case stack: ItemStack =>
					if (this.canEntityUse(entity.getEntityWorld, stack))
						return true
				case _ => // null
			}
		}

		false
	}

	override def updateTask(entity: EntityCreature): Unit = {

		var hasItem = false
		for (hand <- EnumHand.values()) {
			if (entity.getHeldItem(hand) != null) hasItem = true
		}
		if (!hasItem) return

		val position = new Vect(this.pos)
		val ownerDistanceToInventory = (new Vect(entity) - position).length
		if (ownerDistanceToInventory > 2D)
			this.moveEntityTowards(entity,
				this.posVec.x, this.posVec.y, this.posVec.z,
				this.speed, this.getCanFly)
		else {
			this.depositItems(entity)
		}

	}

	final def depositItems(entity: EntityCreature): Unit = {
		val targetTile = entity.getEntityWorld.getTileEntity(this.getPosition)
		if (Capabilities.isInventory(targetTile, this.getFace)) {
			val toInv = Capabilities.getInventory(targetTile, this.getFace)

			var fromStack: ItemStack = null
			for (hand <- EnumHand.values()) {
				fromStack = entity.getHeldItem(hand)
				if (fromStack != null && this.canEntityUse(entity.getEntityWorld, fromStack)) {
					fromStack = this.transferStackTo(fromStack.copy(), toInv)
					entity.setHeldItem(hand, fromStack)
				}
			}

		}
	}

	// ~~~~~ End ~~~~~

}
