package temportalist.esotericraft.utils.client

import net.minecraftforge.client.model.{ModelLoader, ModelLoaderRegistry}
import temportalist.esotericraft.api.init.IPluginClient
import temportalist.esotericraft.main.common.EsoTeriCraft
import temportalist.esotericraft.utils.client.multi.ModelLoaderMulti
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

		//this.registerModel(EsoTeriCraft, ModItems.multi)
		ModelLoaderRegistry.registerLoader(ModelLoaderMulti)
		ModelLoader.setCustomModelResourceLocation(ModItems.multi, 0, ModelLoaderMulti.getLocation)

	}

}
