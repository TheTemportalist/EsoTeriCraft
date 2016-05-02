package temportalist.esotericraft.utils.client

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import temportalist.origin.api.common.block.BlockBase
import temportalist.origin.api.common.item.ItemBase
import temportalist.origin.foundation.common.IMod
import temportalist.origin.foundation.common.registers.{BlockRegister, ItemRegister, Register}

/**
  *
  * Created by TheTemportalist on 4/28/2016.
  *
  * @author TheTemportalist
  */
trait IModelLoader {

	def autoLoadModels(mod: IMod): Unit = {

		for (reg <- mod.getRegisters) {
			reg match {
				case regObj: BlockRegister =>
					for (obj <- regObj.getObjects) this.registerModel(mod, obj)
				case regObj: ItemRegister =>
					for (obj <- regObj.getObjects) this.registerModel(mod, obj)
				case _ =>
					this.registerOtherObjects(mod, reg)
			}
		}

	}

	def registerOtherObjects(mod: IMod, reg: Register): Unit = {}

	final def registerModel(mod: IMod, obj: ItemBase): Unit = {
		this.registerModel(obj, obj.getItemMetaRange, mod, obj.name)
	}

	final def registerModel(mod: IMod, obj: BlockBase): Unit = {
		if (obj.hasItemBlock)
			this.registerModel(obj.getItemBlock, obj.getItemMetaRange, mod, obj.name)
	}

	final def registerModel(item: Item, metas: Range, mod: IMod, name: String): Unit = {
		for (meta <- metas)
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(
				mod.getDetails.getModId + ":" + name
			))
	}

}
