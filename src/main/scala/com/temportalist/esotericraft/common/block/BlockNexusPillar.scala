package com.temportalist.esotericraft.common.block

import com.temportalist.esotericraft.common.block.BlockNexusPillar._
import com.temportalist.esotericraft.common.block.StateProperties._
import com.temportalist.esotericraft.common.init.ModBlocks
import com.temportalist.origin.api.common.block.BlockBase
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

/**
  * Created by TheTemportalist on 1/3/2016.
  */
class BlockNexusPillar extends BlockBase(ModBlocks) {

	this.setDefaultState(this.blockState.getBaseState.
			withProperty(FACING_AXIS_X, EnumFacing.WEST).
			withProperty(FACING_AXIS_Z, EnumFacing.SOUTH).
			withProperty(PILLAR_HEIGHT, Int.box(0)))

	override def onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState,
			placer: EntityLivingBase, stack: ItemStack): Unit = {
		val facingHorizontal = placer.getHorizontalFacing.getOpposite
		var facingX: EnumFacing = null
		var facingZ: EnumFacing = null
		facingHorizontal.getAxis match {
			case EnumFacing.Axis.X =>
				facingX = facingHorizontal
				facingZ = if (placer.isSneaking) EnumFacing.NORTH else EnumFacing.SOUTH
			case EnumFacing.Axis.Z =>
				facingX = if (placer.isSneaking) EnumFacing.WEST else EnumFacing.EAST
				facingZ = facingHorizontal
			case _ =>
		}
		if (facingX != null && facingZ != null) {
			worldIn.setBlockState(pos,
				state.withProperty(FACING_AXIS_X, facingX).withProperty(FACING_AXIS_Z, facingZ))
		}
	}

	override def createBlockState(): BlockState = {
		println("Facing X " + FACING_AXIS_Z.getClass.getSimpleName)
		println("Facing X " + PILLAR_HEIGHT.getClass.getSimpleName)
		new BlockState(this, FACING_AXIS_X, FACING_AXIS_Z, PILLAR_HEIGHT)
	}

	override def getMetaFromState(state: IBlockState): Int = {
		/*
		WS
		WS
		WS
		WS

		WN
		WN
		WN
		WN

		ES
		ES
		ES
		ES

		EN
		EN
		EN
		EN
		*/

		(if (state.getValue(FACING_AXIS_X) == EnumFacing.WEST) 0 else 8) +
				(if (state.getValue(FACING_AXIS_Z) == EnumFacing.SOUTH) 0 else 4) +
				state.getValue(PILLAR_HEIGHT)
	}

	override def getStateFromMeta(meta: Int): IBlockState = {
		this.getBlockState.getBaseState.
				withProperty(FACING_AXIS_X,
					if (meta >= 8) EnumFacing.WEST else EnumFacing.EAST).
				withProperty(FACING_AXIS_Z,
					if ((meta / 4) % 2 == 0) EnumFacing.SOUTH else EnumFacing.NORTH).
				withProperty(PILLAR_HEIGHT, Int.box(meta % 4))
	}

	override def isOpaqueCube: Boolean = false

	override def isFullCube: Boolean = false

	override def isVisuallyOpaque: Boolean = false

	override def getRenderType: Int = 3

}
object BlockNexusPillar {
	val PILLAR_HEIGHT = StateProperties.createPropInt("pillar_height", 0, 3)
}
