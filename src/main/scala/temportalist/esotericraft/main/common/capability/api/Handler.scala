package temportalist.esotericraft.main.common.capability.api

import java.util.concurrent.Callable

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.nbt.NBTBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.{Capability, CapabilityManager}
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import temportalist.origin.foundation.common.IMod

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
abstract class Handler[E, N <: NBTBase, T <: ICapability[E, N]](
		private val persistDeath: Boolean = false
) {

	final def get(player: EntityPlayer): T = player.getCapability(this.getCapability, null)

	protected var CAPABILITY_KEY: ResourceLocation = null

	def isValid(e: AnyRef): Boolean

	def cast(e: AnyRef): E

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

	// ~~~~~~~~~~ Death Persistence ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@SubscribeEvent
	def onPlayerClone(event: PlayerEvent.Clone): Unit = {
		if (!this.persistDeath || !event.isWasDeath) return
		val data = this.get(event.getOriginal).serializeNBT
		this.get(event.getEntityPlayer).deserializeNBT(data)
	}

}
object Handler {

	abstract class HandlerEntity[E <: Entity, N <: NBTBase, T <: ICapability[E, N]](
			private val persistDeath: Boolean = false
	) extends Handler[E, N, T](persistDeath = persistDeath) {

		@SubscribeEvent
		def attachCapabilities(event: AttachCapabilitiesEvent.Entity): Unit = {
			val entity = event.getEntity
			if (this.isValid(entity)) {
				val e = this.cast(entity)
				if (this.getCapability == null) {
					FMLLog.info("ERROR: Capability for " + this.getClassCapability.getCanonicalName + " is NULL!!!")
				}
				FMLLog.info("Attaching capability entity " + this.CAPABILITY_KEY.toString + " to " + e.getClass.getCanonicalName)
				val cap = this.getNewCapabilityInstance
				cap.initEntity(e.getEntityWorld, e)
				event.addCapability(this.CAPABILITY_KEY,
					new CapSerializable[N, T](this.getCapability, cap)
				)
			}
		}

	}

	abstract class HandlerTile[E <: TileEntity, N <: NBTBase, T <: ICapability[E, N]](
			private val persistDeath: Boolean = false
	) extends Handler[E, N, T](persistDeath = persistDeath) {

		@SubscribeEvent
		def attachCapabilities(event: AttachCapabilitiesEvent.TileEntity): Unit = {
			val tile = event.getTileEntity
			if (this.isValid(tile)) {
				val e = this.cast(tile)
				if (this.getCapability == null) {
					FMLLog.info("ERROR: Capability for " + this.getClassCapability.getCanonicalName + " is NULL!!!")
				}
				FMLLog.info("Attaching capability tile " + this.CAPABILITY_KEY.toString + " to " + e.getClass.getCanonicalName)
				val cap = this.getNewCapabilityInstance
				cap.initEntity(e.getWorld, e)
				event.addCapability(this.CAPABILITY_KEY,
					new CapSerializable[N, T](this.getCapability, cap)
				)
			}
		}

	}

	abstract class HandlerItem[E <: Item, N <: NBTBase, T <: ICapability[E, N]](
			private val persistDeath: Boolean = false
	) extends Handler[E, N, T](persistDeath = persistDeath) {

		@SubscribeEvent
		def attachCapabilities(event: AttachCapabilitiesEvent.Item): Unit = {
			val item = event.getItem
			if (this.isValid(item)) {
				val e = this.cast(item)
				if (this.getCapability == null) {
					FMLLog.info("ERROR: Capability for " + this.getClassCapability.getCanonicalName + " is NULL!!!")
				}
				FMLLog.info("Attaching capability item " + this.CAPABILITY_KEY.toString + " to " + e.getClass.getCanonicalName)
				val cap = this.getNewCapabilityInstance
				cap.initItem(e, event.getItemStack)
				event.addCapability(this.CAPABILITY_KEY,
					new CapSerializable[N, T](this.getCapability, cap)
				)
			}
		}

	}

}
