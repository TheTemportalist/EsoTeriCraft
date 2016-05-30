package temportalist.esotericraft.galvanization.common.item

import temportalist.esotericraft.galvanization.common.init.ModItems

/**
  *
  * Created by TheTemportalist on 5/29/2016.
  *
  * @author TheTemportalist
  */
class ItemGalvanizeRendering(itemMetaRange: Range = Range.apply(0, 1))
		extends ItemGalvanize(itemMetaRange = itemMetaRange){
	ModItems.registerObject(this)
}
