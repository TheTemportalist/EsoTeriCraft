package com.temportalist.esotericenhancing.common.init

import com.temportalist.esotericenhancing.common.block.BlockEnhancement
import com.temportalist.origin.foundation.common.register.BlockRegister
import com.temportalist.origin.internal.common.Origin
import net.minecraft.block.Block

/**
  * Created by TheTemportalist on 12/31/2015.
  */
object ModBlocks extends BlockRegister {

	var enhancementBlock: Block = null

	/**
	  * This method is used to register TileEntities.
	  * Recommendation: Use GameRegistry.registerTileEntity
	  */
	override def registerTileEntities(): Unit = {

	}

	override def register(): Unit = {
		this.enhancementBlock = new BlockEnhancement
		Origin.addBlockToTab(this.enhancementBlock)

	}

}
