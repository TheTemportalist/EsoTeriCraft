package temportalist.esotericraft.galvanization.common.item

import java.util

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{ActionResult, EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.task.Task
import temportalist.esotericraft.galvanization.common.task.ai.core.LoaderTask
import temportalist.esotericraft.galvanization.common.task.core.ControllerTask

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 5/24/2016.
  *
  * @author TheTemportalist
  */
class ItemTask extends ItemCreative {

	// ~~~~~~~~~~ World interactions

	override def onItemRightClick(itemStackIn: ItemStack, worldIn: World, playerIn: EntityPlayer,
			hand: EnumHand): ActionResult[ItemStack] = {
		super.onItemRightClick(itemStackIn, worldIn, playerIn, hand)
	}

	override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos,
			hand: EnumHand, facing: EnumFacing,
			hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
		val default = super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)

		if (!stack.hasTagCompound) return default

		if (!worldIn.isRemote) {

			val taskPos = pos
			val face = facing

			val className = stack.getTagCompound.getString("className")
			val displayName = stack.getTagCompound.getString("displayName")
			val classAI = LoaderTask.getClassFromName(className)
			val info = LoaderTask.getAnnotationInfo(classAI)
			val registryName = info.getOrElse("name", null)
			val aiModID = info.getOrElse("modid", null)
			if (registryName == null || aiModID == null) return EnumActionResult.FAIL

			val task = new Task(worldIn)
			task.setPosition(taskPos, face)
			task.setInfoAI(aiModID.toString, registryName.toString, displayName, classAI)
			if (ControllerTask.spawnTask(worldIn, taskPos, face, task)) {
				Galvanize.log("placed")
				return EnumActionResult.SUCCESS
			}

		}

		default
	}

	// ~~~~~~~~~~ Multiple Item Handling

	@SideOnly(Side.CLIENT)
	override def getSubItems(itemIn: Item, tab: CreativeTabs,
			subItems: util.List[ItemStack]): Unit = {
		val classesOf = LoaderTask.getClassInstances
		for (clazz <- classesOf) {
			subItems.add(ControllerTask.getTaskItemForAIClass(clazz))
		}
	}

	override def getItemStackDisplayName(stack: ItemStack): String = {
		super.getItemStackDisplayName(stack) +
				(if (stack.hasTagCompound) ": " + stack.getTagCompound.getString("displayName") else "")
	}

	@SideOnly(Side.CLIENT)
	override def addInformation(stack: ItemStack, playerIn: EntityPlayer,
			tooltip: util.List[String], advanced: Boolean): Unit = {
		if (!stack.hasTagCompound) return

		if (advanced) {
			for (tagKey <- JavaConversions.asScalaSet(stack.getTagCompound.getKeySet)) {
				tooltip.add(tagKey + ": " + stack.getTagCompound.getTag(tagKey).toString)
			}
		}

	}

}
