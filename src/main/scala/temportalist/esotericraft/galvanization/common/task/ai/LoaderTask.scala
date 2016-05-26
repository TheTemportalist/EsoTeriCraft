package temportalist.esotericraft.galvanization.common.task.ai

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import temportalist.esotericraft.api.galvanize.ai.{GalvanizeTask, IGalvanizeTask}
import temportalist.esotericraft.main.common.api.AnnotationLoader

import scala.collection.mutable

/**
  *
  * Created by TheTemportalist on 5/20/2016.
  *
  * @author TheTemportalist
  */
object LoaderTask extends AnnotationLoader(classOf[GalvanizeTask], classOf[IGalvanizeTask]) {

	def preInit(event: FMLPreInitializationEvent): Unit = {
		this.loadAnnotations(event)
	}

	private val MAP_NAME_TO_CLASS = mutable.Map[String, Class[_ <: IGalvanizeTask]]()

	override def onAnnotationClassFound[I <: IGalvanizeTask](implementer: Class[I],
			info: mutable.Map[String, AnyRef]): Unit = {
		this.MAP_NAME_TO_CLASS(implementer.getName) = implementer
	}

	def getClassFromName(name: String): Class[_ <: IGalvanizeTask] = {
		this.MAP_NAME_TO_CLASS.getOrElse(name, null)
	}

}
