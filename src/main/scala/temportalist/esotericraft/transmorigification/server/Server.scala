package temportalist.esotericraft.transmorigification.server

import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.{Phase, ServerTickEvent}
import temportalist.esotericraft.transmorigification.common.Transform
import temportalist.esotericraft.transmorigification.common.capability.{HelperGalvanize, IPlayerGalvanize}

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
object Server {

	def preInit(): Unit = {

		Transform.registerHandler(this)

	}

	@SubscribeEvent
	def tick(event: ServerTickEvent): Unit = {
		if (event.phase == Phase.END) {
			val mc = FMLCommonHandler.instance().getMinecraftServerInstance
			val players = JavaConversions.asScalaBuffer(mc.getPlayerList.getPlayerList)
			for (player <- players) {
				HelperGalvanize.get(player) match {
					case galvanized: IPlayerGalvanize =>
						galvanized.onTickServer()
					case _ =>
				}
			}
		}
	}

}
