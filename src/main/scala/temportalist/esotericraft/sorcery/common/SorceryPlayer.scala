package temportalist.esotericraft.sorcery.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import temportalist.esotericraft.api.capability.EsotericPlayer
import temportalist.esotericraft.api.sorcery.ISorceryPlayer
import temportalist.origin.foundation.common.IMod

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
class SorceryPlayer extends EsotericPlayer with ISorceryPlayer {

	override def init(world: World, player: EntityPlayer): Unit = {

	}

	override def getModForPacketSync: IMod = Sorcery

	override def onDataReceived(nbt: NBTTagCompound): Unit = {

	}

	// ~~~~~~~~~~ Sleeping ~~~~~~~~~~

	private var isSleeping = false
	private var spawnPoint: BlockPos = _

	def setSleepStart(spawn: BlockPos): Unit = {
		this.isSleeping = true
		this.spawnPoint = spawn
	}

	def setSleepEnd(): Unit = {
		this.isSleeping = false
		this.spawnPoint = null
	}

	def isPlayerSleeping: Boolean = isSleeping

}
object SorceryPlayer extends EsotericPlayer.Handler[SorceryPlayer] {

	// ~~~~~~~~~~ Setup And Register ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def register(): Unit = {
		//super.register(Sorcery, "SorceryPlayer")
		val mod = Sorcery
		val key = "SorceryPlayer"

		this.CAPABILITY_KEY = new ResourceLocation(mod.getDetails.getModId, key)

		CapabilitySorcery.register()

		mod.registerHandler(this)

		Sorcery.log("Registered Capability SorceryPlayer")
		Sorcery.log("" + CapabilitySorcery.CAPABILITY)
	}

	// ~~~~~~~~~~ Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getClassCapability: Class[SorceryPlayer] = classOf[SorceryPlayer]

	override def getCapability: Capability[SorceryPlayer] = CapabilitySorcery.CAPABILITY

	override def getNewCapabilityInstance: SorceryPlayer = new SorceryPlayer

}
