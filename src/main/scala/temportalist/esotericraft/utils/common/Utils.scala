package temportalist.esotericraft.utils.common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import temportalist.esotericraft.utils.common.init.ModItems
import temportalist.origin.foundation.common.registers.{OptionRegister, Register}
import temportalist.origin.foundation.common.{IProxy, ModBase}

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
@Mod(modid = Utils.MOD_ID, name = Utils.MOD_NAME, version = Utils.MOD_VERSION,
	modLanguage = "scala",
	guiFactory = Utils.proxyClient,
	dependencies = "required-after:Forge;" + "required-after:origin;" + "required-after:esotericraft;"
)
object Utils extends ModBase {

	// ~~~~~~~~~~ Details & Proxy ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val MOD_ID = "esoteric" + "utils"
	final val MOD_NAME = "Esoteric " + "Utils"
	final val MOD_VERSION = "@MOD_VERSION@"
	final val proxyClient = "temportalist.esotericraft.utils.client.ProxyClient"
	final val proxyServer = "temportalist.esotericraft.utils.server.ProxyServer"

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

	override def getRegisters: Seq[Register] = Seq(ModItems)

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

}
