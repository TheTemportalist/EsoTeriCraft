package temportalist.esotericraft.galvanization.common.entity

import io.netty.buffer.ByteBuf
import net.minecraft.block.state.IBlockState
import net.minecraft.entity._
import net.minecraft.entity.ai.attributes.IAttribute
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{IInventory, ItemStackHelper}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.math.{BlockPos, MathHelper}
import net.minecraft.util.text.{ITextComponent, TextComponentString}
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import temportalist.esotericraft.api.galvanize.ability.IAbilityFly
import temportalist.esotericraft.galvanization.common.entity.emulator.{EntityState, IEntityEmulator}
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class EntityEmpty(world: World) extends EntityCreature(world)
		with IEntityAdditionalSpawnData with IEntityEmulator {

	def this(world: World, entityName: String, origin: Vect) {
		this(world)
		this.setPosition(origin.x, origin.y, origin.z)
		this.setEntityState(entityName, this.getEntityWorld)
	}

	override def getSelfEntityInstance: EntityLivingBase = this

	override protected def syncEntityNameToClient(name: String): Unit = {}

	override def entityInit(): Unit = {
		super.entityInit()
	}

	def getEntityStateInstance: EntityLivingBase = this.getEntityStateInstance(this.getEntityWorld)

	override def applyEntityAttributes(): Unit = {
		super.applyEntityAttributes()
		this.updateAttributes()
	}

	def updateAttributes(): Unit = {
		this.setAttribute(SharedMonsterAttributes.MAX_HEALTH, 10)
		this.setAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 0.25)
	}

	def setAttribute(attribute: IAttribute, default: Double): Unit = {
		this.getEntityAttribute(attribute).setBaseValue(
			this.getEntityStateInstance match {
				case entity: EntityLivingBase =>
					entity.getEntityAttribute(attribute).getBaseValue
				case _ => default
			}
		)
	}

	override def initEntityAI(): Unit = {
		if (this.getEntityState == null) return

		//val canFly = this.canFly

		//this.tasks.addTask(0, new EntityAIFollowPlayer(this, canFly = canFly))
		/*
		this.tasks.addTask(0, new EntityAIItemPickUp(
			this, this.origin, 4, 0.5, canFly = canFly
		))
		this.tasks.addTask(1, new EntityAIItemDeposit(this, this.origin.getDown(), 1, canFly = canFly))
		*/

	}

	override def onEntityConstructed(entity: EntityLivingBase): Unit = {
		this.updateAttributes()
		entity match {
			case e: EntityCreature =>
				e.tasks.taskEntries.clear()
				e.targetTasks.taskEntries.clear()

				this.tasks.taskEntries.clear()
				this.targetTasks.taskEntries.clear()
				this.initEntityAI()
			case _ =>
		}
	}

	final def canFly: Boolean = {
		if (this.getEntityState != null) {
			for (ability <- this._getEntityAbilities)
				if (ability.isInstanceOf[IAbilityFly]) return true
		}
		false
	}

	// ~~~~~ NBT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def writeEntityToNBT(nbt: NBTTagCompound): Unit = {

		if (this.getEntityName != null) nbt.setString("entity_name", this.getEntityName)

		if (this.getEntityState != null)
			nbt.setTag("entity_state", this.getEntityState.serializeNBT())

		nbt.setTag("emulator", this.serializeNBTEmulator)

	}

	override def readEntityFromNBT(nbt: NBTTagCompound): Unit = {

		if (nbt.hasKey("entity_name")) {
			this.setEntityState(nbt.getString("entity_name"), this.getEntityWorld)
		} else this.setEntityName(null)

		var entityState: EntityState = null
		if (nbt.hasKey("entity_state")) {
			entityState = new EntityState
			entityState.deserializeNBT(nbt.getCompoundTag("entity_state"))
		}

		this.deserializeNBTEmulator(nbt.getCompoundTag("emulator"))

		this.setEntityState(entityState)
	}

	override def writeSpawnData(buffer: ByteBuf): Unit = {

		val entityName = if (this.getEntityName != null) this.getEntityName else ""
		ByteBufUtils.writeUTF8String(buffer, entityName)

		val stateTag = if (this.getEntityState != null) this.getEntityState.serializeNBT() else new NBTTagCompound
		ByteBufUtils.writeTag(buffer, stateTag)

	}

	override def readSpawnData(buffer: ByteBuf): Unit = {

		val entityName = ByteBufUtils.readUTF8String(buffer)
		this.setEntityName(entityName)

		val entityStateTag = ByteBufUtils.readTag(buffer)
		if (!entityStateTag.hasNoTags) {
			val entityState = new EntityState()
			entityState.deserializeNBT(entityStateTag)
			this.setEntityState(entityState)
		}

	}

	// ~~~~~ Reflection of Model Entity ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def onUpdate(): Unit = {
		super.onUpdate()

		if (this.getEntityWorld.isRemote) this.onTickClient(this.getEntityWorld)
		else this.onTickServer(this.getEntityWorld)

	}

	override def isEntityUndead: Boolean = {
		this.getEntityStateInstance match {
			case entity: EntityLivingBase => entity.isEntityUndead
			case _ => false
		}
	}

	override def getYOffset: Double = {
		this.getEntityStateInstance match {
			case entity: EntityLivingBase => entity.getYOffset
			case _ => 0
		}
	}

	override def fall(distance: Float, damageMultiplier: Float): Unit = {
		if (!this.canFly) super.fall(distance, damageMultiplier)
	}

	override def updateFallState(y: Double, onGroundIn: Boolean,
			state: IBlockState, pos: BlockPos): Unit = {
		if (!this.canFly) super.updateFallState(y, onGroundIn, state, pos)
	}

	override def moveEntityWithHeading(strafe: Float, forward: Float): Unit = {
		if (!this.canFly) {
			super.moveEntityWithHeading(strafe, forward)
			return
		}

		if (this.isInWater) {
			this.moveRelative(strafe, forward, 0.02F)
			this.moveEntity(this.motionX, this.motionY, this.motionZ)
			this.motionX *= 0.800000011920929D
			this.motionY *= 0.800000011920929D
			this.motionZ *= 0.800000011920929D
		}
		else if (this.isInLava) {
			this.moveRelative(strafe, forward, 0.02F)
			this.moveEntity(this.motionX, this.motionY, this.motionZ)
			this.motionX *= 0.5D
			this.motionY *= 0.5D
			this.motionZ *= 0.5D
		}
		else {
			var f: Float = 0.91F
			if (this.onGround) {
				f = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX),
					MathHelper.floor_double(this.getEntityBoundingBox.minY) - 1,
					MathHelper.floor_double(this.posZ))).getBlock.slipperiness * 0.91F
			}
			val f1: Float = 0.16277136F / (f * f * f)
			this.moveRelative(strafe, forward, if (this.onGround) 0.1F * f1
			else 0.02F)
			f = 0.91F
			if (this.onGround) {
				f = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX),
					MathHelper.floor_double(this.getEntityBoundingBox.minY) - 1,
					MathHelper.floor_double(this.posZ))).getBlock.slipperiness * 0.91F
			}
			this.moveEntity(this.motionX, this.motionY, this.motionZ)
			this.motionX *= f.toDouble
			this.motionY *= f.toDouble
			this.motionZ *= f.toDouble
		}

		this.prevLimbSwingAmount = this.limbSwingAmount
		val d1: Double = this.posX - this.prevPosX
		val d0: Double = this.posZ - this.prevPosZ
		var f2: Float = MathHelper.sqrt_double(d1 * d1 + d0 * d0) * 4.0F

		if (f2 > 1.0F) {
			f2 = 1.0F
		}

		this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F
		this.limbSwing += this.limbSwingAmount

	}

	// ~~~~~ Inventory

	final def markInventoryDirty(): Unit = {

	}

}
object EntityEmpty {

	class Inventory(private val owner: EntityEmpty)
			extends IInventory with INBTSerializable[NBTTagList] {

		// ~~~~~ NBT

		override def markDirty(): Unit = owner.markInventoryDirty()

		override def serializeNBT(): NBTTagList = {
			val nbt = new NBTTagList

			for (i <- this.main.indices) {
				if (this.main(i) != null) {
					val tagSlot = new NBTTagCompound
					tagSlot.setByte("Slot", i.toByte)
					this.main(i).writeToNBT(tagSlot)
					nbt.appendTag(tagSlot)
				}
			}

			nbt
		}

		override def deserializeNBT(nbt: NBTTagList): Unit = {
			this.main = new Array[ItemStack](this.getSizeInventory)
			for (i <- 0 until nbt.tagCount()) {
				val tagSlot = nbt.getCompoundTagAt(i)
				val slot = tagSlot.getByte("Slot") & 255
				val stack = ItemStack.loadItemStackFromNBT(tagSlot)
				this.main(slot) = stack
			}
		}

		// ~~~~~ Naming

		override def getName: String = "EntityEmpty Inventory"

		override def getDisplayName: ITextComponent = new TextComponentString(this.getName)

		override def hasCustomName: Boolean = false

		// ~~~~~ Stack Data

		private var main = Array[ItemStack]()

		final def setSize(size: Int): Unit = {
			val newMain = new Array[ItemStack](size)
			var j = 0
			for (i <- this.main.indices)
				if (this.main(i) != null && j < newMain.length) {
					newMain(j) = this.main(i).copy()
					j += 1
				}
			this.main = newMain
		}

		override def getSizeInventory: Int = this.main.length

		override def getInventoryStackLimit: Int = 1

		override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = true

		override def setInventorySlotContents(index: Int, stack: ItemStack): Unit = {
			this.main(index) = stack
		}

		override def getStackInSlot(index: Int): ItemStack = this.main(index)

		override def removeStackFromSlot(index: Int): ItemStack = {
			if (this.main(index) == null) null
			else {
				val stack = this.main(index)
				this.main(index) = null
				stack
			}
		}

		override def decrStackSize(index: Int, count: Int): ItemStack = {
			if (this.main(index) != null) ItemStackHelper.getAndSplit(this.main, index, count)
			else null
		}

		override def clear(): Unit = {
			for (i <- this.main.indices) this.main(i) = null
		}

		// ~~~~~ Other

		override def isUseableByPlayer(player: EntityPlayer): Boolean = false

		override def closeInventory(player: EntityPlayer): Unit = {}

		override def openInventory(player: EntityPlayer): Unit = {}

		override def getFieldCount: Int = 0

		override def getField(id: Int): Int = 0

		override def setField(id: Int, value: Int): Unit = {}

	}

}
