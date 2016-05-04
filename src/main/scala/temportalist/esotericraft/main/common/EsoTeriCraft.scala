package temportalist.esotericraft.main.common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import temportalist.esotericraft.main.common.init.ModBlocks
import temportalist.esotericraft.main.server.CommandETC
import temportalist.origin.foundation.common.modTraits.IHasCommands
import temportalist.origin.foundation.common.registers.{OptionRegister, Register}
import temportalist.origin.foundation.common.{IProxy, ModBase}
import temportalist.origin.foundation.server.ICommand

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
@Mod(modid = EsoTeriCraft.MOD_ID, name = EsoTeriCraft.MOD_NAME, version = EsoTeriCraft.MOD_VERSION,
	modLanguage = "scala",
	guiFactory = EsoTeriCraft.proxyClient,
	dependencies = "required-after:Forge;" + "required-after:origin;"
)
object EsoTeriCraft extends ModBase with IHasCommands {

	// ~~~~~~~~~~ Details & Proxy ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val MOD_ID = "esotericraft"
	final val MOD_NAME = "EsoTeriCraft"
	final val MOD_VERSION = "@MOD_VERSION@"
	final val proxyClient = "temportalist." + MOD_ID + ".main.client.ProxyClient"
	final val proxyServer = "temportalist." + MOD_ID + ".main.server.ProxyServer"

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

	override def getOptions: OptionRegister = null

	override def getRegisters: Seq[Register] = Seq(ModBlocks)

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

	override def getCommands: Seq[ICommand] = Seq(CommandETC)

}
