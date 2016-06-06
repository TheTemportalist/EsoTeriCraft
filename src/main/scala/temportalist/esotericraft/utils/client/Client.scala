package temportalist.esotericraft.utils.client

import temportalist.esotericraft.api.init.IPluginClient
import temportalist.esotericraft.main.common.EsoTeriCraft
import temportalist.esotericraft.utils.common.init.ModItems
import temportalist.origin.foundation.client.IModelLoader

/**
  *
  * Created by TheTemportalist on 6/6/2016.
  *
  * @author TheTemportalist
  */
object Client extends IPluginClient with IModelLoader {

	override def preInit(): Unit = {

		this.registerModel(EsoTeriCraft, ModItems.spindle)

	}

}
