package temportalist.esotericraft.sorcery.common.capability

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import temportalist.esotericraft.api.sorcery.ApiSorcery.ISorceryPlayer
import temportalist.esotericraft.sorcery.common.Sorcery
import temportalist.origin.foundation.common.capability.IExtendedEntitySync
import temportalist.origin.foundation.common.network.NetworkMod

/**
  *
  * Created by TheTemportalist on 5/6/2016.
  *
  * @author TheTemportalist
  */
class SorceryPlayer(private val player: EntityPlayer)
		extends ISorceryPlayer with IExtendedEntitySync[NBTTagCompound, EntityPlayer] {

	override def getNetworkMod: NetworkMod = Sorcery

	override def serializeNBT(): NBTTagCompound = {
		new NBTTagCompound
	}

	override def deserializeNBT(nbt: NBTTagCompound): Unit = {

	}

}
