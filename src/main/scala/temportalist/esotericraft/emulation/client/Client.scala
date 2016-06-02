package temportalist.esotericraft.emulation.client

import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.api.init.IPluginClient

/**
  *
  * Created by TheTemportalist on 6/2/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
object Client extends IPluginClient {

	override def preInit(): Unit = {
		MinecraftForge.EVENT_BUS.register(this)

	}

	private var hasLoadedAGui = false

	@SubscribeEvent
	def initGuiPost(event: InitGuiEvent.Post): Unit = {
		if (!this.hasLoadedAGui) {
			this.hasLoadedAGui = true

			ModelHandler.loadEntityModels()

		}
	}

}
