package temportalist.esotericraft.client

import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.api.ApiEsotericraft
import temportalist.origin.api.common.resource.IModDetails
import temportalist.origin.foundation.client.{IKeyBinder, KeyHandler}

/**
  * Created by TheTemportalist on 1/14/2016.
  */
@SideOnly(Side.CLIENT)
object EsotericKeyHandler {

	def preInit(): Unit = {
		/*
		val binders = ListBuffer[KeyBinderWrapper]()
		for (elem <- ApiEsotericraft.Keys.getKeyBinders) {
			val wrapper = new KeyBinderWrapper(elem)
			wrapper.addKeyBindings(ApiEsotericraft.Keys.getKeysForBinder(elem):_*)
			binders += wrapper
		}
		KeyHandler.register(binders:_*)
		*/
		KeyHandler.register((
				for (elem <- ApiEsotericraft.Keys.getKeyBinders)
					yield new KeyBinderWrapper(elem).addKeyBindings(
						ApiEsotericraft.Keys.getKeysForBinder(elem): _*)
				): _*)

	}

	private class KeyBinderWrapper(
			private val binder: ApiEsotericraft.Keys.IKeyBinder) extends IKeyBinder {

		override def getMod: IModDetails = null

		override def register(): Unit = this.binder.register()

		override def getModName: String = ApiEsotericraft.Keys.getModName(this.binder)

		override def onKeyPressed(keyBinding: KeyBinding): Unit =
			this.binder.onKeyPressed(keyBinding)

	}

}
