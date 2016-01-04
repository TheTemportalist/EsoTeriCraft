package com.temportalist.esotericraft.common.world

import com.temportalist.esotericraft.common.block.BlockNexusPillar
import com.temportalist.esotericraft.common.init.ModBlocks
import com.temportalist.origin.api.common.lib.V3O

/**
  * Created by TheTemportalist on 1/3/2016.
  */
object StructureNexus extends WorldStructure(7, 5, 7) {

	override def register(): Unit = {
		val centerOffset = new V3O(4, 5, 4)
		// add the nexus
		this.addBlockState(
			ModBlocks.nexusCrystal.getBlockState.getBaseState, centerOffset)

		// create a basic state for all blocks
		val baseState_hidden = ModBlocks.nexusPillar.getBlockState.getBaseState.
				withProperty(BlockNexusPillar.PILLAR_DO_RENDER, Boolean.box(false))
		// map the corners to the prospective offsets
		val cornerMap = Array[(Int, Int)]((+1, -1), (+1, +1), (-1, +1), (-1, -1))

		// iterate over each corner
		for (corner <- 0 to 3) {
			// get the state for this corner - not hidden
			val xz = cornerMap(corner)
			val statePos = centerOffset.copy() + new V3O(xz._1 * 3, -4, xz._2 * 3)

			// add the bottom blockstate of the corner (the giant prong, not hidden)
			val prongState = baseState_hidden.
					withProperty(BlockNexusPillar.PILLAR_CORNER, Int.box(corner)).
					withProperty(BlockNexusPillar.PILLAR_DO_RENDER, Boolean.box(true))
			this.addBlockState(prongState, statePos.copy())

			def appendWithHeight(i: Int): Unit =
				this.addBlockState(baseState_hidden, statePos + (V3O.UP * i))
			for (i <- 1 to 3) appendWithHeight(i)
			statePos += new V3O(-xz._1, 0, -xz._2)
			appendWithHeight(4)
			statePos += new V3O(-xz._1, 0, -xz._2)
			appendWithHeight(4)
		}

	}

}
