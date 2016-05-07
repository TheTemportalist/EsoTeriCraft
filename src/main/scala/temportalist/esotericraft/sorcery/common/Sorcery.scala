package temportalist.esotericraft.sorcery.common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import temportalist.esotericraft.api.init.{IEsoTeriCraft, PluginEsoTeriCraft}
import temportalist.esotericraft.sorcery.common.capability.HandlerSorceryPlayer
import temportalist.esotericraft.sorcery.common.network.PacketCast
import temportalist.origin.api.common.IModDetails
import temportalist.origin.foundation.common.registers.{OptionRegister, Register}
import temportalist.origin.foundation.common.{IProxy, ModBase}

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
@Mod(modid = Sorcery.MOD_ID, name = Sorcery.MOD_NAME, version = Sorcery.MOD_VERSION,
	modLanguage = "scala",
	guiFactory = Sorcery.proxyClient,
	dependencies = "required-after:Forge;" + "required-after:origin;" + "required-after:esotericraft;"
)
object Sorcery extends ModBase {

	private var plugin: Plugin = _

	@PluginEsoTeriCraft
	class Plugin extends IEsoTeriCraft {
		override def onCreated(): Unit = plugin = this
	}

	// ~~~~~~~~~~ Details & Proxy ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val MOD_ID = "esoteric" + "sorcery"
	final val MOD_NAME = "Esoteric " + "Sorcery"
	final val MOD_VERSION = "@MOD_VERSION@"

	final val proxyClient = "temportalist.esotericraft.sorcery.client.ProxyClient"
	final val proxyServer = "temportalist.esotericraft.sorcery.server.ProxyServer"

	override def getModId: String = this.MOD_ID

	override def getModVersion: String = this.MOD_ID

	override def getModName: String = this.MOD_ID

	override def getDetails: IModDetails = this

	@SidedProxy(clientSide = this.proxyClient, serverSide = this.proxyServer)
	var proxy: IProxy = _

	def getProxy: IProxy = this.proxy

	// ~~~~~~~~~~ Inits ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getOptions: OptionRegister = null

	override def getRegisters: Seq[Register] = Seq()

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)

		this.registerNetwork()
		this.registerMessage(classOf[PacketCast.Handler], classOf[PacketCast])

		HandlerSorceryPlayer.init(this)

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
