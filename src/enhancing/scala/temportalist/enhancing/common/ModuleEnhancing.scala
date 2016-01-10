package temportalist.enhancing.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import temportalist.enhancing.common.init.ModBlocks
import temportalist.esotericraft.api.EsotericraftModule
import temportalist.esotericraft.common.tile.TENexusCrystal
import temportalist.origin.api.common.lib.V3O
import temportalist.origin.api.common.utility.Stacks

/**
  * Created by TheTemportalist on 1/4/2016.
  */
object ModuleEnhancing extends EsotericraftModule {

	override def onImpartingFinished(player: EntityPlayer,
			nexusTile: TENexusCrystal): Boolean = {
		this.setTable(player, nexusTile, didComplete = true)
		true
	}

	override def onImpartingInterrupted(player: EntityPlayer, nexusTile: TENexusCrystal,
			percentageDone: Float): Unit = {
		this.setTable(player, nexusTile, didComplete = false)
	}

	def setTable(player: EntityPlayer, nexusTile: TENexusCrystal, didComplete: Boolean): Unit = {
		/*
		val stack = new ItemStack(
			if (didComplete) ModBlocks.enhancementBlock else Blocks.enchanting_table)
		if (!player.inventory.addItemStackToInventory(stack))
			Stacks.spawnItemStack(nexusTile.getWorld, new V3O(nexusTile) + V3O.DOWN,
				stack, nexusTile.getWorld.rand)
		*/
		nexusTile.getWorld.setBlockState(nexusTile.getPos.down(5),
			if (didComplete) ModBlocks.enhancementBlock.getBlockState.getBaseState
			else Blocks.enchanting_table.getBlockState.getBaseState)
	}

}
