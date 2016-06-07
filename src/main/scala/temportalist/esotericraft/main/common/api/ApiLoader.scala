package temportalist.esotericraft.main.common.api

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import temportalist.esotericraft.api.init.IEsoTeriCraft
import temportalist.esotericraft.api.init.IEsoTeriCraft.PluginEsoTeriCraft
import temportalist.origin.api.common.lib.loader.InstanceLoader

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
object ApiLoader extends InstanceLoader(classOf[PluginEsoTeriCraft], classOf[IEsoTeriCraft]) {

	def preInit(event: FMLPreInitializationEvent): Unit = {
		this.loadAnnotations(event)
	}

	def init(event: FMLInitializationEvent): Unit = {
	}

	def postInit(event: FMLPostInitializationEvent): Unit = {
	}

	override def onInstanceCreated(instance: IEsoTeriCraft): Unit = {
		instance.onCreated()
	}

}
