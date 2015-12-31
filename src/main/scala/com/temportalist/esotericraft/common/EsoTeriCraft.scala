package com.temportalist.esotericraft.common

import com.temportalist.origin.api.common.resource.{IModDetails, IModResource}
import com.temportalist.origin.foundation.common.IMod
import com.temportalist.origin.foundation.common.proxy.IProxy
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{SidedProxy, Mod}

/**
  * The main class for the mod
  * Created by TheTemportalist on 12/31/2015.
  */
@Mod(modid = EsoTeriCraft.MOD_ID, name = EsoTeriCraft.MOD_NAME,
	version = EsoTeriCraft.getModVersion, modLanguage = "scala",
	guiFactory = EsoTeriCraft.proxyClient,
	dependencies = ""
)
object EsoTeriCraft extends IMod with IModResource {

	// ~~~~~~~~~~~ Mod Setup ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val MOD_ID = "esotericraft"
	final val MOD_NAME = "EsoTeriCraft"
	final val MOD_VERSION = "1.0.0"

	override def getModID: String = this.MOD_ID

	override def getModVersion: String = this.MOD_VERSION

	override def getModName: String = this.MOD_NAME

	override def getDetails: IModDetails = this

	// ~~~~~~~~~~~ Proxy ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val proxyClient = "com.temportalist.esotericraft.client.ProxyClient"
	final val proxyServer = "com.temportalist.esotericraft.server.ProxyServer"

	@SidedProxy(clientSide = proxyClient, serverSide = proxyServer)
	var proxy: IProxy = null

	// ~~~~~~~~~~~ Inits ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(this, event, this.proxy, null)

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event, this.proxy)
	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event, this.proxy)
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
