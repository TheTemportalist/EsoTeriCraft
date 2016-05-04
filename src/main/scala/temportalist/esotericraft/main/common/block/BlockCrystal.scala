package temportalist.esotericraft.main.common.block

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.World
import temportalist.esotericraft.main.common.EsoTeriCraft
import temportalist.esotericraft.main.common.tile.TileCrystal
import temportalist.origin.api.common.block.BlockTile

/**
  *
  * Created by TheTemportalist on 5/4/2016.
  *
  * @author TheTemportalist
  */
class BlockCrystal extends BlockTile(EsoTeriCraft, classOf[TileCrystal]) {

	override def isOpaqueCube(state: IBlockState): Boolean = false

	override def isFullCube(state: IBlockState): Boolean = false

	override def isVisuallyOpaque: Boolean = false

	override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState,
			playerIn: EntityPlayer, hand: EnumHand, heldItem: ItemStack,
			side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {

		if (!playerIn.isSneaking) worldIn.getTileEntity(pos) match {
			case tile: TileCrystal =>
				tile.updateStructureStates(worldIn, pos)
				return true
			case _ =>
		}

		false
	}

}
