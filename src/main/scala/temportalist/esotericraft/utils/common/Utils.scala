package temportalist.esotericraft.utils.common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import temportalist.esotericraft.api.init.IEsoTeriCraft
import temportalist.esotericraft.api.init.IEsoTeriCraft.PluginEsoTeriCraft
import temportalist.esotericraft.galvanization.common.init.ModItems
import temportalist.origin.api.common.IModDetails
import temportalist.origin.foundation.common.IModPlugin
import temportalist.origin.foundation.common.registers.{OptionRegister, Register}

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
object Utils extends IModPlugin with IModDetails {

	private var plugin: Plugin = _

	@PluginEsoTeriCraft
	class Plugin extends IEsoTeriCraft {
		override def onCreated(): Unit = plugin = this
	}

	override def getModId: String = "esotericutils"

	override def getModName: String = "Esoteric Utils"

	override def getModVersion: String = "@MOD_VERSION@"

	override def getDetails: IModDetails = this

	override def getOptions: OptionRegister = null

	// ~~~~~~~~~~ Inits ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getRegisters: Seq[Register] = Seq(ModItems)

	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)

	}

	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

	}

	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

}
