package temportalist
package esotericraft
package main.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import temportalist.origin.foundation.common.IProxy

/**
  * Created by TheTemportalist on 12/31/2015.
  */
class ProxyCommon extends IProxy {

	override def preInit(): Unit = {

		transmorigification.server.Server.preInit()

	}

	override def register(): Unit = {}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = null

	override def getServerElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = null

}
