package temportalist.esotericraft.galvanization.common.task.ai.interfaces

import net.minecraft.entity.EntityLivingBase
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraftforge.items.IItemHandler

/**
  *
  * Created by TheTemportalist on 5/19/2016.
  *
  * @author TheTemportalist
  */
trait ITaskInventory {

	final def transferStackTo(fromStack: ItemStack, toInv: IInventory): ItemStack = {
		if (fromStack == null) return null

		var toStack: ItemStack = null
		for (toSlot <- 0 until toInv.getSizeInventory) {
			toStack = toInv.getStackInSlot(toSlot)

			if (toStack == null) {
				toInv.setInventorySlotContents(toSlot, fromStack)
				return null
			}
			else if (toStack.stackSize < toStack.getMaxStackSize) {

				if (ItemStack.areItemsEqual(fromStack, toStack) &&
						ItemStack.areItemStackTagsEqual(fromStack, toStack)) {

					val availableSize = toStack.getMaxStackSize - toStack.stackSize
					val amtInserted = Math.min(availableSize, fromStack.stackSize)
					fromStack.stackSize -= amtInserted
					toStack.stackSize += amtInserted
					toInv.setInventorySlotContents(toSlot, toStack)
					if (fromStack.stackSize <= 0) return null

				}

			}
		}

		fromStack
	}

	final def transferStackTo(fromStack: ItemStack, toInv: IItemHandler): ItemStack = {
		if (fromStack == null) return null

		for (toSlot <- 0 until toInv.getSlots) {
			val remainder = toInv.insertItem(toSlot, fromStack, true)
			if (remainder == null || remainder.stackSize != fromStack.stackSize) {
				return toInv.insertItem(toSlot, fromStack, false)
			}
		}

		fromStack
	}

	final def addItemToHands(stackIn: ItemStack, entity: EntityLivingBase): ItemStack = {
		if (stackIn == null) return null

		var stackHeld: ItemStack = null
		val stack = stackIn
		for (hand <- EnumHand.values()) {
			stackHeld = entity.getHeldItem(hand)
			if (stackHeld == null) {
				entity.setHeldItem(hand, stack)
				return null
			}
			else if (stackHeld.stackSize < stackHeld.getMaxStackSize) {
				if (ItemStack.areItemsEqual(stackHeld, stack) &&
						ItemStack.areItemStackTagsEqual(stackHeld, stack)) {
					val availableSize = stackHeld.getMaxStackSize - stackHeld.stackSize
					val amtInserted = Math.min(availableSize, stack.stackSize)
					stack.stackSize -= amtInserted
					stackHeld.stackSize += amtInserted
					entity.setHeldItem(hand, stackHeld)
					if (stack.stackSize <= 0) return null
				}
			}
		}

		stack
	}

}
