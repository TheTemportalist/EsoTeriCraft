package temportalist.esotericraft.sorcery.client

import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.sorcery.common.Sorcery
import temportalist.origin.foundation.client.modTraits.IHasKeys
import temportalist.origin.foundation.client.{IKeyBinder, IModClient}
import temportalist.origin.foundation.common.IMod

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
object Client extends IModClient with IHasKeys {

	override def getMod: IMod = Sorcery

	/**
	  * This needs to be called in [[temportalist.origin.foundation.common.IProxy.preInit]]
	  */
	override def preInit(): Unit = {
		super.preInit()
		KeysSorcery.register()

	}

	@SideOnly(Side.CLIENT)
	override def getKeyBinder: IKeyBinder = KeysSorcery

}
