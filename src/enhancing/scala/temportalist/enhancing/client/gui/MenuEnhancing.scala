package temportalist.enhancing.client.gui

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.enhancing.api.IEnhancingPlayer
import temportalist.enhancing.client.ProxyClient
import temportalist.enhancing.common.Enhancing
import temportalist.enhancing.common.enhancement.EnhancementWrapper
import temportalist.enhancing.common.network.PacketEnhancingTable_Server
import temportalist.enhancing.common.tile.TEEnhancingTable
import temportalist.enhancing.temp.PieMenu

/**
  * Created by TheTemportalist on 1/8/2016.
  */
@SideOnly(Side.CLIENT)
class MenuEnhancing(private val tile: TEEnhancingTable,
		private val esotericPlayer: IEnhancingPlayer,
		private val objectArray: Array[EnhancementWrapper]) extends PieMenu[EnhancementWrapper] {

	override def getPieObjectList: Array[EnhancementWrapper] = this.objectArray

	override def shouldContinueToDisplay: Boolean = tile != null && !tile.isInvalid

	override def close(): Unit = {
		super.close()
		// can cast proxy to ProxyClient because this is only running client side
		Enhancing.proxy.asInstanceOf[ProxyClient].clearMenu()
		this.tile.setGuiNotOpen(true)
		new PacketEnhancingTable_Server(tile,
			PacketEnhancingTable_Server.PacketType.UNLOCK).sendToServer(Enhancing)
	}

	override def selectObj(index: Int, obj: EnhancementWrapper): Unit = {
		new PacketEnhancingTable_Server(tile, PacketEnhancingTable_Server.PacketType.SELECT).
				add(obj.getGlobalID).sendToServer(Enhancing)
	}

	override def getCenterBackground: ResourceLocation = Enhancing.getResource("enhancing_fore")

	override def getBorderBackground: ResourceLocation = Enhancing.getResource("enhancing_back")

}
