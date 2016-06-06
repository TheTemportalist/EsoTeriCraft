package temportalist.esotericraft.galvanization.client

import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.{ModelBakery, ModelResourceLocation}
import net.minecraft.item.ItemStack
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.api.init.IPluginClient
import temportalist.esotericraft.galvanization.common.entity.EntityEmpty
import temportalist.esotericraft.galvanization.common.init.ModItems
import temportalist.esotericraft.main.common.EsoTeriCraft
import temportalist.origin.foundation.client.IModelLoader

/**
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
object Client extends IPluginClient with IModelLoader {

	/**
	  * This needs to be called in [[temportalist.origin.foundation.common.IProxy.preInit]]
	  */
	override def preInit(): Unit = {

		RenderingRegistry.registerEntityRenderingHandler(classOf[EntityEmpty], RenderEmpty)

		this.registerModel(EsoTeriCraft, ModItems.golemEgg)
		this.registerModel(EsoTeriCraft, ModItems.debugTask)

		// TODO move this to IModelLoader
		ModelBakery.registerItemVariants(ModItems.taskItem, ModItems.taskItem.getPossibleModelLocations:_*)
		ModelLoader.setCustomMeshDefinition(ModItems.taskItem, new ItemMeshDefinition {
			override def getModelLocation(stack: ItemStack): ModelResourceLocation = {
				ModItems.taskItem.getModelLocation(stack)
			}
		})

		MinecraftForge.EVENT_BUS.register(ClientTask)

	}


}
