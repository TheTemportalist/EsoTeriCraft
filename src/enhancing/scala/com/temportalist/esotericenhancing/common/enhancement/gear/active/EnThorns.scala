package com.temportalist.esotericenhancing.common.enhancement.gear.active

import com.temportalist.esotericenhancing.api.Enhancement
import net.minecraft.entity.{EntityLivingBase, Entity}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.DamageSource

/**
  * Power is 1:1 to damage
  * Created by TheTemportalist on 12/31/2015.
  */
object EnThorns extends Enhancement("thorns") {

	override def onPlayerAttacking(player: EntityPlayer, entity: Entity, power: Float): Unit = {
		entity match {
			case living: EntityLivingBase =>
				living.attackEntityFrom(DamageSource.causeThornsDamage(player), power)
				living.playSound("damage.thorns", 0.5F, 1.0F)
			case _ =>
		}
	}

}
