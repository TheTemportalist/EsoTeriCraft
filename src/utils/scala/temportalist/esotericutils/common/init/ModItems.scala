package temportalist.esotericutils.common.init

import net.minecraft.creativetab.CreativeTabs
import temportalist.origin.foundation.common.registers.ItemRegister

/**
  *
  * Created by TheTemportalist on 4/26/2016.
  *
  * @author TheTemportalist
  */
object ModItems extends ItemRegister {

	var spindle: ItemSpindle = _

	override def register(): Unit = {

		this.spindle = this.registerObject(new ItemSpindle(this.getMod))
		this.spindle.setCreativeTab(CreativeTabs.TOOLS)

	}

}
