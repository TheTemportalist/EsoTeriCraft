package temportalist.esotericraft.main.common.capability_new.api

import java.util.concurrent.Callable

import net.minecraftforge.common.capabilities.Capability.IStorage

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
trait CapabilityWrapper[T] {

	def getCapability: Class[T]

	def getImplementation: T

	def getStorage: IStorage[T]

	final def getFactory: Callable[T] = {
		new Callable[T] {

			@throws(classOf[Exception])
			override def call(): T = getImplementation

		}
	}

}
