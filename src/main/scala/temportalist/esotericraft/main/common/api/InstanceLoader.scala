package temportalist.esotericraft.main.common.api

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
class InstanceLoader[C, T](annotation: Class[C], instance: Class[T])
		extends AnnotationLoader(annotation = annotation, instance = instance) {

	private var instances: ListBuffer[T] = ListBuffer[T]()

	final def getInstances: Seq[T] = this.instances

	override def onAnnotationClassFound[I <: T](implementer: Class[I],
			info: mutable.Map[String, AnyRef]): Unit = {
		try {
			val instance = implementer.newInstance()
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
