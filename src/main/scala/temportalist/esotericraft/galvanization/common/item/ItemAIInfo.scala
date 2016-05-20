package temportalist.esotericraft.galvanization.common.item
import java.util

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.common.entity.EntityEmpty
import temportalist.esotericraft.galvanization.common.entity.ai.AILoader

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
		val classesOfAI = AILoader.getClassInstances
		for (classAI <- classesOfAI) {
			subItems.add({
				val sub = stack.copy()
				sub.setTagCompound(new NBTTagCompound)
				val nameAI = AILoader.getAnnotationInfo(classAI).getOrElse("name", null)
				if (nameAI != null) sub.getTagCompound.setString("ai_name", nameAI.toString)
				sub.getTagCompound.setString("ai_class", classAI.getName)
				sub
			})
		}
	}

	@SideOnly(Side.CLIENT)
	override def addInformation(stack: ItemStack, playerIn: EntityPlayer,
			tooltip: util.List[String], advanced: Boolean): Unit = {
		if (!stack.hasTagCompound) return

		if (stack.getTagCompound.hasKey("ai_name"))
			tooltip.add(stack.getTagCompound.getString("ai_name"))
		if (advanced)
			tooltip.add(stack.getTagCompound.getString("ai_class"))

	}

	override def itemInteractionForEntity(stack: ItemStack, playerIn: EntityPlayer,
			target: EntityLivingBase, hand: EnumHand): Boolean = {
		target match {
			case empty: EntityEmpty =>
				if (!stack.hasTagCompound) return false

				val classNameOfAI = stack.getTagCompound.getString("ai_class")
				try {
					val classOfAI = Class.forName(classNameOfAI).asInstanceOf[Class[_ <: EntityAIBase]]
					val priority = empty.tasks.taskEntries.size()
					val classParameters = AILoader.getAnnotationInfo(classOfAI).getOrElse(
						"parameters", null).asInstanceOf[Array[Class[_]]]
					if (classParameters == null) return false
					val ai = this.createInstanceOfAI(classOfAI, classParameters)

					empty.tasks.addTask(priority, ai)
					true
				}
				catch {
					case e: Exception => false
				}

			case _ => super.itemInteractionForEntity(stack, playerIn, target, hand)
		}
	}

	private def createInstanceOfAI(classOfAI: Class[_ <: EntityAIBase],
			parameterClasses: Array[Class[_]]): EntityAIBase = {
		null
	}

}
