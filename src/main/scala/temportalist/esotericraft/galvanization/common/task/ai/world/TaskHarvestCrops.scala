package temportalist.esotericraft.galvanization.common.task.ai.world

import net.minecraft.block.state.IBlockState
import net.minecraft.block.{BlockCrops, BlockReed}
import net.minecraft.entity.EntityCreature
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.{World, WorldServer}
import temportalist.esotericraft.api.galvanize.ai.GalvanizeTask
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.task.ai.interfaces.{IFakePlayer, ITaskBoundingBoxMixin}

/**
  *
  * Created by TheTemportalist on 5/26/2016.
  *
  * @author TheTemportalist
  */
@GalvanizeTask(modid = Galvanize.MOD_ID,
	name = "harvestCrop",
	displayName = "Harvest (Crops)"
)
class TaskHarvestCrops(
		pos: BlockPos,
		face: EnumFacing
) extends TaskHarvest(pos, face) with ITaskBoundingBoxMixin with IFakePlayer {

	override def isBlockValid(world: World, pos: BlockPos, state: IBlockState): Boolean = {
		state.getBlock match {
			case crop: BlockCrops => crop.isMaxAge(state)
			case reed: BlockReed => world.getBlockState(pos.up()).getBlock.isInstanceOf[BlockReed]
			case _ => false
		}
	}

	override def harvestState(world: World, pos: BlockPos, state: IBlockState,
			entity: EntityCreature): Unit = {
		world match {
			case worldServer: WorldServer =>

				val posTarget =
					state.getBlock match {
						case reed: BlockReed => pos.up()
						case _ => pos
					}

				val fakePlayer = this.getFakePlayer(worldServer)
				entity.swingArm(EnumHand.MAIN_HAND)
				TaskHarvestTree.breakBlock(world, posTarget, fakePlayer)

			case _ =>
		}

	}

}
