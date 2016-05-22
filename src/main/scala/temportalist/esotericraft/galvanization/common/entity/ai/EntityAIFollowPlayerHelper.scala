package temportalist.esotericraft.galvanization.common.entity.ai

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{ActionResult, EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import temportalist.esotericraft.api.galvanize.ai.{AIEmptyHelper, EntityAIEmpty, EntityAIHelperObj}

/**
  *
  * Created by TheTemportalist on 5/22/2016.
  *
  * @author TheTemportalist
  */
@AIEmptyHelper
class EntityAIFollowPlayerHelper extends EntityAIHelperObj {

	override def getClassAI: Class[_ <: EntityAIEmpty] = classOf[EntityAIFollowPlayer]

	override def onItemRightClick(itemStack: ItemStack, world: World, player: EntityPlayer,
			hand: EnumHand): ActionResult[ItemStack] = {

		new ActionResult[ItemStack](EnumActionResult.PASS, itemStack)
	}

	override def onItemUse(itemStack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos,
			hand: EnumHand, facing: EnumFacing,
			hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {

		EnumActionResult.PASS
	}

}
