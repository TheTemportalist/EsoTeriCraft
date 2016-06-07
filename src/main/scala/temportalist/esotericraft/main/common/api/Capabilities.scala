package temportalist.esotericraft.main.common.api

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.{CapabilityItemHandler, IItemHandler}

/**
  *
  * Created by TheTemportalist on 6/7/2016.
  *
  * @author TheTemportalist
  */
object Capabilities {

	def isInventory(tileEntity: TileEntity, face: EnumFacing = null): Boolean = {
		tileEntity != null &&
				tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face)
	}

	def getInventory(tileEntity: TileEntity, face: EnumFacing = null): IItemHandler = {
		tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face)
	}

}
