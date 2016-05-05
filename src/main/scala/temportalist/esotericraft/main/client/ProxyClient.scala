package temportalist.esotericraft.main.client

import java.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import temportalist.esotericraft.main.common.init.ModBlocks
import temportalist.esotericraft.api.util.IModelLoader
import temportalist.esotericraft.main.common.{EsoTeriCraft, ProxyCommon}

/**
  * Created by TheTemportalist on 12/31/2015.
  */
class ProxyClient extends ProxyCommon with IModGuiFactory with IModelLoader {

	override def preInit(): Unit = {

		OBJLoader.INSTANCE.addDomain(EsoTeriCraft.getModId)
		ModelLoader.setCustomModelResourceLocation(ModBlocks.crystal.getItemBlock, 0,
			new ModelResourceLocation(EsoTeriCraft.getModId + ":BlockCrystal"))

		this.registerModel(ModBlocks.pillar.getItemBlock, 0 until 1, EsoTeriCraft,
			ModBlocks.pillar.getClass.getSimpleName, "normal")

	}

	override def register(): Unit = {}

	override def postInit(): Unit = {}

	override def runtimeGuiCategories(): util.Set[RuntimeOptionCategoryElement] = null

	override def initialize(minecraftInstance: Minecraft): Unit = {}

	override def getHandlerFor(element: RuntimeOptionCategoryElement): RuntimeOptionGuiHandler = null

	override def mainConfigGuiClass(): Class[_ <: GuiScreen] = classOf[GuiConfig]

}
