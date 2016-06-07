package temportalist.esotericraft.galvanization.common.item

import java.util

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util._
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.api.init.Details
import temportalist.esotericraft.galvanization.common.task.Task
import temportalist.esotericraft.galvanization.common.task.ai.core.LoaderTask
import temportalist.esotericraft.galvanization.common.task.core.ControllerTask

import scala.collection.JavaConversions
import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 5/24/2016.
  *
  * @author TheTemportalist
  */
class ItemTask extends ItemGalvanize {

	// ~~~~~~~~~~ World interactions

	override def onItemRightClick(itemStackIn: ItemStack, worldIn: World, playerIn: EntityPlayer,
			hand: EnumHand): ActionResult[ItemStack] = {
		super.onItemRightClick(itemStackIn, worldIn, playerIn, hand)
	}

	override def onItemUse(stackIn: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos,
			hand: EnumHand, facing: EnumFacing,
			hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
		val default = super.onItemUse(stackIn, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)

		var stack = stackIn
		if (!stack.hasTagCompound) return default

		if (!worldIn.isRemote) {

			val taskPos = pos
			val face = facing

			val className = stack.getTagCompound.getString("className")
			val displayName = stack.getTagCompound.getString("displayName")
			val registryName = stack.getTagCompound.getString("name")
			val classAI = LoaderTask.getClassFromName(className)
			val info = LoaderTask.getAnnotationInfo(classAI)
			val aiModID = info.getOrElse("modid", null)
			if (registryName == null || aiModID == null) return EnumActionResult.FAIL

			val task = new Task(worldIn)
			task.setPosition(taskPos, face)
			task.setInfoAI(aiModID.toString, registryName, displayName, classAI)
			if (ControllerTask.spawnTask(worldIn, taskPos, face, task)) {
				if (!playerIn.capabilities.isCreativeMode) {
					stack.stackSize -= 1
					if (stack.stackSize <= 0) stack = null
					playerIn.setHeldItem(hand, stack)
				}
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
			subItems.add(ControllerTask.getNewItemStackForAIClass(clazz))
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

	def getPossibleModelLocations: Seq[ResourceLocation] = {
		val locations = ListBuffer[ResourceLocation]()
		locations += new ModelResourceLocation(this.getRegistryName.toString, "inventory")

		val classesOf = LoaderTask.getClassInstances
		for (classOf <- classesOf) {
			val info = LoaderTask.getAnnotationInfo(classOf)
			val modid = info.getOrElse("modid", null).asInstanceOf[String]
			val nameVariant = info.getOrElse("name", null).asInstanceOf[String]
			if (modid != null && nameVariant != null) {
				locations += new ModelResourceLocation(modid + ":" + this.name, "task=" + nameVariant)
			}
		}

		locations
	}

	@SideOnly(Side.CLIENT)
	def getModelLocation(stack: ItemStack): ModelResourceLocation = {
		var modid = Details.MOD_ID
		var variant = "inventory"
		if (stack.hasTagCompound) {
			modid = stack.getTagCompound.getString("modid")
			variant = "task=" + stack.getTagCompound.getString("name")
		}
		new ModelResourceLocation(modid + ":" + this.name, variant)
	}

}
