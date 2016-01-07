package com.temportalist.esotericraft.common.tile

import com.temportalist.esotericraft.api.ApiEsotericraft
import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.foundation.common.tile.TEBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{AxisAlignedBB, BlockPos, ITickable}

/**
  * Created by TheTemportalist on 1/2/2016.
  */
class TENexusCrystal extends TEBase with ITickable {

	override def update(): Unit = {

	}

	// ~~~~~~~~~~~ Create the area bounding box ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def setPos(posIn: BlockPos): Unit = {
		super.setPos(posIn)
		this.setBoundingBox(posIn)
	}

	private var boundingBox: AxisAlignedBB = null

	def setBoundingBox(pos: BlockPos): Unit = {
		val posVec = new V3O(pos)
		val center = posVec + V3O.CENTER.suppressedYAxis()
		val radius = 2.5
		val height = 3.0
		val min = center - new V3O(radius, height, radius)
		val max = center + new V3O(radius, 0, radius)
		this.boundingBox = V3O.toAABB(min, max)
		this.markDirty()
	}

	def hasBoundingBox: Boolean = this.boundingBox != null

	def getBoundingBox: AxisAlignedBB = this.boundingBox

	// ~~~~~~~~~~~ NBT Saving ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def writeToNBT(tagCom: NBTTagCompound): Unit = {
		super.writeToNBT(tagCom)
	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		super.readFromNBT(tagCom)
	}

	// ~~~~~~~~~~~ Effect handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def tryDoEffect(player: EntityPlayer): Boolean = {
		if (this.isPlayerUnder(player)) {
			val module = ApiEsotericraft.getModuleForTrigger(player.getCurrentEquippedItem)

		}
		false
	}

	private def isPlayerUnder(player: EntityPlayer): Boolean = {
		this.hasBoundingBox && this.getBoundingBox.isVecInside(player.getPositionVector)
	}

}
