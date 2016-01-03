package com.temportalist.esotericraft.common.block

import com.temportalist.esotericraft.common.init.ModBlocks
import com.temportalist.esotericraft.common.tile.TENexusCrystal
import com.temportalist.origin.api.common.block.BlockTile
import com.temportalist.origin.api.common.lib.V3O
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{EnumFacing, BlockPos}
import net.minecraft.world.World

import scala.collection.mutable.ListBuffer

/**
  * Created by TheTemportalist on 1/2/2016.
  */
class BlockNexusCrystal extends BlockTile(ModBlocks, classOf[TENexusCrystal]) {

	override def isOpaqueCube: Boolean = false

	override def isFullCube: Boolean = false

	override def isVisuallyOpaque: Boolean = false

	override def getRenderType: Int = 3

	override def getMetaFromState(state: IBlockState): Int = 0

	override def hasCustomItemModel: Boolean = true

	override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState,
			playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float,
			hitZ: Float): Boolean = {
		this.constructStructure(worldIn, pos)
		true
	}

	def constructStructure(world: World, pos: BlockPos): Unit = {
		val blockStatePositions = ListBuffer[(IBlockState, BlockPos)]()
		val baseState = ModBlocks.nexusPillar.getBlockState.getBaseState
		def getBlockState(facingX: EnumFacing, facingZ: EnumFacing, height: Int): IBlockState = {
			baseState.
					withProperty(StateProperties.FACING_AXIS_X, facingX).
					withProperty(StateProperties.FACING_AXIS_Z, facingZ).
					withProperty(BlockNexusPillar.PILLAR_HEIGHT, Int.box(height))
		}

		val crystal = new V3O(pos)
		V3O.AXIS_FACING(EnumFacing.Axis.X).foreach(facingX => {
			V3O.AXIS_FACING(EnumFacing.Axis.Z).foreach(facingZ => {
				for (height <- 0 to 3) {
					val state = getBlockState(facingX, facingZ, height)
					val pos = crystal.copy() + new V3O(
						-facingX.getFrontOffsetX * 3,
						-4 + height,
						-facingZ.getFrontOffsetZ * 3)
					blockStatePositions += ((state, pos.toBlockPos))
				}
			})
		})

		blockStatePositions.foreach(statePos => {
			world.setBlockState(statePos._2, statePos._1)
		})

	}

}
