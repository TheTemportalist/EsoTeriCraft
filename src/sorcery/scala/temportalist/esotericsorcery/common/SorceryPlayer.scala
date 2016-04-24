package temportalist.esotericsorcery.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject}
import temportalist.esotericraft.api.sorcery.ISorceryPlayer
import temportalist.esotericraft.main.common.capability.EsotericPlayer
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

	@CapabilityInject(classOf[SorceryPlayer])
	var CAPABILITY: Capability[SorceryPlayer] = _

	def register(): Unit = {
		super.register(Sorcery, "SorceryPlayer")
		Sorcery.log("Registered Capability SorceryPlayer")
		Sorcery.log("" + this.CAPABILITY)
	}

	// ~~~~~~~~~~ Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getClassCapability: Class[SorceryPlayer] = classOf[SorceryPlayer]

	override def getCapability: Capability[SorceryPlayer] = this.CAPABILITY

	override def getNewCapabilityInstance: SorceryPlayer = new SorceryPlayer

}
