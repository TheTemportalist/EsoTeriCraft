package temportalist.esotericraft.galvanization.common.entity

import io.netty.buffer.ByteBuf
import net.minecraft.block.state.IBlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity._
import net.minecraft.entity.ai.EntityAISwimming
import net.minecraft.entity.ai.attributes.IAttribute
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.init.{Items, MobEffects, SoundEvents}
import net.minecraft.inventory.{IInventory, ItemStackHelper}
import net.minecraft.item.{ItemArrow, ItemAxe, ItemStack, ItemSword}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.util.math.{BlockPos, MathHelper}
import net.minecraft.util.text.{ITextComponent, TextComponentString}
import net.minecraft.util.{DamageSource, EnumFacing, EnumHand, EnumParticleTypes}
import net.minecraft.world.{World, WorldServer}
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import temportalist.esotericraft.api.galvanize.ability.IAbilityFly
import temportalist.esotericraft.galvanization.common.entity.emulator.{EntityState, IEntityEmulator}
import temportalist.esotericraft.galvanization.common.task.INBTCreator
import temportalist.origin.api.common.lib.Vect

import scala.collection.mutable.ListBuffer
import scala.collection.{JavaConversions, mutable}

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class EntityEmpty(world: World) extends EntityCreature(world)
		with IEntityAdditionalSpawnData with IEntityEmulator with INBTCreator {

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

		this.tasks.addTask(0, new EntityAISwimming(this))
		this.tasks.addTask(1, new EntityAITaskUpdater(this))

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

	// ~~~~~ Tasks ~~~~~

	private val taskPositions = mutable.Map[BlockPos, ListBuffer[EnumFacing]]()

	def addTask(pos: BlockPos, face: EnumFacing): Boolean = {
		if (!this.taskPositions.contains(pos))
			this.taskPositions(pos) = ListBuffer[EnumFacing]()
		if (!this.taskPositions(pos).contains(face)) {
			this.taskPositions(pos) += face
			true
		}
		else false
	}

	def removeTask(pos: BlockPos, face: EnumFacing): Boolean = {
		if (this.taskPositions.contains(pos) && this.taskPositions(pos).contains(face)) {
			val i = this.taskPositions(pos).indexOf(face)
			this.taskPositions(pos).remove(i)
			if (this.taskPositions(pos).isEmpty) this.taskPositions.remove(pos)
			true
		}
		else false
	}

	def getTaskPositionsAsSeq: Seq[(BlockPos, EnumFacing)] = {
		val ret = ListBuffer[(BlockPos, EnumFacing)]()
		for (entry <- this.taskPositions) for (face <- entry._2) ret += ((entry._1, face))
		ret
	}

	override def processInteract(player: EntityPlayer, hand: EnumHand,
			stack: ItemStack): Boolean = {
		if (stack != null && (stack.getItem.isItemTool(stack) || stack.getItem.isInstanceOf[ItemArrow])) {
			var thisHand: EnumHand = null
			if (this.getHeldItem(EnumHand.MAIN_HAND) == null)
				thisHand = EnumHand.MAIN_HAND
			else if (this.getHeldItem(EnumHand.OFF_HAND) == null) thisHand = EnumHand.OFF_HAND
			if (thisHand != null) {
				this.setHeldItem(thisHand, stack.copy())
				player.setHeldItem(hand, null)
				return true
			}
		}
		false
	}

	override def attackEntity(target: EntityLivingBase): Unit = {
		val inst = this.getEntityStateInstance

		var f = inst.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue
				.toFloat
		var f1 = EnchantmentHelper
				.getModifierForCreature(this.getHeldItemMainhand, target.getCreatureAttribute)

		if (f > 0F || f1 > 0F) {
			val fullAttackPower = true
			val sprintingHit = false
			var fallingHit = false
			var isSword = false
			val knockbackModifier = EnchantmentHelper.getKnockbackModifier(this)

			// spinting hit stuff

			fallingHit = fullAttackPower && this.fallDistance > 0F && !this.onGround &&
					!this.isOnLadder && !this.isInWater &&
					!this.isPotionActive(MobEffects.BLINDNESS) && !this.isRiding

			if (fallingHit) f *= 1.5F

			f += f1
			val d0 = this.distanceWalkedModified - this.prevDistanceWalkedModified

			if (fullAttackPower && !sprintingHit && !fallingHit &&
					this.onGround && d0 < inst.getAIMoveSpeed) {
				val held = this.getHeldItemMainhand
				if (held != null && held.getItem.isInstanceOf[ItemSword])
					isSword = true
			}

			var f4 = 0F
			var didSetFire = false
			val fireModifier = EnchantmentHelper.getFireAspectModifier(this)

			f4 = target.getHealth
			if (fireModifier > 0 && !target.isBurning) {
				didSetFire = true
				target.setFire(1)
			}

			val mX = target.motionX
			val mY = target.motionY
			val mZ = target.motionZ
			this.swingArm(EnumHand.MAIN_HAND)
			val flag5 = target.attackEntityFrom(DamageSource.causeMobDamage(this), f)

			if (flag5) {

				if (knockbackModifier > 0) {
					target.knockBack(this, knockbackModifier * 0.5F,
						MathHelper.sin(this.rotationYaw * 0.017453292F).toDouble,
						(-MathHelper.cos(this.rotationYaw * 0.017453292F)).toDouble
					)
					this.motionX *= 0.6D
					this.motionZ *= 0.6D
				}

				if (isSword) {
					val entList = JavaConversions.asScalaBuffer(
						this.getEntityWorld.getEntitiesWithinAABB(classOf[EntityLivingBase],
							target.getEntityBoundingBox.expand(1.0D, 0.25D, 1.0D))
					)
					for (ent <- entList) {
						if (ent != this && ent != target &&
								!this.isOnSameTeam(ent) &&
								this.getDistanceSqToEntity(ent) < 9.0D) {
							ent.knockBack(this, 0.4F,
								MathHelper.sin(this.rotationYaw * 0.017453292F).toDouble,
								(-MathHelper.cos(this.rotationYaw * 0.017453292F)).toDouble
							)
							ent.attackEntityFrom(DamageSource.causeMobDamage(this), 1.0F)
						}
					}
					this.getEntityWorld
							.playSound(null.asInstanceOf[EntityPlayer], this.posX, this.posY,
								this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
								this.getSoundCategory,
								1.0F, 1.0F)

				}

				target match {
					case player: EntityPlayerMP =>
						if (player.velocityChanged) {
							player.connection.sendPacket(new SPacketEntityVelocity(player))
							player.velocityChanged = true
							player.motionX = mX
							player.motionY = mY
							player.motionZ = mZ
						}
					case _ =>
				}

				if (isSword) {
					this.getEntityWorld.playSound(null.asInstanceOf[EntityPlayer],
						this.posX, this.posY, this.posZ,
						SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory, 1.0F, 1.0F)
				}

				if (!fallingHit && !isSword) {
					if (fullAttackPower) {
						this.getEntityWorld.playSound(null.asInstanceOf[EntityPlayer],
							this.posX, this.posY, this.posZ,
							SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
							this.getSoundCategory, 1.0F, 1.0F)
					}
					else {
						this.getEntityWorld.playSound(null.asInstanceOf[EntityPlayer],
							this.posX, this.posY, this.posZ,
							SoundEvents.ENTITY_PLAYER_ATTACK_WEAK,
							this.getSoundCategory, 1.0F, 1.0F)
					}
				}

				if (!this.getEntityWorld.isRemote) target match {
					case player: EntityPlayer =>
						val thisHeldMain: ItemStack = this.getHeldItemMainhand
						val playerActive: ItemStack =
							if (player.isHandActive) player.getActiveItemStack
							else null

						if (thisHeldMain != null && playerActive != null &&
								thisHeldMain.getItem.isInstanceOf[ItemAxe] &&
								playerActive.getItem == Items.SHIELD) {
							var f3: Float = 0.25F + EnchantmentHelper.getEfficiencyModifier(this).toFloat * 0.05F
							if (sprintingHit) {
								f3 += 0.75F
							}
							if (this.rand.nextFloat < f3) {
								player.getCooldownTracker.setCooldown(Items.SHIELD, 100)
								this.worldObj.setEntityState(player, 30.toByte)
							}
						}
					case _ =>
				}

				this.setLastAttacker(target)

				EnchantmentHelper.applyThornEnchantments(target, this)

				EnchantmentHelper.applyArthropodEnchantments(this, target)
				val thisHeldMain: ItemStack = this.getHeldItemMainhand
				var entity: Entity = target

				/*
				target match {
					case part: EntityDragonPart =>
						part.entityDragonObj match {
							case livingPart: EntityLivingBase =>
								entity = livingPart
							case _ =>
						}
					case _ =>
				}
				*/
				/*
				if (thisHeldMain != null && entity.isInstanceOf[EntityLivingBase]) {
					thisHeldMain.hitEntity(entity.asInstanceOf[EntityLivingBase], this)
					if (thisHeldMain.stackSize <= 0) {
						this.setHeldItem(EnumHand.MAIN_HAND, null.asInstanceOf[ItemStack])
						net.minecraftforge.event.ForgeEventFactory
								.onPlayerDestroyItem(this, thisHeldMain, EnumHand.MAIN_HAND)
					}
				}
				*/

				val f5: Float = f4 - target.getHealth
				if (fireModifier > 0) target.setFire(fireModifier * 4)
				if (f5 > 2.0F) {
					val k: Int = (f5.toDouble * 0.5D).toInt
					this.getEntityWorld match {
						case worldServer: WorldServer =>
							worldServer.spawnParticle(
								EnumParticleTypes.DAMAGE_INDICATOR,
								target.posX,
								target.posY + (target.height * 0.5F).toDouble,
								target.posZ,
								k, 0.1D, 0.0D, 0.1D, 0.2D
							)
						case _ =>
					}
				}

			}
			else {
				this.getEntityWorld.playSound(null.asInstanceOf[EntityPlayer],
					this.posX, this.posY, this.posZ,
					SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE,
					this.getSoundCategory, 1.0F, 1.0F)
				if (didSetFire) target.extinguish()
			}
		}

	}

	// ~~~~~ NBT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def writeEntityToNBT(nbt: NBTTagCompound): Unit = {
		super.writeEntityToNBT(nbt)

		if (this.getEntityName != null) nbt.setString("entity_name", this.getEntityName)

		if (this.getEntityState != null)
			nbt.setTag("entity_state", this.getEntityState.serializeNBT())

		nbt.setTag("emulator", this.serializeNBTEmulator)

		val tagTasks = new NBTTagList
		for (posFace <- this.getTaskPositionsAsSeq) {
			val tag = new NBTTagCompound
			tag.setTag("pos", new Vect(posFace._1).serializeNBT())
			tag.setInteger("face", posFace._2.ordinal())
			tagTasks.appendTag(tag)
		}
		if (!tagTasks.hasNoTags)
			nbt.setTag("tasks", tagTasks)

	}

	override def readEntityFromNBT(nbt: NBTTagCompound): Unit = {
		super.readEntityFromNBT(nbt)

		if (nbt.hasKey("entity_name")) {
			this.setEntityState(nbt.getString("entity_name"), this.getEntityWorld)
		}
		else this.setEntityName(null)

		var entityState: EntityState = null
		if (nbt.hasKey("entity_state")) {
			entityState = new EntityState
			entityState.deserializeNBT(nbt.getCompoundTag("entity_state"))
		}

		this.deserializeNBTEmulator(nbt.getCompoundTag("emulator"))

		this.setEntityState(entityState)

		if (nbt.hasKey("tasks")) {
			val tagTasks = this.getTagList[NBTTagCompound](nbt, "tasks")
			this.taskPositions.clear()
			for (tag <- this.getTagListAsIterable[NBTTagCompound](tagTasks)) {
				val pos = Vect.readFrom(tag, "pos").toBlockPos
				val face = EnumFacing.values()(tag.getInteger("face"))
				this.addTask(pos, face)
			}
		}

	}

	override def writeSpawnData(buffer: ByteBuf): Unit = {

		val entityName = if (this.getEntityName != null) this.getEntityName else ""
		ByteBufUtils.writeUTF8String(buffer, entityName)

		val stateTag = if (this.getEntityState != null) this.getEntityState.serializeNBT()
		else new NBTTagCompound
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

	override def getEyeHeight: Float = {
		this.getEntityStateInstance match {
			case living: EntityLivingBase => living.getEyeHeight
			case _ => super.getEyeHeight
		}
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
