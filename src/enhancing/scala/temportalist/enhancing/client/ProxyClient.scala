package temportalist.enhancing.client

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import temportalist.enhancing.api.ApiEsotericEnhancing
import temportalist.enhancing.client.gui.MenuEnhancing
import temportalist.enhancing.common.tile.TEEnhancingTable
import temportalist.enhancing.common.{Enhancing, ProxyCommon}
import temportalist.enhancing.temp.PieMenu

/**
  * Created by TheTemportalist on 12/31/2015.
  */
class ProxyClient extends ProxyCommon {

	override def register(): Unit = {

		ClientRegistry.bindTileEntitySpecialRenderer(classOf[TEEnhancingTable], TERenderEnhancingTable)

	}

	private var enhancingMenu: PieMenu[_] = null

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		val esoteric = ApiEsotericEnhancing.getProps(player)
		if (esoteric == null) return null
		world.getTileEntity(new BlockPos(x, y, z)) match {
			case tile: TEEnhancingTable =>
				if (ID == Enhancing.GUI_ENHANCEMENT) {
					this.enhancingMenu = new MenuEnhancing(
						tile, esoteric, tile.getValidEnhancements(esoteric))
					this.enhancingMenu.open()
				}
			case _ =>
		}
		null
	}

	def clearMenu(): Unit = {
		this.enhancingMenu = null
	}

	@SubscribeEvent
	def gameOverlay(event: RenderGameOverlayEvent): Unit = {
		if (event.`type` == RenderGameOverlayEvent.ElementType.TEXT) {
			if (this.enhancingMenu != null) {
				this.enhancingMenu.onTick(event.resolution, event.partialTicks)
			}
		}
	}

}
