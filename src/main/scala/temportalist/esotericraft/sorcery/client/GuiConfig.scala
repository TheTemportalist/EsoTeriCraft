package temportalist.esotericraft.sorcery.client

import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.sorcery.common.Sorcery
import temportalist.origin.foundation.client.gui.GuiConfigBase

/**
  * Created by TheTemportalist on 12/31/2015.
  */
@SideOnly(Side.CLIENT)
class GuiConfig(guiScreen: GuiScreen) extends GuiConfigBase(guiScreen, Sorcery) {}
