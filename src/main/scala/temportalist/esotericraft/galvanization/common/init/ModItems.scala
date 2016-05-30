package temportalist.esotericraft.galvanization.common.init

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.esotericraft.galvanization.common.item.{ItemEggGolem, ItemTask, ItemTaskDebug}
import temportalist.esotericraft.galvanization.common.task.ai.active.{TaskAttack, TaskFollowPlayer}
import temportalist.esotericraft.galvanization.common.task.ai.status.TaskItemDeposit
import temportalist.esotericraft.galvanization.common.task.ai.world.{TaskHarvestCrops, TaskHarvestTree, TaskItemCollect}
import temportalist.esotericraft.galvanization.common.task.core.ControllerTask
import temportalist.origin.foundation.common.registers.ItemRegister

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
object ModItems extends ItemRegister {

	var golemEgg: Item = null
	var taskItem: ItemTask = null
	var debugTask: Item = null

	override def register(): Unit = {

		this.golemEgg = new ItemEggGolem
		this.golemEgg.setCreativeTab(CreativeTabs.MISC)

		this.taskItem = new ItemTask
		this.taskItem.setCreativeTab(CreativeTabs.MISC)

		this.debugTask = new ItemTaskDebug
		this.debugTask.setCreativeTab(CreativeTabs.MISC)

	}

	override def registerCrafting(): Unit = {

		GameRegistry.addRecipe(new ItemStack(this.golemEgg),
			"fff", "fef", "fff",
			Char.box('f'), Items.ROTTEN_FLESH,
			Char.box('e'), Items.EGG
		)

		GameRegistry.addShapelessRecipe(
			ControllerTask.getNewItemStackForAIClass(classOf[TaskAttack]),
			Items.IRON_SWORD, Items.PAPER
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getNewItemStackForAIClass(classOf[TaskFollowPlayer]),
			Items.PAPER, Items.ROTTEN_FLESH
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getNewItemStackForAIClass(classOf[TaskItemDeposit]),
			Items.PAPER, Blocks.CHEST
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getNewItemStackForAIClass(classOf[TaskHarvestCrops]),
			Items.PAPER, Items.WHEAT_SEEDS
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getNewItemStackForAIClass(classOf[TaskHarvestTree]),
			Items.PAPER, Items.STONE_AXE
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getNewItemStackForAIClass(classOf[TaskItemCollect]),
			Items.PAPER, Items.STICK
		)

		GameRegistry.addShapelessRecipe(new ItemStack(this.debugTask),
			Items.STICK, Items.IRON_INGOT
		)

	}

}
