package temportalist.esotericraft.common.init

import net.minecraft.block.Block
import temportalist.esotericraft.common.block.{BlockNexusCrystal, BlockNexusPillar}
import temportalist.esotericraft.common.tile.TENexusCrystal
import temportalist.origin.foundation.common.register.BlockRegister

/**
  * Created by TheTemportalist on 1/2/2016.
  */
object ModBlocks extends BlockRegister {

	var nexusCrystal: Block = null
	var nexusPillar: Block = null

	/**
	  * This method is used to register TileEntities.
	  * Recommendation: Use GameRegistry.registerTileEntity
	  */
	override def registerTileEntities(): Unit = {
		this.register("nexusCrystal", classOf[TENexusCrystal])
	}

	override def register(): Unit = {

		this.nexusCrystal = new BlockNexusCrystal().addToOriginTab()
		this.nexusPillar = new BlockNexusPillar().addToOriginTab()

	}

}
