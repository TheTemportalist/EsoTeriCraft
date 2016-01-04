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

		val crystal = new V3O(pos)
		for (corner <- 0 to 3) {
			val state = baseState.
					withProperty(BlockNexusPillar.PILLAR_CORNER, Int.box(corner)).
					withProperty(BlockNexusPillar.PILLAR_DO_RENDER, Boolean.box(true))
			val xz = corner match {
				case 0 => (+1, -1)
				case 1 => (+1, +1)
				case 2 => (-1, +1)
				case 3 => (-1, -1)
				case _ => (0, 0)
			}
			val pos = crystal.copy() + new V3O(xz._1 * 3, -4, xz._2 * 3)
			blockStatePositions += ((state, pos.toBlockPos))
			val hiddenState = state.withProperty(
				BlockNexusPillar.PILLAR_DO_RENDER, Boolean.box(false))
			def append(i: Int): Unit =
				blockStatePositions += ((hiddenState, (pos + (V3O.UP * i)).toBlockPos))
			for (i <- 1 to 3) append(i)
			pos += new V3O(-xz._1, 0, -xz._2)
			append(4)
			pos += new V3O(-xz._1, 0, -xz._2)
			append(4)
		}

		blockStatePositions.foreach(statePos => {
			world.setBlockState(statePos._2, statePos._1)
		})

	}

}
