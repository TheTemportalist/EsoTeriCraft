package temportalist.esotericraft.galvanization.common.item

import java.util

import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class ItemCreative(itemMetaRange: Range = Range.apply(0, 1))
		extends ItemGalvanize(itemMetaRange = itemMetaRange) {

	@SideOnly(Side.CLIENT)
	override def addInformation(itemStack: ItemStack, playerIn: EntityPlayer,
			tooltip: util.List[String], advanced: Boolean): Unit = {
		tooltip.add(ChatFormatting.YELLOW + "Creative Use Only")
	}

	def canUse(player: EntityPlayer): Boolean = player.capabilities.isCreativeMode

}
