package temportalist.esotericraft.galvanization.common.init

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.{EnumDyeColor, ItemStack}
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.esotericraft.galvanization.common.item.{ItemEggGolem, ItemGalvanize, ItemTask, ItemTaskDebug}
import temportalist.esotericraft.galvanization.common.task.ai.active.{TaskAttack, TaskFollowPlayer}
import temportalist.esotericraft.galvanization.common.task.ai.status.{TaskItemInsert, TaskItemInsertFilter, TaskUsePlant}
import temportalist.esotericraft.galvanization.common.task.ai.world._
import temportalist.esotericraft.galvanization.common.task.core.ControllerTask
import temportalist.origin.foundation.common.registers.ItemRegister

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
object ModItems extends ItemRegister {

	var golemEgg: ItemGalvanize = null
	var taskItem: ItemTask = null
	var debugTask: ItemGalvanize = null

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
			Items.PAPER, Items.STONE_SWORD
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getNewItemStackForAIClass(classOf[TaskFollowPlayer]),
			Items.PAPER, Items.ROTTEN_FLESH
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
			Items.PAPER, new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage)
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getNewItemStackForAIClass(classOf[TaskItemInsert]),
			Items.PAPER, Blocks.CHEST
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getNewItemStackForAIClass(classOf[TaskMine]),
			Items.PAPER, Items.STONE_PICKAXE
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getNewItemStackForAIClass(classOf[TaskItemExtract]),
			Items.PAPER, Items.STICK
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getNewItemStackForAIClass(classOf[TaskUsePlant]),
			Items.PAPER, Blocks.SAPLING
		)
		GameRegistry.addShapelessRecipe(
			ControllerTask.getNewItemStackForAIClass(classOf[TaskItemInsertFilter]),
			Items.PAPER, Items.PAPER
		)

		GameRegistry.addShapelessRecipe(new ItemStack(this.debugTask),
			Items.STICK, Items.IRON_INGOT
		)

	}

}
