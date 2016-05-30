package temportalist.esotericraft.galvanization.client

import java.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.{ModelBakery, ModelResourceLocation}
import net.minecraft.item.ItemStack
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import net.minecraftforge.fml.client.registry.RenderingRegistry
import temportalist.esotericraft.galvanization.common.entity.EntityEmpty
import temportalist.esotericraft.galvanization.common.init.ModItems
import temportalist.esotericraft.galvanization.common.{Galvanize, ProxyCommon}
import temportalist.origin.foundation.client.IModelLoader

/**
  * Created by TheTemportalist on 12/31/2015.
  */
class ProxyClient extends ProxyCommon with IModGuiFactory with IModelLoader {

	override def preInit(): Unit = {
		super.preInit()
		Client.preInit()
		this.autoLoadModels(Galvanize)

		RenderingRegistry.registerEntityRenderingHandler(classOf[EntityEmpty], RenderEmpty)

		// TODO move this to IModelLoader
		ModelBakery.registerItemVariants(ModItems.taskItem, ModItems.taskItem.getPossibleModelLocations:_*)
		ModelLoader.setCustomMeshDefinition(ModItems.taskItem, new ItemMeshDefinition {
			override def getModelLocation(stack: ItemStack): ModelResourceLocation = {
				ModItems.taskItem.getModelLocation(stack)
			}
		})

	}

	override def register(): Unit = {}

	override def postInit(): Unit = {}

	override def runtimeGuiCategories(): util.Set[RuntimeOptionCategoryElement] = null

	override def initialize(minecraftInstance: Minecraft): Unit = {}

	override def getHandlerFor(element: RuntimeOptionCategoryElement): RuntimeOptionGuiHandler = null

	override def mainConfigGuiClass(): Class[_ <: GuiScreen] = classOf[GuiConfig]

}
