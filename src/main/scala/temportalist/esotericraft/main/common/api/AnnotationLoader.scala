package temportalist.esotericraft.main.common.api

import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import temportalist.esotericraft.galvanization.common.Galvanize

import scala.collection.{JavaConversions, mutable}

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class AnnotationLoader[C, T](private val annotation: Class[C], private val instance: Class[T]) {

	private var classInstances: Map[Class[_ <: T], Map[String, AnyRef]] = _

	final def loadAnnotations(event: FMLPreInitializationEvent): Unit = {
		this.classInstances = this.findInstanceClasses(event.getAsmData)
	}

	final def findInstanceClasses(asmData: ASMDataTable): Map[Class[_ <: T], Map[String, AnyRef]] = {
		val annotationName = this.annotation.getName
		val dataAnnotatedClasses = JavaConversions.asScalaSet(asmData.getAll(annotationName))
		val classes = mutable.Map[Class[_ <: T], Map[String, AnyRef]]()
		for (dataAnnotatedClass <- dataAnnotatedClasses) {
			try {
				val annotatedClass = Class.forName(dataAnnotatedClass.getClassName)
				val annotatedClassAsSub = annotatedClass.asSubclass(this.instance)
				val annotationInfo = JavaConversions.mapAsScalaMap(dataAnnotatedClass.getAnnotationInfo).toMap
				classes.put(annotatedClassAsSub, annotationInfo)
				this.onAnnotationClassFound(annotatedClassAsSub, annotationInfo)
			}
			catch {
				case e: Exception =>
					e.printStackTrace()
			}
		}
		classes.toMap
	}

	def onAnnotationClassFound(implementingClass: Class[_ <: T], annotationInfo: Map[String, AnyRef]): Unit = {}

	final def getClassInstances: Iterable[Class[_ <: T]] = this.classInstances.keys

	final def getAnnotationInfo(clazz: Class[_ <: T]) =
		this.classInstances.getOrElse(clazz, Map[String, AnyRef]())

}
