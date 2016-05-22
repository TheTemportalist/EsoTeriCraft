package temportalist.esotericraft.galvanization.common.entity.ai

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.{ActionResult, EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import temportalist.esotericraft.api.galvanize.ai.EntityAIHelperObj
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 5/22/2016.
  *
  * @author TheTemportalist
  */
trait IEntityAIOriginHelper extends EntityAIHelperObj {

	override def onItemRightClick(itemStack: ItemStack, world: World, player: EntityPlayer,
			hand: EnumHand): ActionResult[ItemStack] = {
		val retStack = itemStack.copy()

		if (!retStack.hasTagCompound) retStack.setTagCompound(new NBTTagCompound)

		if (player.isSneaking) {
			if (retStack.getTagCompound.hasKey("origin")) {

				retStack.getTagCompound.removeTag("origin")
				if (retStack.getTagCompound.hasKey("face_ordinal"))
					retStack.getTagCompound.removeTag("face_ordinal")

				if (!player.getEntityWorld.isRemote)
					player.addChatMessage(new TextComponentString("Removed position"))

				if (retStack.getTagCompound.hasNoTags)
					retStack.setTagCompound(null)

				return new ActionResult[ItemStack](EnumActionResult.SUCCESS, retStack)
			}
		}

		new ActionResult[ItemStack](EnumActionResult.PASS, itemStack)
	}

	override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos,
			hand: EnumHand, facing: EnumFacing,
			hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {

		val origin = new Vect(pos)

		if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound)

		stack.getTagCompound.setTag("origin", origin.serializeNBT())
		stack.getTagCompound.setInteger("face_ordinal", facing.ordinal())

		if (!player.getEntityWorld.isRemote)
			player.addChatMessage(new TextComponentString("Set position to " +
					origin.x_i() + "," + origin.y_i() + "," + origin.z_i()))

		EnumActionResult.SUCCESS
	}

}
