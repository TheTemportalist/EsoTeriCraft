package temportalist.esotericraft.main.client.plugin

import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.foundation.client.IKeyBinder

// TODO move to Origin
/**
  * replace [[temportalist.origin.foundation.client.modTraits.IHasKeys]] with this
  * Created by TheTemportalist on 6/2/2016.
  *
  * @author TheTemportalist
  */
trait IKeyBinderOwner {

	@SideOnly(Side.CLIENT)
	def getKeyBinders: Seq[IKeyBinder]

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	final def onMouse(event: MouseEvent): Unit = {
		this.getKeyBinders.foreach(_.checkBindingsForPress(event.getButton + 100))
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	final def onKey(event: KeyInputEvent): Unit = {
		this.getKeyBinders.foreach(_.checkBindingsForPress(-1))
	}

}
