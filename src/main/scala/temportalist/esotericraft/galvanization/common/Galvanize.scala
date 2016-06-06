package temportalist

package esotericraft
package galvanization
package common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.relauncher.Side
import temportalist.esotericraft.api.init.IEsoTeriCraft
import temportalist.esotericraft.api.init.IEsoTeriCraft.PluginEsoTeriCraft
import temportalist.esotericraft.galvanization.common.init.{ModEntities, ModItems}
import temportalist.esotericraft.galvanization.common.network.PacketUpdateClientTasks
import temportalist.esotericraft.galvanization.common.task.ai.core.LoaderTask
import temportalist.esotericraft.galvanization.common.task.core.ControllerTask
import temportalist.origin.api.common.IModDetails
import temportalist.origin.foundation.common.IModPlugin
import temportalist.origin.foundation.common.registers.{OptionRegister, Register}

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
object Galvanize extends IModPlugin with IModDetails {

	/*
		TODO Tasks:
		Mining
		Emulation of attacks
		Item contains other items and emulates their actions
	 */

	private var plugin: Plugin = _

	@PluginEsoTeriCraft
	class Plugin extends IEsoTeriCraft {

		override def onCreated(): Unit = plugin = this

	}

	override def getModId: String = "esoteric" + "galvanization"

	override def getModName: String = "Esoteric " + "Galvanization"

	override def getModVersion: String = "@MOD_VERSION@"

	override def getDetails: IModDetails = this

	// ~~~~~~~~~~ Inits ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getOptions: OptionRegister = Options

	override def getRegisters: Seq[Register] = Seq(ModItems, ModEntities)

	override def getNetworkName: String = "galvanization" // max 20 characters

	def preInit(event: FMLPreInitializationEvent): Unit = {
		LoaderTask.preInit(event)
		super.preInitialize(event)

		this.registerNetwork()
		this.registerMessage(classOf[PacketUpdateClientTasks.Handler], classOf[PacketUpdateClientTasks], Side.CLIENT)

		this.registerHandler(ControllerTask)

	}

	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

	}

	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

}
