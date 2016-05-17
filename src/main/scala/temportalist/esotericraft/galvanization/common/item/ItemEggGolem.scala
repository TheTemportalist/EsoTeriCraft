package temportalist.esotericraft.galvanization.common.item

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{EntityList, EntityLivingBase}
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.entity.EntityEmpty
import temportalist.esotericraft.galvanization.common.item.ItemEggGolem._
import temportalist.origin.api.common.item.INBTHandler

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

			Galvanize.log(EntityList.getEntityString(target))

			val tag = this.getTagOrElseSet(stack)
			tag.setString(ENTITY_ID, EntityList.getEntityString(target))
			stack.setTagCompound(tag)
			//val ret = this.set(stack, ENTITY_ID, EntityList.getEntityString(target))
			//Galvanize.log("" + ret)

			Galvanize.log("" + stack.getTagCompound)

			playerIn.setHeldItem(hand, stack)
			true
		}
		else false
	}

	override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos,
			hand: EnumHand, facing: EnumFacing,
			hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
		if (stack.getTagCompound.hasKey(ENTITY_ID)) {

			if (!worldIn.isRemote) {

				val entityName = this.get[String](stack, ENTITY_ID)
				Galvanize.log(entityName)

				Galvanize.log("add empty")

				val empty = new EntityEmpty(worldIn, entityName = entityName)
				empty.setPosition(pos.getX + 0.5, pos.getY + 1, pos.getZ + 0.5)
				val ret = worldIn.spawnEntityInWorld(empty)
				Galvanize.log("" + ret)

			}
			//this.removeTag(stack, ENTITY_ID)
			EnumActionResult.SUCCESS
		}
		else EnumActionResult.PASS
	}

}
object ItemEggGolem {
	val ENTITY_ID = "entityID"
}
