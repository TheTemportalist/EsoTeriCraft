package temportalist.esotericraft.main.common.capability

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject}
import temportalist.esotericraft.main.common.EsoTeriCraft
import temportalist.esotericraft.main.common.capability.api.Handler.HandlerEntity
import temportalist.esotericraft.main.common.capability.api.ICapability

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
object CapabilityPlayer extends HandlerEntity[EntityPlayer, NBTTagCompound, CapabilityPlayer] {

	def register(): Unit = {
		super.register(EsoTeriCraft, "EsotericPlayer")
		EsoTeriCraft.log("Registered Capability EsotericPlayer")
	}

	@CapabilityInject(classOf[CapabilityPlayer])
	var CAPABILITY: Capability[CapabilityPlayer] = null

	override def getClassCapability: Class[CapabilityPlayer] = classOf[CapabilityPlayer]

	override def getCapability: Capability[CapabilityPlayer] = this.CAPABILITY

	override def getNewCapabilityInstance: CapabilityPlayer = new CapabilityPlayer

}
