package com.temportalist.esotericraft.common.init

import com.temportalist.esotericraft.common.EsoTeriCraft
import com.temportalist.esotericraft.common.block.BlockNexusCrystal
import com.temportalist.esotericraft.common.tile.TENexusCrystal
import com.temportalist.origin.foundation.common.register.BlockRegister
import com.temportalist.origin.internal.common.Origin
import net.minecraft.block.Block

/**
  * Created by TheTemportalist on 1/2/2016.
  */
object ModBlocks extends BlockRegister {

	var nexusCrystal: Block = null

	/**
	  * This method is used to register TileEntities.
	  * Recommendation: Use GameRegistry.registerTileEntity
	  */
	override def registerTileEntities(): Unit = {
		this.register(EsoTeriCraft.getModID + ":nexusCrystal", classOf[TENexusCrystal])
	}

	override def register(): Unit = {
		this.nexusCrystal = new BlockNexusCrystal
		Origin.addBlockToTab(this.nexusCrystal)

	}

}
