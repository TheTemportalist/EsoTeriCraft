package temportalist.esotericraft.main.common.api

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import temportalist.esotericraft.api.{EsoTeriCraft, IPlugin}

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
object ApiLoader extends AnnotationLoader(classOf[EsoTeriCraft], classOf[IPlugin]) {

	def preInit(event: FMLPreInitializationEvent): Unit = {
		this.loadPlugins(event)
		for (plugin <- this.getPlugins) plugin.preInit(event)
	}

	def init(event: FMLInitializationEvent): Unit = {
		for (plugin <- this.getPlugins) plugin.init(event)
	}

	def postInit(event: FMLPostInitializationEvent): Unit = {
		for (plugin <- this.getPlugins) plugin.postInit(event)
	}

}
