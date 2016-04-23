package temportalist.esotericraft.main.common

import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import temportalist.esotericraft.main.common.capability.CapabilityPlayer
import temportalist.esotericraft.main.common.capability_new.api.CapabilityWrapper
import temportalist.origin.foundation.common.registers.{OptionRegister, Register}
import temportalist.origin.foundation.common.{IProxy, ModBase}

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
object EsoTeriCraft extends ModBase {

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

	override def getRegisters: Seq[Register] = Seq()

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)

		EsoTeriCraft.log("Registering Capabilities")
		CapabilityPlayer.register()

	}

	def register[T](wrap: CapabilityWrapper[T]): Unit = {
		CapabilityManager.INSTANCE.register(wrap.getCapability, wrap.getStorage, wrap.getFactory)
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
