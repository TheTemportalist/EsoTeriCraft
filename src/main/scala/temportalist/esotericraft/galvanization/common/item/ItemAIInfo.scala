package temportalist.esotericraft.galvanization.common.item

import java.util

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{EntityCreature, EntityLivingBase}
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.{ActionResult, EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.api.galvanize.ai.EntityAIEmpty
import temportalist.esotericraft.galvanization.common.entity.ai.LoaderAI

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 5/20/2016.
  *
  * @author TheTemportalist
  */
class ItemAIInfo extends ItemCreative {

	@SideOnly(Side.CLIENT)
	override def getSubItems(itemIn: Item, tab: CreativeTabs,
			subItems: util.List[ItemStack]): Unit = {
		val stack = new ItemStack(itemIn)
		val classesOfAI = LoaderAI.getClassInstances
		for (classAI <- classesOfAI) {
			subItems.add({
				val sub = stack.copy()
				sub.setTagCompound(new NBTTagCompound)
				val nameAI = LoaderAI.getAnnotationInfo(classAI).getOrElse("displayName", null)
				if (nameAI != null) sub.getTagCompound.setString("ai_name", nameAI.toString)
				sub.getTagCompound.setString("ai_class", classAI.getName)
				sub
			})
		}
	}

	override def getItemStackDisplayName(stack: ItemStack): String = {
		super.getItemStackDisplayName(stack) +
				(if (stack.hasTagCompound) ": " + stack.getTagCompound.getString("ai_name") else "")
	}

	@SideOnly(Side.CLIENT)
	override def addInformation(stack: ItemStack, playerIn: EntityPlayer,
			tooltip: util.List[String], advanced: Boolean): Unit = {
		if (!stack.hasTagCompound) return

		for (tagKey <- JavaConversions.asScalaSet(stack.getTagCompound.getKeySet)) {
			tooltip.add(tagKey + ": " + stack.getTagCompound.getTag(tagKey).toString)
		}

	}

	override def itemInteractionForEntity(stack: ItemStack, playerIn: EntityPlayer,
			target: EntityLivingBase, hand: EnumHand): Boolean = {

		if (!stack.hasTagCompound) return false

		target match {
			case creature: EntityCreature =>

				val classNameOfAI = stack.getTagCompound.getString("ai_class")
				try {
					val classOfAI = Class.forName(
						classNameOfAI).asInstanceOf[Class[_ <: EntityAIBase]]

					var entriesRemoved = 0
					// cloned so that the underlying map isn't affected by iteration,
					// and this iteration isn't affected by task removal
					val taskEntries = JavaConversions.asScalaSet(creature.tasks.taskEntries).clone()
					for (taskEntry <- taskEntries) {
						if (classOfAI.isAssignableFrom(taskEntry.action.getClass)) {

							if (!target.getEntityWorld.isRemote) {
								playerIn.addChatMessage(new TextComponentString(
									"Removing AI task " + classOfAI.getSimpleName +
											" with priority " + taskEntry.priority))
							}

							creature.tasks.taskEntries.remove(taskEntry)
							entriesRemoved += 1
						}
						else if (entriesRemoved > 0) {
							creature.tasks.removeTask(taskEntry.action)
							creature.tasks.addTask(taskEntry.priority - entriesRemoved, taskEntry.action)
						}
					}
					if (entriesRemoved > 0) return true

					val priority = creature.tasks.taskEntries.size()
					val ai = classOfAI.getConstructor(classOf[EntityCreature]).newInstance(creature)

					if (ai != null) {
						ai.asInstanceOf[EntityAIEmpty].initWith(stack)
						creature.tasks.addTask(priority, ai)
						return true
					}

				}
				catch {
					case e: Exception => e.printStackTrace()
				}

			case _ => return super.itemInteractionForEntity(stack, playerIn, target, hand)
		}

		false
	}

	override def onItemRightClick(stack: ItemStack, worldIn: World, playerIn: EntityPlayer,
			hand: EnumHand): ActionResult[ItemStack] = {
		val pass = super.onItemRightClick(stack, worldIn, playerIn, hand)

		if (!stack.hasTagCompound) return pass

		val classNameOfAI = stack.getTagCompound.getString("ai_class")
		val helper = LoaderAI.getHelperForAIClass(classNameOfAI)
		if (helper == null) return pass

		helper.onItemRightClick(stack, worldIn, playerIn, hand)
	}

	override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos,
			hand: EnumHand, facing: EnumFacing,
			hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
		val pass = super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)

		if (!stack.hasTagCompound) return pass

		val classNameOfAI = stack.getTagCompound.getString("ai_class")
		val helper = LoaderAI.getHelperForAIClass(classNameOfAI)
		if (helper == null) return pass

		helper.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)
	}

}
