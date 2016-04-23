package temportalist.esotericraft.main.common.capability

import java.util.concurrent.Callable

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject, CapabilityManager, ICapabilitySerializable}
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import temportalist.esotericraft.main.common.capability.api.{CapSerializable, ICapability, StorageBlank}
import temportalist.origin.foundation.common.IMod

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
class CapabilityPlayer extends ICapability[EntityPlayer, NBTTagCompound] {

	def init(world: World, entity: EntityPlayer): Unit = {

	}

	override def getNew: NBTTagCompound = new NBTTagCompound

	override def writeToNBT(nbt: NBTTagCompound): Unit = {

	}

	override def readFromNBT(nbt: NBTTagCompound): Unit = {

	}

}
object CapabilityPlayer {

	@CapabilityInject(classOf[CapabilityPlayer])
	var CAPABILITY: Capability[CapabilityPlayer] = null
	var PROPERTY: ResourceLocation = null

	def register(mod: IMod): Unit = {

		this.PROPERTY = new ResourceLocation(mod.getDetails.getModId, "CapabilityPlayer")

		CapabilityManager.INSTANCE.register(
			classOf[CapabilityPlayer],
			new StorageBlank[CapabilityPlayer],
			new Callable[CapabilityPlayer] {
				override def call(): CapabilityPlayer = null
			}
		)

		mod.registerHandler(this)

	}

	@SubscribeEvent
	def attachCapabilities(event: AttachCapabilitiesEvent.Entity): Unit = {
		val entity = event.getEntity

		val isPlayer = entity.isInstanceOf[EntityPlayer]
		val shouldAddCapability = isPlayer && entity.getCapability(CAPABILITY, null) == null

		if (shouldAddCapability) {

			val cap = new CapabilityPlayer
			if (cap.isValidForCapability(entity)) {
				val casted = cap.getObjectAs(entity)
				cap.init(entity.getEntityWorld, casted)
				event.addCapability(PROPERTY, new CapSerializable[NBTTagCompound, CapabilityPlayer](CAPABILITY, cap))
			}

		}

	}

}
