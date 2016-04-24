package temportalist.esotericsorcery.common

import net.minecraft.entity.player.EntityPlayer.EnumStatus
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import net.minecraftforge.fml.relauncher.Side
import temportalist.esotericraft.main.common.network.PacketSyncEsotericPlayer
import temportalist.esotericsorcery.client.EnumKeyAction
import temportalist.esotericsorcery.common.network.{PacketKeyPressed, SyncSorceryPlayerHandler}
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

	// ~~~~~~~~~~ Details & Proxy ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val MOD_ID = "esotericsorcery"
	final val MOD_NAME = "Esoteric Sorcery"
	final val MOD_VERSION = "@MOD_VERSION@"
	final val proxyClient = "temportalist." + MOD_ID + ".client.ProxyClient"
	final val proxyServer = "temportalist." + MOD_ID + ".server.ProxyServer"

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

		this.registerNetwork()
		this.registerMessage(classOf[SyncSorceryPlayerHandler], classOf[PacketSyncEsotericPlayer], Side.CLIENT)
		this.registerMessage(classOf[PacketKeyPressed.Handler], classOf[PacketKeyPressed], Side.SERVER)

		SorceryPlayer.register()

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

	def doAction(action: EnumKeyAction, player: EntityPlayerMP): Unit = {
		this.log(action.toString)
		action match {
			case EnumKeyAction.CAST =>

				// Bed action
				val spawn = player.getBedLocation(player.dimension)
				val status = player.trySleep(player.getPosition)
				if (status == EnumStatus.OK) SorceryPlayer.get(player).setSleepStart(spawn)
			case _ =>
		}
	}

}
