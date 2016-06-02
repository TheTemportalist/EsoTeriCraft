package temportalist.esotericraft.transmorigification.common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.relauncher.Side
import temportalist.esotericraft.api.init.IEsoTeriCraft
import temportalist.esotericraft.api.init.IEsoTeriCraft.PluginEsoTeriCraft
import temportalist.esotericraft.transmorigification.common.capability.HandlerPlayerGalvanize
import temportalist.esotericraft.transmorigification.common.network.{PacketSetModel, PacketUpdateClientModels}
import temportalist.esotericraft.transmorigification.server.CommandSetPlayerModel
import temportalist.origin.api.common.IModDetails
import temportalist.origin.foundation.common.IModPlugin
import temportalist.origin.foundation.common.modTraits.IHasCommands
import temportalist.origin.foundation.common.registers.OptionRegister
import temportalist.origin.foundation.server.ICommand

/**
  *
  * Created by TheTemportalist on 6/2/2016.
  *
  * @author TheTemportalist
  */
object Transform extends IModPlugin with IHasCommands with IModDetails {

	private var plugin: Plugin = _

	@PluginEsoTeriCraft
	class Plugin extends IEsoTeriCraft {

		override def onCreated(): Unit = plugin = this

	}

	override def getModId: String = "esoteric" + "transform"

	override def getModName: String = "Esoteric Transmorigfication"

	override def getModVersion: String = "@MOD_VERSION@"

	override def getDetails: IModDetails = this

	override def getNetworkName: String = "esoteric_transform" // max 20 characters

	override def getOptions: OptionRegister = null

	// ~~~~~~~~~~ Inits ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)

		this.registerNetwork()
		HandlerPlayerGalvanize.init(this, "PlayerGalvanize")
		this.registerMessage(classOf[PacketSetModel.Handler], classOf[PacketSetModel], Side.SERVER)
		this.registerMessage(classOf[PacketUpdateClientModels.Handler], classOf[PacketUpdateClientModels], Side.CLIENT)

	}

	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

	}

	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

	override def getCommands: Seq[ICommand] = Seq(CommandSetPlayerModel)

}
