package temportalist.esotericraft.sorcery.common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import temportalist.esotericraft.api.init.IEsoTeriCraft
import temportalist.esotericraft.api.init.IEsoTeriCraft.PluginEsoTeriCraft
import temportalist.esotericraft.sorcery.common.capability.HandlerSorceryPlayer
import temportalist.esotericraft.sorcery.common.network.PacketCast
import temportalist.origin.api.common.IModDetails
import temportalist.origin.foundation.common.IModPlugin
import temportalist.origin.foundation.common.registers.OptionRegister

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
object Sorcery extends IModPlugin with IModDetails {

	private var plugin: Plugin = _

	@PluginEsoTeriCraft
	class Plugin extends IEsoTeriCraft {
		override def onCreated(): Unit = plugin = this
	}

	override def getModId: String = "esoteric_sorcery"

	override def getModName: String = "Esoteric Sorcery"

	override def getModVersion: String = "@MOD_VERSION@"

	override def getDetails: IModDetails = this

	override def getNetworkName: String = "esoteric_sorcery"

	override def getOptions: OptionRegister = null

	// ~~~~~~~~~~ Inits ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\

	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)

		this.registerNetwork()
		this.registerMessage(classOf[PacketCast.Handler], classOf[PacketCast])

		HandlerSorceryPlayer.init(this, "SorceryPlayer")

	}

	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

	}

	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

}
