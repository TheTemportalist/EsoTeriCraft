package com.temportalist.esotericenhancing.common.block

import com.temportalist.esotericenhancing.common.Enhancing
import com.temportalist.origin.api.common.block.BlockBase
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{EnumFacing, BlockPos}
import net.minecraft.world.World

/**
  * Created by TheTemportalist on 12/31/2015.
  */
class BlockEnhancement extends BlockBase(Enhancing.getModID, "enhancement") {

	override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState,
			player: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float,
			hitZ: Float): Boolean = {
		if (player.isSneaking) return false
		player.openGui(Enhancing, Enhancing.GUI_ENHANCEMENT, world, pos.getX, pos.getY, pos.getZ)
		true
	}

}
