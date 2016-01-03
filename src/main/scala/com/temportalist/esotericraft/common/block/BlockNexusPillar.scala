package com.temportalist.esotericraft.common.block

import com.temportalist.esotericraft.common.init.ModBlocks
import com.temportalist.origin.api.common.block.BlockBase
import net.minecraft.util.EnumFacing
import com.temportalist.esotericraft.common.block.StateProperties._

/**
  * Created by TheTemportalist on 1/3/2016.
  */
class BlockNexusPillar extends BlockBase(ModBlocks) {

	this.setDefaultState(this.blockState.getBaseState.
			withProperty(FACING_AXIS_X, EnumFacing.WEST).
			withProperty(FACING_AXIS_Z, EnumFacing.SOUTH))



	override def isOpaqueCube: Boolean = false

	override def isFullCube: Boolean = false

	override def isVisuallyOpaque: Boolean = false

	override def getRenderType: Int = 3

}
