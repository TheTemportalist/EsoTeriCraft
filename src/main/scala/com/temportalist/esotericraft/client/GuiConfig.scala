package com.temportalist.esotericraft.client

import com.temportalist.esotericraft.common.EsoTeriCraft
import com.temportalist.origin.foundation.client.gui.GuiConfigBase
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * Created by TheTemportalist on 12/31/2015.
  */
@SideOnly(Side.CLIENT)
class GuiConfig(guiScreen: GuiScreen) extends GuiConfigBase(guiScreen,
	EsoTeriCraft, EsoTeriCraft.getModID) {}
