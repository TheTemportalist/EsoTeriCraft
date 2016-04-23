package temportalist.esotericraft.main.common.capability.api

import java.util.concurrent.Callable

import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.nbt.NBTBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.{Capability, CapabilityManager}
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import temportalist.origin.foundation.common.IMod

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
abstract class Handler[E, N <: NBTBase, T <: ICapability[E, N]] {

	protected var CAPABILITY_KEY: ResourceLocation = null

	def getClassCapability: Class[T]

	def getCapability: Capability[T]

	def getNewCapabilityInstance: T

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
object Handler {

	abstract class HandlerEntity[E <: Entity, N <: NBTBase, T <: ICapability[E, N]]
			extends Handler[E, N, T] {

		@SubscribeEvent
		def attachCapabilities(event: AttachCapabilitiesEvent.Entity): Unit = {
			event.getEntity match {
				case e: E =>
					val cap = this.getNewCapabilityInstance
					cap.initEntity(e.getEntityWorld, e)
					event.addCapability(this.CAPABILITY_KEY,
						new CapSerializable[N, T](this.getCapability, cap)
					)
				case _ =>
			}
		}

	}

	abstract class HandlerTile[E <: TileEntity, N <: NBTBase, T <: ICapability[E, N]]
			extends Handler[E, N, T] {

		@SubscribeEvent
		def attachCapabilities(event: AttachCapabilitiesEvent.TileEntity): Unit = {
			event.getTileEntity match {
				case e: E =>
					val cap = this.getNewCapabilityInstance
					cap.initEntity(e.getWorld, e)
					event.addCapability(this.CAPABILITY_KEY,
						new CapSerializable[N, T](this.getCapability, cap)
					)
				case _ =>
			}
		}

	}

	abstract class HandlerItem[E <: Item, N <: NBTBase, T <: ICapability[E, N]]
			extends Handler[E, N, T] {

		@SubscribeEvent
		def attachCapabilities(event: AttachCapabilitiesEvent.Item): Unit = {
			event.getItem match {
				case e: E =>
					val cap = this.getNewCapabilityInstance
					cap.initItem(e, event.getItemStack)
					event.addCapability(this.CAPABILITY_KEY,
						new CapSerializable[N, T](this.getCapability, cap)
					)
				case _ =>
			}
		}

	}

}
