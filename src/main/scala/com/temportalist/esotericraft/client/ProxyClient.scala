package com.temportalist.esotericraft.client

import java.util

import com.temportalist.esotericraft.common.init.ModBlocks
import com.temportalist.esotericraft.common.{EsoTeriCraft, ProxyCommon}
import com.temportalist.origin.api.common.resource.{IModDetails, IModResource}
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}

/**
  * Created by TheTemportalist on 12/31/2015.
  */
class ProxyClient extends ProxyCommon with IModGuiFactory {

	override def preInit(): Unit = {
	}

	override def register(): Unit = {}

	override def postInit(): Unit = {}

	override def runtimeGuiCategories(): util.Set[RuntimeOptionCategoryElement] = null

	override def initialize(minecraftInstance: Minecraft): Unit = {}

	override def getHandlerFor(element: RuntimeOptionCategoryElement): RuntimeOptionGuiHandler = null

	override def mainConfigGuiClass(): Class[_ <: GuiScreen] = classOf[GuiConfig]

}
