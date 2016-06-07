package temportalist.esotericraft.galvanization.common.task.ai.status

import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler
import temportalist.esotericraft.api.galvanize.ai.{EnumTaskType, GalvanizeTask}
import temportalist.esotericraft.api.init.Details

import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 6/7/2016.
  *
  * @author TheTemportalist
  */
@GalvanizeTask(modid = Details.MOD_ID,
	name = "itemInsertFilter",
	displayName = "Deposit Items (Filtered)"
)
class TaskItemInsertFilter(
		pos: BlockPos, face: EnumFacing
) extends TaskItemInsert(pos, face) {

	// ~~~~~ Task Info ~~~~~

	override def getTaskType: EnumTaskType = EnumTaskType.STATUS_RELIANT

	// ~~~~~ AI ~~~~~

	private val filterList = ListBuffer[ItemStack]()

	override def onSpawn(world: World): Unit = {
		val tile = world.getTileEntity(this.getPosition)
		if (tile != null &&
				tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.getFace)) {
			val inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.getFace)

			for (slot <- 0 until inventory.getSlots) {
				var stack = inventory.getStackInSlot(slot)
				if (stack != null) {
					stack = stack.copy()
					stack.stackSize = 1
					filterList += stack
				}
			}

		}
	}

	override def canEntityUse(world: World, stack: ItemStack): Boolean = {
		for (template <- this.filterList) {
			if (ItemStack.areItemsEqual(template, stack) &&
					ItemStack.areItemStackTagsEqual(template, stack)) return true
		}
		false
	}

	// ~~~~~ End ~~~~~

}
