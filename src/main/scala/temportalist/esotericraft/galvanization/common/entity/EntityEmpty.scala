package temportalist.esotericraft.galvanization.common.entity

import io.netty.buffer.ByteBuf
import net.minecraft.block.state.IBlockState
import net.minecraft.entity._
import net.minecraft.entity.ai.attributes.IAttribute
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.{BlockPos, MathHelper}
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import temportalist.esotericraft.api.galvanize.ability.IAbilityFly
import temportalist.esotericraft.galvanization.common.entity.ai.EntityAIFollowPlayer
import temportalist.esotericraft.galvanization.common.entity.emulator.{EntityState, IEntityEmulator}

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class EntityEmpty(world: World) extends EntityCreature(world)
		with IEntityAdditionalSpawnData with IEntityEmulator {

	def this(world: World, entityName: String) {
		this(world)

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

		val canFly = this.canFly
		//this.moveHelper = if (canFly) new EntityMoveHelperFly(this) else new EntityMoveHelper(this)

		this.tasks.addTask(0, new EntityAIFollowPlayer(this, canFly = canFly))

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

	private def canFly: Boolean = {
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
		this.setEntityState(entityState)

		this.deserializeNBTEmulator(nbt.getCompoundTag("emulator"))

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
}
