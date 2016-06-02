package temportalist.esotericraft.galvanization.client

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.origin.foundation.client.IModClient
import temportalist.origin.foundation.common.IModPlugin

/**
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
object Client extends IModClient {

	override def getMod: IModPlugin = Galvanize

	/**
	  * This needs to be called in [[temportalist.origin.foundation.common.IProxy.preInit]]
	  */
	override def preInit(): Unit = {
		super.preInit()

		MinecraftForge.EVENT_BUS.register(ClientTask)

	}

}
