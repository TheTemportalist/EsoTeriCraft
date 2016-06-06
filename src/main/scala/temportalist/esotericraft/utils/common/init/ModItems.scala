package temportalist.esotericraft.utils.common.init

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.ShapedOreRecipe
import temportalist.esotericraft.main.common.EsoTeriCraft
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

		this.spindle = this.registerObject(new ItemSpindle(EsoTeriCraft))
		this.spindle.setCreativeTab(CreativeTabs.TOOLS)

	}

	override def registerCrafting(): Unit = {

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this.spindle, 4), "i", "s",
			Char.box('i'), "ingotIron", Char.box('s'), "cobblestone"
		))

	}

}
