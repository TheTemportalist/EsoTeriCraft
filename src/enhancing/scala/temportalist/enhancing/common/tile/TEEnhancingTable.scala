package temportalist.enhancing.common.tile

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import temportalist.enhancing.api.{ApiEsotericEnhancing, IEnhancingPlayer}
import temportalist.enhancing.common.Enhancing
import temportalist.enhancing.common.enhancement.EnhancementWrapper
import temportalist.enhancing.common.extended.EnhancingPlayer
import temportalist.origin.api.common.lib.V3O
import temportalist.origin.api.common.tile.ITileSaver
import temportalist.origin.api.common.utility.Stacks

/**
  * Created by TheTemportalist on 1/7/2016.
  */
class TEEnhancingTable extends TileEntity with ITileSaver {

	private var enhancingStack: ItemStack = null
	private var guiNotOpen = false

	def getEnhancingStackForRender: ItemStack = this.enhancingStack

	def doEnhancingAction(world: World, pos: BlockPos, state: IBlockState,
			player: EntityPlayer): Boolean = {
		var held = player.getCurrentEquippedItem
		if (this.enhancingStack == null) {
			if (held != null && !player.isSneaking) {
				this.enhancingStack = held.copy()
				enhancingStack.stackSize = 1
				held = held.copy()
				held.stackSize -= 1
				player.setCurrentItemOrArmor(0, held)
				this.markDirty()
				return true
			}
		}
		else {
			if (!player.isSneaking) {
				this.setGuiNotOpen(false)
				player.openGui(Enhancing, Enhancing.GUI_ENHANCEMENT,world,
					pos.getX, pos.getY, pos.getZ)
			}
			else if (this.guiNotOpen) {
				Stacks.spawnItemStack(world, new V3O(pos) + V3O.UP,
					this.enhancingStack.copy(), world.rand)
				this.enhancingStack = null
				this.markDirty()
			}
			return true
		}
		false
	}

	def getValidEnhancements(esoteric: IEnhancingPlayer): Array[EnhancementWrapper] = {
		esoteric.asInstanceOf[EnhancingPlayer].getValidEnhancementsFor(this.enhancingStack)
	}

	def setGuiNotOpen(b: Boolean) = {
		this.guiNotOpen = b
		this.markDirty()
	}

	def enhanceStackWithGlobalID(globalID: Int): Unit = {
		val enhancement = ApiEsotericEnhancing.getEnhancement(globalID)
		// todo enhance stack
	}

	override def writeToNBT(tag_com: NBTTagCompound): Unit = {
		super.writeToNBT(tag_com)

	}

	override def readFromNBT(tag_com: NBTTagCompound): Unit = {
		super.readFromNBT(tag_com)

	}

}
