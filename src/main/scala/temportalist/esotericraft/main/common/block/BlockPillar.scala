package temportalist.esotericraft.main.common.block

import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import temportalist.esotericraft.main.common.EsoTeriCraft
import temportalist.esotericraft.main.common.block.BlockPillar._
import temportalist.esotericraft.main.common.tile.TilePillar
import temportalist.origin.api.common.block.BlockTile

/**
  *
  * Created by TheTemportalist on 5/4/2016.
  *
  * @author TheTemportalist
  */
class BlockPillar extends BlockTile(EsoTeriCraft, classOf[TilePillar]) {

	this.setDefaultState(
		this.getBlockState.getBaseState.
				withProperty(CORNER, Int.box(0)).
				withProperty(RENDER_STATE, Int.box(0))
	)

	override def createBlockState(): BlockStateContainer = {
		new BlockStateContainer(this, CORNER, RENDER_STATE)
	}

	override def getMetaFromState(state: IBlockState): Int = {
		/*
			0:  0  1  2  3
			1:  4  5  6  7
			2:  8  9 10 11
		 */
		state.getValue(CORNER) + (state.getValue(RENDER_STATE) * 4)
	}

	override def getStateFromMeta(meta: Int): IBlockState = {
		this.getBlockState.getBaseState.
				withProperty(CORNER, Int.box(meta % 4)).
				withProperty(RENDER_STATE, Int.box(meta / 4))
	}

	override def isOpaqueCube(state: IBlockState): Boolean = state.getValue(RENDER_STATE) == 0

	override def isFullCube(state: IBlockState): Boolean = state.getValue(RENDER_STATE) == 0

	override def getRenderType(state: IBlockState): EnumBlockRenderType = {
		if (state.getValue(RENDER_STATE) == 1) EnumBlockRenderType.INVISIBLE
		else EnumBlockRenderType.MODEL
	}

	override def breakBlock(worldIn: World, pos: BlockPos, state: IBlockState): Unit = {
		worldIn.getTileEntity(pos) match {
			case tile: TilePillar => tile.breakBlockPre(state)
			case _ =>
		}
		super.breakBlock(worldIn, pos, state)
	}

}
object BlockPillar {

	val CORNER: PropertyInteger = PropertyInteger.create("corner", 0, 3)
	val RENDER_STATE: PropertyInteger = PropertyInteger.create("render_state", 0, 2)

}
