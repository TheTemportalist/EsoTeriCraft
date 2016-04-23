package temportalist.esotericraft.main.common.capability.api

import java.util.concurrent.Callable

import net.minecraft.nbt.NBTBase
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.CapabilityManager
import temportalist.origin.foundation.common.IMod

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
abstract class Handler[E, N <: NBTBase, T <: ICapability[E, N]] {

	private var CAPABILITY_KEY: ResourceLocation = null

	def getClassCapability: Class[T]

	def register(mod: IMod, key: String): Unit = {

		this.CAPABILITY_KEY = new ResourceLocation(mod.getDetails.getModId, key)

		CapabilityManager.INSTANCE.register(
			this.getClassCapability,
			new StorageBlank[T],
			new Callable[T] {
				override def call(): T = null.asInstanceOf[T]
			}
		)

		mod.registerHandler(this)

	}

}
