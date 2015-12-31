package com.temportalist.esotericenhancing.server

import com.temportalist.esotericenhancing.common.EnhancingNBT
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent
import net.minecraftforge.fml.relauncher.Side

/**
  * Created by TheTemportalist on 12/31/2015.
  */
object TickHandler {

	@SubscribeEvent
	def playerTick(event: PlayerTickEvent): Unit = {
		if (event.side != Side.SERVER) return
		EnhancingNBT.getAllEnhancements(event.player).foreach(enhancementAndPower => {
			enhancementAndPower._1.onPlayerTick(event.player, enhancementAndPower._2)
		})
	}

}
