package temportalist.esotericraft.galvanization.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import temportalist.esotericraft.galvanization.server.Server
import temportalist.origin.foundation.common.IProxy

/**
  * Created by TheTemportalist on 12/31/2015.
  */
class ProxyCommon extends IProxy {

	override def preInit(): Unit = {
		Server.preInit()

	}

	override def register(): Unit = {}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = null

	override def getServerElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = null

}
