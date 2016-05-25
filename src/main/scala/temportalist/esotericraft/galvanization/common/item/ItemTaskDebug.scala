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
class ItemTaskDebug extends ItemCreative with INBTCreator {

	override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos,
			hand: EnumHand, facing: EnumFacing,
			hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
		val default = super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)

		ControllerTask.getTaskAt(worldIn, pos, facing) match {
			case task: ITask =>
				this.checkStackNBT(stack)
				stack.getTagCompound.setTag("task", {
					val tag = this.getCompoundNew
					tag.setTag("pos", new Vect(pos).serializeNBT())
					tag.setInteger("face", facing.ordinal())
					tag
				})
				Galvanize.log("Set task " + pos.toString + " " + facing)
				return EnumActionResult.SUCCESS
			case _ =>
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

					Galvanize.log("add task " + pos.toString + " " + face)
					empty.addTask(pos, face)
				}
			case _ =>
		}
		false
	}

}
