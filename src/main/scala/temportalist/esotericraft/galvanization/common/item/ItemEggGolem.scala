package temportalist.esotericraft.galvanization.common.item

import java.util

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{EntityList, EntityLivingBase}
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing.Axis
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.common.capability.{HelperGalvanize, IPlayerGalvanize}
import temportalist.esotericraft.galvanization.common.entity.EntityEmpty
import temportalist.esotericraft.galvanization.common.item.ItemEggGolem._
import temportalist.origin.api.common.item.INBTHandler
import temportalist.origin.api.common.lib.Vect

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class ItemEggGolem extends ItemCreative() with INBTHandler {

	this.addKey[String](ENTITY_ID)

	override def itemInteractionForEntity(stack: ItemStack, playerIn: EntityPlayer,
			target: EntityLivingBase, hand: EnumHand): Boolean = {
		if (this.canUse(playerIn) && playerIn.isSneaking) {

			val tag = this.getTagOrElseSet(stack)
			tag.setString(ENTITY_ID, EntityList.getEntityString(target))
			stack.setTagCompound(tag)
			//val ret = this.set(stack, ENTITY_ID, EntityList.getEntityString(target))
			//Galvanize.log("" + ret)

			//Galvanize.log("" + stack.getTagCompound)

			playerIn.setHeldItem(hand, stack)
			true
		}
		else false
	}

	override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos,
			hand: EnumHand, facing: EnumFacing,
			hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
		if (stack.hasTagCompound && stack.getTagCompound.hasKey(ENTITY_ID)) {

			if (!worldIn.isRemote) {

				val entityName = this.get[String](stack, ENTITY_ID)

				val position = new Vect(pos).up() + Vect.CENTER.suppressAxisGet(Axis.Y)
				val empty = new EntityEmpty(worldIn, entityName, position)
				val ret = worldIn.spawnEntityInWorld(empty)

			}
			//this.removeTag(stack, ENTITY_ID)
			EnumActionResult.SUCCESS
		}
		else EnumActionResult.PASS
	}

	@SideOnly(Side.CLIENT) override
	def addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: util.List[String],
			advanced: Boolean): Unit = {
		if (stack.hasTagCompound && stack.getTagCompound.hasKey(ENTITY_ID)) {
			tooltip.add(this.get[String](stack, ENTITY_ID))
		}

		tooltip.add("")

		HelperGalvanize.get(playerIn) match {
			case galvanized: IPlayerGalvanize =>
				for (ability <-
				     JavaConversions.iterableAsScalaIterable(galvanized.getEntityAbilities)) {
					tooltip.add(ability.getName)
				}
			case _ => // null
		}


	}

}
object ItemEggGolem {
	val ENTITY_ID = "entityID"
}
