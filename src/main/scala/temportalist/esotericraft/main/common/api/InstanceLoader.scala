package temportalist.esotericraft.main.common.api

import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
class InstanceLoader[C, T](annotation: Class[C], instance: Class[T])
		extends AnnotationLoader(annotation = annotation, instance = instance) {

	private var instances: ListBuffer[T] = _

	final def getInstances: Seq[T] = this.instances

	override def onAnnotationClassFound(
			implementingClass: Class[_ <: T], annotationInfo: Map[String, AnyRef]): Unit = {
		try {
			val instance = implementingClass.newInstance()
			this.instances += instance
			this.onInstanceCreated(instance)
		}
		catch {
			case e: Exception =>
				e.printStackTrace()
		}
	}

	def onInstanceCreated(instance: T): Unit = {}

}
