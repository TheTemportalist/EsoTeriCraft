package temportalist.esotericraft.main.common.init

import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import temportalist.esotericraft.main.common.block.{BlockCrystal, BlockPillar}
import temportalist.esotericraft.main.common.tile.{TileCrystal, TilePillar}
import temportalist.origin.foundation.common.registers.BlockRegister

/**
  *
  * Created by TheTemportalist on 5/4/2016.
  *
  * @author TheTemportalist
  */
object ModBlocks extends BlockRegister {

	var crystal: BlockCrystal = _
	var pillar: BlockPillar = _

	/**
	  * This method is used to register TileEntities.
	  * Recommendation: Use GameRegistry.registerTileEntity
	  */
	override def registerTileEntities(): Unit = {
		this.register("Crystal", classOf[TileCrystal])
		this.register("Pillar", classOf[TilePillar])

	}

	override def register(): Unit = {

		this.crystal = new BlockCrystal
		this.crystal.setCreativeTab(CreativeTabs.MISC)

		this.pillar = new BlockPillar
		this.pillar.setCreativeTab(CreativeTabs.MISC)

	}

}
