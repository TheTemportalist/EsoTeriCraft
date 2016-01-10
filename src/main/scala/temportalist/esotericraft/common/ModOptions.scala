package temportalist.esotericraft.common

import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.foundation.common.register.OptionRegister

/**
  * Created by TheTemportalist on 1/3/2016.
  */
object ModOptions extends OptionRegister {

	var biomeEsotericID: Int = -1
	var biomeUmbraID: Int = -1

	override def getExtension: String = "json"

	override def register(): Unit = {
		this.biomeEsotericID = this.getAndComment(
			"general", "Esoteric Biome ID", "", this.findFreeBiomeID)
		if (this.biomeEsotericID < 0) this.biomeEsotericID = this.findFreeBiomeID
		this.biomeUmbraID = this.getAndComment(
			"general", "Umbra Biome ID", "", this.findFreeBiomeID)
		if (this.biomeUmbraID < 0) this.biomeUmbraID = this.findFreeBiomeID

	}

	@SideOnly(Side.CLIENT) override
	def mainConfigGuiClass(): Class[_ <: GuiScreen] = null

}
