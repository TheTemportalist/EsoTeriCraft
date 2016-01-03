package com.temportalist.esotericraft.common.block

import com.temportalist.esotericraft.common.EsoTeriCraft
import com.temportalist.esotericraft.common.tile.TENexusCrystal
import com.temportalist.origin.api.common.block.BlockTile
import net.minecraft.block.state.IBlockState

/**
  * Created by TheTemportalist on 1/2/2016.
  */
class BlockNexusCrystal extends BlockTile(
	EsoTeriCraft.getModID, "BlockNexusCrystal", classOf[TENexusCrystal]) {

	override def isOpaqueCube: Boolean = false

	override def isFullCube: Boolean = false

	override def isVisuallyOpaque: Boolean = false

	override def getRenderType: Int = 3

	override def getMetaFromState(state: IBlockState): Int = 0

}
