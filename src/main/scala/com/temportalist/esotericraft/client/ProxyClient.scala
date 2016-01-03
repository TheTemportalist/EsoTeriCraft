package com.temportalist.esotericraft.client

import java.util

import com.temportalist.esotericraft.common.init.ModBlocks
import com.temportalist.esotericraft.common.{EsoTeriCraft, ProxyCommon}
import com.temportalist.origin.api.common.resource.{IModResource, IModDetails, EnumResource}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.IModGuiFactory.{RuntimeOptionGuiHandler, RuntimeOptionCategoryElement}

/**
  * Created by TheTemportalist on 12/31/2015.
  */
class ProxyClient extends ProxyCommon with IModGuiFactory {

	override def register(): Unit = {
		this.addOBJDomain(EsoTeriCraft)
		this.addCustomModel(EsoTeriCraft, ModBlocks.nexusCrystal.getDefaultState, EnumResource.MODEL_BLOCK)
	}

	def addOBJDomain(mod: IModDetails): Unit = OBJLoader.instance.addDomain(mod.getModID)

	def addCustomModel(item: Item, meta: Int, location: ResourceLocation): Unit = {
		ModelLoader.setCustomModelResourceLocation(item, meta,
			new ModelResourceLocation(location, "inventory"))
	}

	def addCustomModel(mod: IModResource, state: IBlockState, source: EnumResource): Unit = {
		val block = state.getBlock
		this.addCustomModel(
			Item.getItemFromBlock(block),
			state.getBlock.getMetaFromState(state),
			mod.loadResource(source, state.getBlock.getClass.getSimpleName))
	}

	override def postInit(): Unit = {}

	override def runtimeGuiCategories(): util.Set[RuntimeOptionCategoryElement] = null

	override def initialize(minecraftInstance: Minecraft): Unit = {}

	override def getHandlerFor(element: RuntimeOptionCategoryElement): RuntimeOptionGuiHandler = null

	override def mainConfigGuiClass(): Class[_ <: GuiScreen] = classOf[GuiConfig]

}
