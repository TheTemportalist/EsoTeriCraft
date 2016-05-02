package temportalist.esotericraft.sorcery.common.network

import net.minecraft.nbt.NBTTagCompound
import temportalist.esotericraft.api.network.PacketSyncEsotericPlayer
import temportalist.esotericraft.sorcery.common.SorceryPlayer
import temportalist.esotericraft.sorcery.common.network.SyncSorceryPlayerHandler.SyncData

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
class SyncSorceryPlayerHandler extends PacketSyncEsotericPlayer.Handler {

	override def getSyncData(entityID: Int, data: NBTTagCompound): Runnable = {
		new SyncData(entityID, data)
	}

}
object SyncSorceryPlayerHandler {
	class SyncData(eID: Int, data: NBTTagCompound)
			extends PacketSyncEsotericPlayer.SyncData(eID, data) {
		override def run(): Unit = {
			SorceryPlayer.get(this.getPlayer).onDataReceived(data)
		}
	}
}
