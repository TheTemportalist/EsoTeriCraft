package temportalist.enhancing.server

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent
import net.minecraftforge.fml.relauncher.Side
import temportalist.enhancing.common.EnhancingNBT

/**
  * Created by TheTemportalist on 12/31/2015.
  */
object TickHandler {

	@SubscribeEvent
	def playerTick(event: PlayerTickEvent): Unit = {
		if (event.side != Side.SERVER) return
		// todo get enhancements only every few seconds
		EnhancingNBT.getAllEnhancements(event.player).foreach(enhancementAndPower => {
			enhancementAndPower._1.onPlayerTick(event.player, enhancementAndPower._2)
		})
	}

}
