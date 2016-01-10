package temportalist.enhancing.common.block

import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World
import temportalist.enhancing.common.ModuleEnhancing
import temportalist.enhancing.common.init.ModBlocks
import temportalist.enhancing.common.tile.TEEnhancingTable
import temportalist.esotericraft.api.ApiEsotericraft
import temportalist.origin.api.common.block.BlockTile

/**
  * Created by TheTemportalist on 1/7/2016.
  */
class BlockEnhancingTable extends BlockTile(ModBlocks, classOf[TEEnhancingTable],
	mat = Material.rock, color = MapColor.redColor){

	this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F)
	this.setLightOpacity(0)

	override def isFullBlock: Boolean = false

	override def isOpaqueCube: Boolean = false

	override def getRenderType: Int = 3

	override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState,
			playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float,
			hitZ: Float): Boolean = {
		if (!ApiEsotericraft.Player.hasKnowledgeOf(playerIn, ModuleEnhancing)) {
			val playerPos = playerIn.getPositionVector
			playerIn.knockBack(playerIn, 10,
				pos.getX - playerPos.xCoord, pos.getZ - playerPos.zCoord)
		}
		else {
			worldIn.getTileEntity(pos) match {
				case tile: TEEnhancingTable =>
					return tile.doEnhancingAction(worldIn, pos, state, playerIn)
				case _ =>
			}
		}
		false
	}

}
