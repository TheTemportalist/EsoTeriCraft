package temportalist.esotericraft.main.common.tile

import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import temportalist.esotericraft.api.util.NBT
import temportalist.origin.api.common.tile.ITileSaver

/**
  *
  * Created by TheTemportalist on 5/4/2016.
  *
  * @author TheTemportalist
  */
class TilePillar extends TileEntity with ITileSaver {

	private var posCrystal: BlockPos = null

	def setCrystal(tile: TileCrystal): Unit = {
		this.posCrystal = tile.getPos
	}

	def getCrystal: TileCrystal = {
		this.getWorld.getTileEntity(this.posCrystal) match {
			case tile: TileCrystal => tile
			case _ => null
		}
	}

	def breakBlockPre(state: IBlockState): Unit = {
		this.getCrystal.notifyOfBlockBreak()
	}

	override def writeToNBT(compound: NBTTagCompound): Unit = {
		super.writeToNBT(compound)

		if (this.posCrystal != null)
			compound.setTag("crystal", NBT.store(this.posCrystal))

	}

	override def readFromNBT(compound: NBTTagCompound): Unit = {
		super.readFromNBT(compound)

		if (compound.hasKey("crystal"))
			this.posCrystal = NBT.get[BlockPos](compound, "crystal")

	}

}
