package com.temportalist.esotericenhancing.common

import com.temportalist.esotericenhancing.common.init.{Enhancements, ModBlocks}
import com.temportalist.esotericraft.api.ApiEsotericraft
import com.temportalist.origin.api.common.resource.{IModDetails, IModResource}
import com.temportalist.origin.foundation.common.IMod
import com.temportalist.origin.foundation.common.proxy.IProxy
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{EntityDamageSource, EnumChatFormatting}
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.player.{AttackEntityEvent, ItemTooltipEvent}
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.{Mod, SidedProxy}

/**
  * Created by TheTemportalist on 12/31/2015.
  */
@Mod(modid = Enhancing.MOD_ID, name = Enhancing.MOD_NAME,
	version = Enhancing.MOD_VERSION, modLanguage = "scala",
	guiFactory = Enhancing.proxyClient,
	dependencies = "required-after:esotericraft;"
)
object Enhancing extends IMod with IModResource {

	// ~~~~~~~~~~~ Mod Setup ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val MOD_ID = "esotericenhancing"
	final val MOD_NAME = "Esoteric Enhancing"
	final val MOD_VERSION = "1.0.0"

	override def getModID: String = this.MOD_ID

	override def getModVersion: String = this.MOD_VERSION

	override def getModName: String = this.MOD_NAME

	override def getDetails: IModDetails = this

	// ~~~~~~~~~~~ Proxy ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val proxyClient = "com.temportalist.esotericenhancing.client.ProxyClient"
	final val proxyServer = "com.temportalist.esotericenhancing.server.ProxyServer"

	@SidedProxy(clientSide = proxyClient, serverSide = proxyServer)
	var proxy: IProxy = null

	// ~~~~~~~~~~~ Inits ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(this, event, this.proxy, null, ModBlocks, Enhancements)
		ApiEsotericraft.registerModule(HookHandlerEnhancing)

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event, this.proxy)
	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event, this.proxy)
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	val GUI_ENHANCEMENT = 0

	@SubscribeEvent
	def toolTipEvent(event: ItemTooltipEvent): Unit = {
		if (EnhancingNBT.isEnhanced(event.itemStack)) {
			EnhancingNBT.getEnhancements(event.itemStack).foreach(enhancement => {
				event.toolTip.add(
					EnumChatFormatting.LIGHT_PURPLE +
							enhancement._1.getName +
							EnumChatFormatting.DARK_PURPLE +
							" (" + enhancement._2 + ")"
				)
			})
		}
	}

	@SubscribeEvent
	def livingAttack(event: LivingAttackEvent): Unit = {
		event.source match {
			case damageSource: EntityDamageSource =>
				if (damageSource.getIsThornsDamage) return
				damageSource.getEntity match {
					case player: EntityPlayer => // player is attacker
						EnhancingNBT.foreachEnhancement(player, (enhancement, power) => {
							enhancement.onPlayerAttacking(player, event.entityLiving, power)
						})
					case _ =>
				}
				event.entityLiving match {
					case player: EntityPlayer => // player is being attacked
						EnhancingNBT.foreachEnhancement(player, (enhancement, power) => {
							enhancement.onPlayerAttacked(player, damageSource.getEntity, power)
						})
					case _ =>
				}
			case _ =>
		}
	}

}
