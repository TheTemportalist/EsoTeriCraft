package temportalist.esotericraft.galvanization.common.entity.ai

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import temportalist.esotericraft.api.galvanize.ai.{AIEmpty, AIEmptyHelper, EntityAIEmpty, EntityAIHelperObj}
import temportalist.esotericraft.main.common.api.{AnnotationLoader, InstanceLoader}

import scala.collection.mutable

/**
  *
  * Created by TheTemportalist on 5/20/2016.
  *
  * @author TheTemportalist
  */
object LoaderAI extends AnnotationLoader(classOf[AIEmpty], classOf[EntityAIEmpty]) {

	def preInit(event: FMLPreInitializationEvent): Unit = {
		this.loadAnnotations(event)
		HelperLoader.loadAnnotations(event)
	}

	private val MAP_NAME_TO_CLASS = mutable.Map[String, Class[_ <: EntityAIEmpty]]()

	override def onAnnotationClassFound[I <: EntityAIEmpty](implementer: Class[I],
			info: mutable.Map[String, AnyRef]): Unit = {
		this.MAP_NAME_TO_CLASS(implementer.getName) = implementer
	}

	def getClassFromName(name: String): Class[_ <: EntityAIEmpty] = {
		this.MAP_NAME_TO_CLASS.getOrElse(name, null)
	}

	private object HelperLoader extends InstanceLoader(classOf[AIEmptyHelper], classOf[EntityAIHelperObj]) {

		val MAP_AICLASS_TO_HELPER = mutable.Map[String, EntityAIHelperObj]()

		override def onAnnotationClassFound[I <: EntityAIHelperObj](implementer: Class[I],
				info: mutable.Map[String, AnyRef]): Unit = {
			super.onAnnotationClassFound(implementer, info)

			try {
				val helper = implementer.getConstructor().newInstance()
				this.MAP_AICLASS_TO_HELPER.put(helper.getClassAI.getName, helper)
			}
			catch {
				case e: Exception =>
					e.printStackTrace()
			}

		}

	}

	def getHelperForAIClass(aiClass: Class[EntityAIEmpty]): EntityAIHelperObj = {
		this.getHelperForAIClass(aiClass.getName)
	}

	def getHelperForAIClass(aiClass: String): EntityAIHelperObj = {
		HelperLoader.MAP_AICLASS_TO_HELPER.getOrElse(aiClass, null)
	}

}
