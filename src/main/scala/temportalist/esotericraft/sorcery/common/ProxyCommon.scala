package temportalist.esotericraft.sorcery.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import temportalist.origin.foundation.common.IProxy

/**
  * Created by TheTemportalist on 5/6/2015.
  */
class ProxyCommon extends IProxy {

	override def register(): Unit = {}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = null

	override def getServerElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = null

}
