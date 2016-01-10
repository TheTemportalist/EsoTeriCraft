package temportalist.enhancing.common.init

import net.minecraft.block.Block
import temportalist.enhancing.common.block.BlockEnhancingTable
import temportalist.enhancing.common.tile.TEEnhancingTable
import temportalist.origin.foundation.common.register.BlockRegister
import temportalist.origin.internal.common.Origin

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
		this.register("enhancing_table", classOf[TEEnhancingTable])
	}

	override def register(): Unit = {
		this.enhancementBlock = new BlockEnhancingTable
		Origin.addBlockToTab(this.enhancementBlock)

	}

}
