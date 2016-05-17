package temportalist.esotericraft.galvanization.common.capability

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.{NBTTagCompound, NBTTagString}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.entity.emulator.{IEntityEmulator, EntityState}
import temportalist.origin.foundation.common.capability.IExtendedEntitySync
import temportalist.origin.foundation.common.network.NetworkMod

/**
  *
  * Based HEAVILY on [[https://github.com/iChun/Morph/blob/master/src/main/java/morph/common/morph/MorphInfo.java]]
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
class PlayerGalvanize(private val player: EntityPlayer)
		extends IPlayerGalvanize with IExtendedEntitySync[NBTTagCompound, EntityPlayer] with IEntityEmulator {

	override def getNetworkMod: NetworkMod = Galvanize

	override def serializeNBT(): NBTTagCompound = {
		val nbt = new NBTTagCompound

		if (this.getEntityState != null) nbt.setTag("entity_state", this.getEntityState.serializeNBT())

		nbt
	}

	override def deserializeNBT(nbt: NBTTagCompound): Unit = {

		if (nbt.hasKey("main")) {
			nbt.getTag("main") match {
				case tagStr: NBTTagString =>
					this.setEntityStateEntity(tagStr.getString, this.getWorld)
				case tagCom: NBTTagCompound =>
					this.deserializeNBT(tagCom)
				case _ =>
			}
			return
		}

		if (nbt.hasKey("entityName")) {
			this.setEntityStateEntity(nbt.getString("entityName"), this.getWorld)
		} else this.setEntityName(null)

		var entityState: EntityState = null
		if (nbt.hasKey("entity_state")) {
			entityState = new EntityState
			entityState.deserializeNBT(nbt.getCompoundTag("entity_state"))
		}
		this.setEntityState(entityState)

	}

	def getWorld: World = this.player.getEntityWorld

	@SideOnly(Side.CLIENT)
	override def onTickClient(): Unit = this.onTickClient(this.getWorld)

	override def onTickServer(): Unit = this.onTickServer(this.getWorld)

	override def getSelfEntityInstance: EntityLivingBase = this.player

	override protected def syncEntityNameToClient(name: String): Unit = {
		this.sendNBTToClient(this.player, new NBTTagString(name))
	}

}
