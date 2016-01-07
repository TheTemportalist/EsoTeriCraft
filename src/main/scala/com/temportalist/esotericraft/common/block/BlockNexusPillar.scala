package com.temportalist.esotericraft.common.block

import com.temportalist.esotericraft.common.block.BlockNexusPillar._
import com.temportalist.esotericraft.common.init.ModBlocks
import com.temportalist.origin.api.common.block.BlockBase
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.BlockPos
import net.minecraft.world.World

/**
  * Created by TheTemportalist on 1/3/2016.
  */
class BlockNexusPillar extends BlockBase(ModBlocks, mat = Material.rock) {

	this.setDefaultState(this.blockState.getBaseState.
			withProperty(PILLAR_CORNER, Int.box(0)).
			withProperty(PILLAR_DO_RENDER, Boolean.box(true)))
	this.setBlockUnbreakable()
	this.setResistance(6000000.0F)
	this.disableStats()

	/*
	override def onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState,
			placer: EntityLivingBase, stack: ItemStack): Unit = {
		val facingHorizontal = placer.getHorizontalFacing
		val corner = facingHorizontal match {
			case EnumFacing.NORTH =>
				if (placer.isSneaking) 1 else 2
			case EnumFacing.SOUTH =>
				if (placer.isSneaking) 3 else 0
			case EnumFacing.EAST =>
				if (placer.isSneaking) 0 else 1
			case EnumFacing.WEST =>
				if (placer.isSneaking) 2 else 3
			case _ => -1
		}
		if (corner >= 0) {
			val cornerState = state.withProperty(PILLAR_CORNER, Int.box(corner))
			for (i <- 0 to 3)
				worldIn.setBlockState(pos, cornerState.withProperty(PILLAR_HEIGHT, Int.box(i)))
		}
	}
	*/

	override def removedByPlayer(world: World, pos: BlockPos, player: EntityPlayer,
			willHarvest: Boolean): Boolean = false

	override def createBlockState(): BlockState = {
		new BlockState(this, PILLAR_CORNER, PILLAR_DO_RENDER)
	}

	override def getMetaFromState(state: IBlockState): Int = {
		if (!state.getValue(PILLAR_DO_RENDER)) 4
		else state.getValue(PILLAR_CORNER)
	}

	override def getStateFromMeta(meta: Int): IBlockState = {
		var baseState = this.getBlockState.getBaseState
		if (meta < 4) baseState = baseState.withProperty(PILLAR_CORNER, Int.box(meta))
		baseState.withProperty(PILLAR_DO_RENDER, Boolean.box(meta < 4))
		baseState
	}

	override def isOpaqueCube: Boolean = false

	override def isFullCube: Boolean = false

	override def isVisuallyOpaque: Boolean = false

	override def getRenderType: Int = 3

}
object BlockNexusPillar {
	val PILLAR_CORNER = StateProperties.createPropInt("corner", 0, 3)
	val PILLAR_DO_RENDER = PropertyBool.create("do_render")
}
