package temportalist.esotericraft.main.common.api

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import temportalist.esotericraft.api.init.{IEsoTeriCraft, PluginEsoTeriCraft}

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
object ApiLoader extends AnnotationLoader(classOf[PluginEsoTeriCraft], classOf[IEsoTeriCraft]) {

	def preInit(event: FMLPreInitializationEvent): Unit = {
		this.loadPlugins(event)
	}

	def init(event: FMLInitializationEvent): Unit = {
	}

	def postInit(event: FMLPostInitializationEvent): Unit = {
	}

	override def onInstanceCreated(instance: IEsoTeriCraft): Unit = {
		instance.onCreated()
	}

}
