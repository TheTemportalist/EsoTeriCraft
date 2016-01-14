package temportalist.esotericraft.common.block

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World
import temportalist.esotericraft.common.init.ModBlocks
import temportalist.esotericraft.common.tile.TENexusCrystal
import temportalist.origin.api.common.block.BlockTile

/**
  * Created by TheTemportalist on 1/2/2016.
  */
class BlockNexusCrystal extends BlockTile(ModBlocks, classOf[TENexusCrystal]) {

	override def isOpaqueCube: Boolean = false

	override def isFullCube: Boolean = false

	override def isVisuallyOpaque: Boolean = false

	override def getRenderType: Int = 3

	override def hasCustomItemModel: Boolean = true

	override def usesOBJ: Boolean = true

	override def onBlockAdded(world: World, pos: BlockPos, state: IBlockState): Unit = {
		// todo set prongs to multiblock state
	}

	override def removedByPlayer(world: World, pos: BlockPos, player: EntityPlayer,
			willHarvest: Boolean): Boolean = {
		//return false
		if (super.removedByPlayer(world, pos, player, willHarvest)) {
			// todo set prongs to basic stone state
			true
		} else false
	}

	override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState,
			playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float,
			hitZ: Float): Boolean = {
		worldIn.getTileEntity(pos) match {
			case crystal: TENexusCrystal =>
				crystal.tryDoEffect(playerIn)
			case _ => false
		}
	}

}
