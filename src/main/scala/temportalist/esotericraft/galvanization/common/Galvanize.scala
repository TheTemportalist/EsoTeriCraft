package temportalist.esotericraft.galvanization.common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import temportalist.esotericraft.api.init.{IEsoTeriCraft, PluginEsoTeriCraft}
import temportalist.esotericraft.galvanization.common.capability.HandlerPlayerGalvanize
import temportalist.esotericraft.galvanization.common.init.{ModEntities, ModItems}
import temportalist.esotericraft.galvanization.server.CommandSetPlayerModel
import temportalist.origin.foundation.common.modTraits.IHasCommands
import temportalist.origin.foundation.common.{IProxy, ModBase}
import temportalist.origin.foundation.common.registers.{OptionRegister, Register}
import temportalist.origin.foundation.server.ICommand

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
@Mod(modid = Galvanize.MOD_ID, name = Galvanize.MOD_NAME, version = Galvanize.MOD_VERSION,
	modLanguage = "scala",
	guiFactory = Galvanize.proxyClient,
	dependencies = "required-after:Forge;" + "required-after:origin;" + "required-after:esotericraft;"
)
object Galvanize extends ModBase with IHasCommands {

	private var plugin: Plugin = _

	@PluginEsoTeriCraft
	class Plugin extends IEsoTeriCraft {
		override def onCreated(): Unit = plugin = this
	}

	// ~~~~~~~~~~ Details & Proxy ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val MOD_ID = "esoteric" + "galvanization"
	final val MOD_NAME = "Esoteric " + "Galvanization"
	final val MOD_VERSION = "@MOD_VERSION@"
	final val proxyClient = "temportalist.esotericraft.galvanization.client.ProxyClient"
	final val proxyServer = "temportalist.esotericraft.galvanization.server.ProxyServer"

	/**
	  *
	  * @return A mod's ID
	  */
	override def getModId: String = this.MOD_ID

	/**
	  *
	  * @return A mod's name
	  */
	override def getModName: String = this.MOD_NAME

	/**
	  *
	  * @return A mod's version
	  */
	override def getModVersion: String = this.MOD_VERSION

	@SidedProxy(clientSide = this.proxyClient, serverSide = this.proxyServer)
	var proxy: IProxy = _

	override def getProxy: IProxy = this.proxy

	// ~~~~~~~~~~ Inits ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getOptions: OptionRegister = Options

	override def getRegisters: Seq[Register] = Seq(ModItems, ModEntities)

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)

		this.registerNetwork()
		HandlerPlayerGalvanize.init(this, "PlayerGalvanize")

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

	override def getCommands: Seq[ICommand] = Seq(CommandSetPlayerModel)

}
