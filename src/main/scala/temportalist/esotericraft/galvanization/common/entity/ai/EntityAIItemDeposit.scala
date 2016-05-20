package temportalist.esotericraft.galvanization.common.entity.ai

import net.minecraft.entity.EntityCreature
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import temportalist.esotericraft.api.galvanize.ai.AIEmpty
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 5/19/2016.
  *
  * @author TheTemportalist
  */
@AIEmpty(name = "Deposit Items")
class EntityAIItemDeposit[O <: EntityCreature](
		private val owner: O,
		private val inventoryPos: Vect,
		private val speed: Double = 1D,
		private val canFly: Boolean = false
) extends EntityAIHelper with IEntityAIInventory {

	this.setMutexBits(EnumAIMutex.EVERYTHING_OKAY)

	override def shouldExecute(): Boolean = {
		this.inventoryPos.getTile(this.owner.getEntityWorld) match {
			case inv: IInventory => // Block at target pos is an inventory
			case _ =>
				return false
		}

		// Check if there is SOMETHING being held
		for (hand <- EnumHand.values())
			if (this.owner.getHeldItem(hand) != null) {
				return true
			}

		false
	}

	override def updateTask(): Unit = {

		var hasItem = false
		for (hand <- EnumHand.values()) {
			if (this.owner.getHeldItem(hand) != null) hasItem = true
		}
		if (!hasItem) return

		val ownerDistanceToInventory = (new Vect(this.owner) - this.inventoryPos).length
		if (ownerDistanceToInventory > 1.25D)
			this.moveEntityTowards(this.owner,
				this.inventoryPos.x, this.inventoryPos.y, this.inventoryPos.z,
				this.speed, this.canFly)
		else {
			this.depositItems()
		}
	}

	final def depositItems(): Unit = {
		this.inventoryPos.getTile(this.owner.getEntityWorld) match {
			case toInv: IInventory =>
				var fromStack: ItemStack = null
				for (hand <- EnumHand.values()) {
					fromStack = this.owner.getHeldItem(hand)
					if (fromStack != null) {
						fromStack = this.transferStackTo(fromStack.copy(), toInv)
						this.owner.setHeldItem(hand, fromStack)
					}
				}
			case _ =>
		}
	}

}
