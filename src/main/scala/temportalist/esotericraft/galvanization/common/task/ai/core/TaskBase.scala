package temportalist.esotericraft.galvanization.common.task.ai.core

import net.minecraft.entity.EntityCreature
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import temportalist.esotericraft.api.galvanize.ai.IGalvanizeTask
import temportalist.esotericraft.galvanization.common.task.ai.interfaces.{ITaskEntityMover, ITaskFlyCheck}

/**
  *
  * Created by TheTemportalist on 5/26/2016.
  *
  * @author TheTemportalist
  */
abstract class TaskBase(
		private val pos: BlockPos, private val face: EnumFacing
) extends IGalvanizeTask with ITaskFlyCheck with ITaskEntityMover {

	// ~~~~~ AI ~~~~~

	override def onSpawn(world: World): Unit = {}

	override def startExecuting(entity: EntityCreature): Unit = {
		this.checkCanFly(entity)
	}

	override def updateTask(entity: EntityCreature): Unit = {}

	override def resetTask(entity: EntityCreature): Unit = {}

	// ~~~~~ End ~~~~~

	def getPosition: BlockPos = this.pos

	def getFace: EnumFacing = this.face

	// ~~~~~ Other ~~~~~

	override def canEntityUse(world: World, stack: ItemStack): Boolean = false

	override def serializeNBT(): NBTTagCompound = new NBTTagCompound

	override def deserializeNBT(nbt: NBTTagCompound): Unit = {}

}
