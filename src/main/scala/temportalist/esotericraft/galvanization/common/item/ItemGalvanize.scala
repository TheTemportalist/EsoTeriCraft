package temportalist.esotericraft.galvanization.common.item

import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.init.ModItems
import temportalist.origin.api.common.item.ItemBase

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class ItemGalvanize(itemMetaRange: Range = Range.apply(0, 1))
		extends ItemBase(Galvanize, itemMetaRange = itemMetaRange) {
	ModItems.registerObject(this)
}
