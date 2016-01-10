package temportalist.enhancing.common.enhancement.gear.passive

import net.minecraft.entity.player.EntityPlayer
import temportalist.enhancing.api.Enhancement

/**
  * Regenerates the player's health per tick
  * Conversions: 1.0 power = 1.0 hearts = 2.0 health per TICK
  *              0.2 power = 0.2 hearts = 0.4 health per SECOND
  * Created by TheTemportalist on 12/31/2015.
  */
class EnRegeneration extends Enhancement("regeneration") {

	override def computePower(powers: Array[Float]): Float = {
		var sum = 0f
		powers.foreach(power => sum += power)
		sum
	}

	override def onPlayerTick(player: EntityPlayer, power: Float): Unit = {
		if (player.getHealth < player.getMaxHealth)
			player.setHealth(2 * power + player.getHealth)
	}

}
