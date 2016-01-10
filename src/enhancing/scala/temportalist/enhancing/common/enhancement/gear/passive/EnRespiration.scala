package temportalist.enhancing.common.enhancement.gear.passive

import java.util.Random
import net.minecraft.entity.player.EntityPlayer
import temportalist.enhancing.api.Enhancement

/**
  * 1 Power = 15 seconds breathing
  * Power MUST BE INTEGER
  * Created by TheTemportalist on 1/1/2016.
  */
class EnRespiration extends Enhancement("respiration") {

	override def onPlayerTick(player: EntityPlayer, power: Float): Unit = {
		player.setAir(this.decreaseAirSupply(player.getRNG, player.getAir, power))
	}

	// See EntityLivingBase.decreaseAirSupply(int air): int
	def decreaseAirSupply(rand: Random, air: Int, power: Float): Int = {
		val powerInt = power.toInt
		if (powerInt > 0 && rand.nextInt(powerInt + 1) > 0) air else air - 1
	}

}
