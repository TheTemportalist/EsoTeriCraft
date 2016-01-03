package com.temportalist.esotericenhancing.common.enhancement.attack

import com.temportalist.esotericenhancing.api.Enhancement
import com.temportalist.esotericenhancing.common.EnhancingNBT
import net.minecraft.enchantment.{Enchantment, EnchantmentHelper}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.stats.StatList
import net.minecraftforge.event.entity.player.ArrowLooseEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

// TODO unfinished
/**
  *
  * Created by TheTemportalist on 12/31/2015.
  */
object EnArchery extends Enhancement("archery") {

	@SubscribeEvent
	def arrowLoose(event: ArrowLooseEvent): Unit = {
		if (EnhancingNBT.hasEnhancement(event.bow, this)) {
			val power = EnhancingNBT.getPower(event.bow, this)
			if (power >= 0f) {
				event.setCanceled(true)
				this.looseArrow(event.bow, event.charge, event.entityPlayer, power)
			}
		}
	}

	def looseArrow(bowStack: ItemStack, charge: Int, player: EntityPlayer, power: Float): Unit = {
		val world = player.getEntityWorld
		val dontConsumeArrow = player.capabilities.isCreativeMode ||
				EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bowStack) > 0

		var f: Float = charge.toFloat / 20.0F
		f = (f * f + f * 2.0F) / 3.0F
		if (f.toDouble < 0.1D) return
		if (f > 1.0F) f = 1.0F

		val entityArrow: EntityArrow = new EntityArrow(world, player, f * 2.0F)
		if (f == 1.0F) entityArrow.setIsCritical(true)

		val j: Int = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, bowStack)
		if (j > 0) entityArrow.setDamage(entityArrow.getDamage + j.toDouble * 0.5D + 0.5D)

		val k: Int = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, bowStack)
		if (k > 0) entityArrow.setKnockbackStrength(k)

		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, bowStack) > 0)
			entityArrow.setFire(100)

		bowStack.damageItem(1, player)
		world.playSoundAtEntity(player, "random.bow", 1.0F,
			1.0F / (world.rand.nextFloat * 0.4F + 1.2F) + f * 0.5F)

		if (dontConsumeArrow) entityArrow.canBePickedUp = 2
		else player.inventory.consumeInventoryItem(Items.arrow)

		player.triggerAchievement(StatList.objectUseStats(Item.getIdFromItem(Items.bow)))

		if (!world.isRemote) world.spawnEntityInWorld(entityArrow)
	}

}
