package com.temportalist.esotericenhancing.client

import com.temportalist.esotericenhancing.common.ProxyCommon
import com.temportalist.esotericraft.common.EsoTeriCraft
import com.temportalist.esotericraft.common.init.ModBlocks
import com.temportalist.origin.api.common.resource.{IModResource, EnumResource, IModDetails}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.obj.OBJLoader

/**
  * Created by TheTemportalist on 12/31/2015.
  */
class ProxyClient extends ProxyCommon {

	override def register(): Unit = {}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {

		null
	}

}
