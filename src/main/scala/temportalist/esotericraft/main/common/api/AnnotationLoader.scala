package temportalist.esotericraft.main.common.api

import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

import scala.collection.JavaConversions
import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class AnnotationLoader[T](private val annotation: Class[_], private val instance: Class[T]) {

	private var plugins: Seq[T] = _

	final def loadPlugins(event: FMLPreInitializationEvent): Unit = {
		this.plugins = this.getInstances(event.getAsmData)
	}

	final def getPlugins: Seq[T] = this.plugins

	final def getInstances(asmData: ASMDataTable): Seq[T] = {
		val annotationName = this.annotation.getCanonicalName
		val dataAnnotatedClasses = JavaConversions.asScalaSet(asmData.getAll(annotationName))
		val instances = ListBuffer[T]()
		for (dataAnnotatedClass <- dataAnnotatedClasses) {
			try {
				val annotatedClass = Class.forName(dataAnnotatedClass.getClassName)
				val instanceClass = annotatedClass.asSubclass(this.instance)
				val instance = instanceClass.newInstance()
				instances += instance
				this.onInstanceCreated(instance)
			}
			catch {
				case e: Exception =>
					e.printStackTrace()
			}
		}
		instances
	}

	def onInstanceCreated(instance: T): Unit = {}

}
