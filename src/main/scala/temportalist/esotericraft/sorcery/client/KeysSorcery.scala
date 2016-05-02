package temportalist.esotericraft.sorcery.client

import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.input.Keyboard
import temportalist.esotericraft.sorcery.common.Sorcery
import temportalist.esotericraft.sorcery.common.network.PacketKeyPressed
import temportalist.origin.api.client.EnumKeyCategory
import temportalist.origin.foundation.client.IKeyBinder

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
object KeysSorcery extends IKeyBinder {

	var keyCast: KeyBinding = _

	override def register(): Unit = {

		this.keyCast = this.make("cast", Keyboard.KEY_R, EnumKeyCategory.GAMEPLAY)

	}

	def make(desc: String, keyCode: Int, cate: EnumKeyCategory): KeyBinding = {
		val key = new KeyBinding(Sorcery.getModId + ":" + desc, keyCode, cate.getName)
		this.registerKeyBinding(key)
		key
	}

	override def onKeyPressed(keyBinding: KeyBinding): Unit = {
		val action =
			if (keyBinding.getKeyDescription == this.keyCast.getKeyDescription) EnumKeyAction.CAST
			else EnumKeyAction.NONE
		if (action != EnumKeyAction.NONE) {
			new PacketKeyPressed(action).sendToServer(Sorcery)
		}
	}

}
