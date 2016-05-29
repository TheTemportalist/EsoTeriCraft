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
	var taskItem: Item = null
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

		val taskStack = new ItemStack(this.taskItem)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getTaskItemForAIClass(classOf[TaskAttack], taskStack),
			Items.IRON_SWORD, Items.MAP
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getTaskItemForAIClass(classOf[TaskFollowPlayer], taskStack),
			Items.MAP, Items.ROTTEN_FLESH
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getTaskItemForAIClass(classOf[TaskItemDeposit], taskStack),
			Items.MAP, Blocks.CHEST
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getTaskItemForAIClass(classOf[TaskHarvestCrops], taskStack),
			Items.MAP, Items.WHEAT_SEEDS
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getTaskItemForAIClass(classOf[TaskHarvestTree], taskStack),
			Items.MAP, Items.STONE_AXE
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getTaskItemForAIClass(classOf[TaskItemCollect], taskStack),
			Items.MAP, Items.STICK
		)

		GameRegistry.addShapelessRecipe(new ItemStack(this.debugTask),
			Items.STICK, Items.IRON_INGOT
		)

	}

}
