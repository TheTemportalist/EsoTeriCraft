package temportalist.esotericraft.sorcery.client

import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.input.Keyboard
import temportalist.esotericraft.sorcery.common.Sorcery
import temportalist.origin.api.client.{EnumHUDOverlay, EnumKeyCategory}
import temportalist.origin.foundation.client.{IKeyBinder, IModClient}
import temportalist.origin.foundation.client.modTraits.IHasKeys
import temportalist.origin.foundation.common.IModPlugin
import temportalist.origin.internal.common.Origin

/**
  *
  * Created by TheTemportalist on 5/6/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
object Client extends IModClient with IHasKeys {

	override def getMod: IModPlugin = Sorcery

	/**
	  * This needs to be called in [[temportalist.origin.foundation.common.IProxy.preInit]]
	  */
	override def preInit(): Unit = {
		super.preInit()


	}

	@SideOnly(Side.CLIENT)
	override def getKeyBinder: IKeyBinder = ModKeys

	object ModKeys extends IKeyBinder {

		var cast: KeyBinding = _

		override def register(): Unit = {

			this.cast = new KeyBinding("desc", Keyboard.KEY_C, EnumKeyCategory.GAMEPLAY.getName)
			this.registerKeyBinding(this.cast)

		}

		override def onKeyPressed(keyBinding: KeyBinding): Unit = {
			if (keyBinding.getKeyDescription == this.cast.getKeyDescription) {

			}
		}

	}

}
