package temportalist
package esotericraft
package main
package common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import temportalist.esotericraft.api.init.Details
import temportalist.esotericraft.main.common.api.ApiLoader
import temportalist.esotericraft.main.common.init.ModBlocks
import temportalist.esotericraft.main.server.CommandETC
import temportalist.origin.foundation.common.modTraits.IHasCommands
import temportalist.origin.foundation.common.registers.{OptionRegister, Register}
import temportalist.origin.foundation.common.{IProxy, ModBase}
import temportalist.origin.foundation.server.ICommand

import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
@Mod(modid = Details.MOD_ID, name = Details.MOD_NAME, version = Details.MOD_VERSION,
	modLanguage = "scala",
	guiFactory = EsoTeriCraft.proxyClient,
	dependencies = "required-after:Forge;" + "required-after:origin;"
)
object EsoTeriCraft extends ModBase with IHasCommands {

	// ~~~~~~~~~~ Details & Proxy ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val proxyClient = "temportalist." + Details.MOD_ID + ".main.client.ProxyClient"
	final val proxyServer = "temportalist." + Details.MOD_ID + ".main.server.ProxyServer"

	/**
	  *
	  * @return A mod's ID
	  */
	override def getModId: String = Details.MOD_ID

	/**
	  *
	  * @return A mod's name
	  */
	override def getModName: String = Details.MOD_NAME

	/**
	  *
	  * @return A mod's version
	  */
	override def getModVersion: String = Details.MOD_VERSION

	@SidedProxy(clientSide = this.proxyClient, serverSide = this.proxyServer)
	var proxy: IProxy = _

	override def getProxy: IProxy = this.proxy

	// ~~~~~~~~~~ Inits ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getOptions: OptionRegister = null

	override def getRegisters: Seq[Register] = Seq(ModBlocks)

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {

		ApiLoader.preInit(event)

		utils.common.Utils.preInit(event)
		emulation.common.Emulation.preInit(event)
		transmorigification.common.Transform.preInit(event)
		//galvanization.common.Galvanize.preInit(event)
		sorcery.common.Sorcery.preInit(event)

		super.preInitialize(event)


	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)
		ApiLoader.init(event)

		utils.common.Utils.init(event)
		emulation.common.Emulation.init(event)
		transmorigification.common.Transform.init(event)
		//galvanization.common.Galvanize.init(event)
		sorcery.common.Sorcery.init(event)
	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)
		ApiLoader.postInit(event)

		utils.common.Utils.postInit(event)
		emulation.common.Emulation.postInit(event)
		transmorigification.common.Transform.postInit(event)
		//galvanization.common.Galvanize.postInit(event)
		sorcery.common.Sorcery.postInit(event)

	}

	override def getCommands: Seq[ICommand] = {
		val list = ListBuffer[ICommand]()
		list += CommandETC
		list ++= transmorigification.common.Transform.getCommands
		list
	}

}
