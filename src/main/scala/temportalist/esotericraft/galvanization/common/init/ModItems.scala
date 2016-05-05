package temportalist.esotericraft.galvanization.common.init

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import temportalist.esotericraft.galvanization.common.item.ItemEggGolem
import temportalist.origin.foundation.common.registers.ItemRegister

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
object ModItems extends ItemRegister {

	var golemEgg: Item = null

	override def register(): Unit = {

		this.golemEgg = new ItemEggGolem
		this.golemEgg.setCreativeTab(CreativeTabs.MISC)

	}

}
