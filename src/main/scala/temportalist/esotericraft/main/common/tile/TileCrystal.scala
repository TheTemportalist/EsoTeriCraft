package temportalist.esotericraft.main.common.tile

import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{EnumFacing, ITickable}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import temportalist.esotericraft.main.common.block.BlockPillar
import temportalist.esotericraft.main.common.block.BlockPillar._
import temportalist.origin.api.common.tile.ITileSaver

/**
  *
  * Created by TheTemportalist on 5/4/2016.
  *
  * @author TheTemportalist
  */
class TileCrystal extends TileEntity with ITileSaver with ITickable {

	private val pillarHeight = 5

	override def update(): Unit = {

	}

	def updateStructureStates(world: World, pos: BlockPos): Unit = {

		// corners default to upper right
		/*
			  N(2)
			 1---2
			 -----
		W(1) --P-- E(3)
			 -----
			 0---3
			  S(0)
		*/

		val canForm = this.canForm(world, pos)

		var posCorner: BlockPos = null
		var posPillar: BlockPos = null
		var corner: Int = -1
		var statePillar: IBlockState = null
		for (face <- EnumFacing.HORIZONTALS) {

			corner = face.getHorizontalIndex
			posCorner = this.getPositionForCorner(face, pos)

			for (h <- 0 until this.pillarHeight) {
				posPillar = posCorner.down(h)
				statePillar = world.getBlockState(posPillar)
				if (this.isValidPillar(statePillar)) {
					statePillar = statePillar.
							withProperty(CORNER, Int.box(if (canForm) corner else 0)).
							withProperty(RENDER_STATE,
								Int.box(
									if (!canForm) 0
									else if (h == this.pillarHeight - 1) 2
									else 1
								)
							)
					world.setBlockState(posPillar, statePillar, 3)
					world.getTileEntity(posPillar) match {
						case tile: TilePillar => tile.setCrystal(this)
						case _ =>
					}
				}
			}

		}
	}

	private def getPositionForCorner(face: EnumFacing, posCenter: BlockPos): BlockPos = {
		val radius = 3
		val pos = posCenter.offset(face, radius)
		face match {
			case EnumFacing.NORTH => pos.offset(EnumFacing.EAST, radius)
			case EnumFacing.EAST => pos.offset(EnumFacing.SOUTH, radius)
			case EnumFacing.SOUTH => pos.offset(EnumFacing.WEST, radius)
			case EnumFacing.WEST => pos.offset(EnumFacing.NORTH, radius)
			case _ => pos
		}
	}

	private def canForm(world: World, posCenter: BlockPos): Boolean = {
		for (face <- EnumFacing.HORIZONTALS) {
			for (h <- 0 until this.pillarHeight) {
				if (!this.isValidPillar(world.getBlockState(
					this.getPositionForCorner(face, pos).down(h)
				))) {
					return false
				}
			}
		}
		true
	}

	private def isValidPillar(state: IBlockState): Boolean = state.getBlock.isInstanceOf[BlockPillar]

	def notifyOfBlockBreak(): Unit = {
		this.updateStructureStates(this.getWorld, this.getPos)
	}

}
