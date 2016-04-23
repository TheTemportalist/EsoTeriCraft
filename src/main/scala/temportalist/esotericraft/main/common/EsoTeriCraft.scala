package temportalist.esotericraft.main.common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import temportalist.origin.foundation.common.registers.{OptionRegister, Register}
import temportalist.origin.foundation.common.{IProxy, ModBase}

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  * @author TheTemportalist
  */
@Mod(modid = EsoTeriCraft.MOD_ID, name = EsoTeriCraft.MOD_NAME, version = EsoTeriCraft.MOD_VERSION,
	modLanguage = "scala",
	guiFactory = EsoTeriCraft.proxyClient,
	dependencies = "required-after:Forge;" + "required-after:origin;"
)
object EsoTeriCraft extends ModBase {

	final val MOD_ID = "esotericraft"
	final val MOD_NAME = "EsoTeriCraft"
	final val MOD_VERSION = "@MOD_VERSION@"
	final val proxyClient = "temportalist.compression.main.client.ProxyClient"
	final val proxyServer = "temportalist.compression.main.server.ProxyServer"

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

	override def getOptions: OptionRegister = null

	override def getRegisters: Seq[Register] = Seq()

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
