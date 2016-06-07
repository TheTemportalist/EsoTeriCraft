package temportalist.esotericraft.galvanization.common.item

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.entity.EntityEmpty
import temportalist.esotericraft.galvanization.common.task.core.ControllerTask
import temportalist.esotericraft.galvanization.common.task.{INBTCreator, ITask}
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 5/25/2016.
  *
  * @author TheTemportalist
  */
class ItemTaskDebug extends ItemGalvanizeRendering with INBTCreator {

	override def onItemUseFirst(stack: ItemStack,
			playerIn: EntityPlayer,
			worldIn: World, pos: BlockPos,
			facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float,
			hand: EnumHand): EnumActionResult = {
		val default = super
				.onItemUseFirst(stack, playerIn, worldIn, pos, facing, hitX, hitY, hitZ, hand)

		if (playerIn.isSneaking) {
			if (ControllerTask.breakTask(worldIn, pos, facing,
				drop = !playerIn.capabilities.isCreativeMode)) {
				return EnumActionResult.SUCCESS
			}
		}
		else {
			ControllerTask.getTaskAt(worldIn, pos, facing) match {
				case task: ITask =>
					this.checkStackNBT(stack)
					stack.getTagCompound.setTag("task", {
						val tag = this.getCompoundNew
						tag.setTag("pos", new Vect(pos).serializeNBT())
						tag.setInteger("face", facing.ordinal())
						tag
					})
					Galvanize.log(task.getName)
					return EnumActionResult.SUCCESS
				case _ =>
			}
		}

		default
	}

	override def itemInteractionForEntity(stack: ItemStack, playerIn: EntityPlayer,
			target: EntityLivingBase, hand: EnumHand): Boolean = {
		target match {
			case empty: EntityEmpty =>
				if (stack.hasTagCompound && stack.getTagCompound.hasKey("task")) {

					val tag = stack.getTagCompound.getCompoundTag("task")
					val pos = Vect.readFrom(tag, "pos").toBlockPos
					val face = EnumFacing.values()(tag.getInteger("face"))
					stack.getTagCompound.removeTag("task")

					Galvanize.log("add")
					empty.addTask(pos, face)
				}
			case _ =>
		}
		false
	}

}
