package temportalist.esotericraft.galvanization.common.entity.emulator

import javax.annotation.Nullable

import net.minecraft.client.Minecraft
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{EntityList, EntityLivingBase}
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.client.{EntityModel, ModelHandler}

/**
  *
  * Created by TheTemportalist on 5/17/2016.
  *
  * @author TheTemportalist
  */
trait IEntityEmulator {

	private var entityName: String = null
	private var entityState: EntityState = _
	@SideOnly(Side.CLIENT)
	private var entityModel: EntityModel[_ <: EntityLivingBase, _ <: EntityLivingBase] = _

	// ~~~~~~~~~~ Getters ~~~~~~~~~~

	final def getEntityName: String = this.entityName

	final def getEntityState: EntityState = this.entityState

	final def getEntityStateInstance(world: World): EntityLivingBase = {
		if (this.entityState != null) this.entityState.getInstance(world)
		else null
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	final def getEntityModelInstance(world: World): EntityModel[_ <: EntityLivingBase, _ <: EntityLivingBase] = {
		this.getEntityState match {
			case entityState: EntityState =>
				if (this.entityModel == null)
					this.entityModel = ModelHandler.getEntityModel(entityState.getInstance(world))
				this.entityModel
			case _ => null
		}
	}

	// ~~~~~~~~~~ Setters ~~~~~~~~~~

	final def setEntityName(entityName: String): Unit = {
		this.entityName = entityName
	}

	final def setEntityState(entityName: String, world: World): Unit = {
		this.setEntityName(entityName)

		if (world != null) {
			if (!world.isRemote) this.syncEntityNameToClient(this.entityName)
			else if (this.entityModel != null) this.entityModel = null
			this.constructEntityState(world)
		}
	}

	private final def constructEntityState(world: World): Unit = {
		EntityList.createEntityByName(this.entityName, world) match {
			case living: EntityLivingBase =>
				this.setEntityStateEntity(living)
				this.onEntityConstructed(this.getEntityStateInstance(world))
			case _ =>
		}
	}

	def onEntityConstructed(entity: EntityLivingBase): Unit = {}

	final def setEntityStateEntity(entity: EntityLivingBase): Unit = {
		if (entity == null) this.entityState = null
		else this.entityState = new EntityState(EntityType.create(entity))
	}

	final def setEntityState(state: EntityState): Unit = {
		this.entityState = state
		this.entityModel = null
	}

	final def clearEntityState(): Unit = this.entityState = null

	// ~~~~~~~~~~ Syncing ~~~~~~~~~~

	protected def syncEntityNameToClient(name: String): Unit

	// ~~~~~~~~~~ Ticking ~~~~~~~~~~

	@SideOnly(Side.CLIENT)
	final def onTickClient(world: World): Unit = {
		if (this.entityState != null) {
			this.entityState.onUpdate()
			val entInstance = this.getEntityStateInstance(world)
			val self = this.getSelfEntityInstance
			entInstance match {
				case living: EntityLivingBase =>
					this.setSizeAndEye(living.width, living.height, living.getEyeHeight, self)
				case _ => // null
			}
			this.syncEntityWithSelf(entInstance, self)
		}
	}

	final def onTickServer(world: World): Unit = {

		if (this.entityState != null) {
			this.entityState.onUpdate()
			val entInstance = this.getEntityStateInstance(world)
			val self = this.getSelfEntityInstance
			entInstance match {
				case living: EntityLivingBase =>
					this.setSizeAndEye(living.width, living.height, living.getEyeHeight, self)
				case _ => // null
			}
		}

	}

	def getSelfEntityInstance: EntityLivingBase

	// ~~~~~~~~~~ Entity data matching ~~~~~~~~~~

	@SideOnly(Side.CLIENT)
	final def syncEntityWithSelf(ent: EntityLivingBase, self: EntityLivingBase): Unit = {

		//prevs
		ent.prevRotationYawHead = self.prevRotationYawHead
		ent.prevRotationYaw = self.prevRotationYaw
		ent.prevRotationPitch = self.prevRotationPitch
		ent.prevRenderYawOffset = self.prevRenderYawOffset
		ent.prevLimbSwingAmount = self.prevLimbSwingAmount
		ent.prevSwingProgress = self.prevSwingProgress
		ent.prevPosX = self.prevPosX
		ent.prevPosY = self.prevPosY
		ent.prevPosZ = self.prevPosZ

		//currents
		ent.rotationYawHead = self.rotationYawHead
		ent.rotationYaw = self.rotationYaw
		ent.rotationPitch = self.rotationPitch
		ent.renderYawOffset = self.renderYawOffset
		ent.limbSwingAmount = self.limbSwingAmount
		ent.limbSwing = self.limbSwing
		ent.posX = self.posX
		ent.posY = self.posY
		ent.posZ = self.posZ
		ent.motionX = self.motionX
		ent.motionY = self.motionY
		ent.motionZ = self.motionZ
		ent.ticksExisted = self.ticksExisted
		ent.isAirBorne = self.isAirBorne
		ent.moveStrafing = self.moveStrafing
		ent.moveForward = self.moveForward
		ent.dimension = self.dimension
		ent.worldObj = self.worldObj
		// ent.ridingEntity = self.ridingEntity
		ent.hurtTime = self.hurtTime
		ent.deathTime = self.deathTime
		ent.isSwingInProgress = self.isSwingInProgress
		ent.swingProgress = self.swingProgress
		ent.swingProgressInt = self.swingProgressInt

		val prevOnGround: Boolean = ent.onGround
		ent.onGround = self.onGround

		val mc = Minecraft.getMinecraft

		if (self != mc.thePlayer) {
			ent.noClip = false
			ent.setEntityBoundingBox(self.getEntityBoundingBox)
			ent.moveEntity(0.0D, -0.01D, 0.0D)
			ent.posY = self.posY
		}
		ent.noClip = self.noClip

		ent.setSneaking(self.isSneaking)
		ent.setSprinting(self.isSprinting)
		ent.setInvisible(self.isInvisible)
		ent.setHealth(ent.getMaxHealth * (self.getHealth / self.getMaxHealth))

		if (prevOnGround && !ent.onGround) {
			ent match {
				case slime: EntitySlime => slime.squishAmount = 0.6F
				case _ =>
			}
		}

		ent match {
			case dragon: EntityDragon =>
				dragon.prevRotationYaw += 180F
				dragon.rotationYaw += 180F
				dragon.deathTicks = self.deathTime
			case _ =>
		}

		for (i <- EntityEquipmentSlot.values()) {
			if (ent.getItemStackFromSlot(i) == null && self.getItemStackFromSlot(i) != null ||
					ent.getItemStackFromSlot(i) != null && self.getItemStackFromSlot(i) == null ||
					ent.getItemStackFromSlot(i) != null && self.getItemStackFromSlot(i) != null &&
							!ent.getItemStackFromSlot(i).isItemEqual(self.getItemStackFromSlot(i))) {
				ent.setItemStackToSlot(i,
					if (self.getItemStackFromSlot(i) != null) self.getItemStackFromSlot(i).copy
					else null)
			}
		}

		ent match {
			case entPlayer: EntityPlayer =>
				for (hand <- EnumHand.values()) {
					if (entPlayer.getHeldItem(hand) != self.getHeldItem(hand))
						entPlayer.setHeldItem(hand,
							if (self.getHeldItem(hand) == null) null else self.getHeldItem(hand).copy())
				}
			case _ =>
		}

	}

	private final def setSizeAndEye(width: Float, height: Float, eyeHeight: Float, self: EntityLivingBase): Unit = {
		if (self != null) {

			self match {
				case player: EntityPlayer =>
					player.eyeHeight = eyeHeight
				case _ =>
			}


			// Start; setSize because it protected
			if (width != self.width || height != self.height) {
				val currentWidth = self.width
				self.width = width
				self.height = height
				val aabb = self.getEntityBoundingBox
				self.setEntityBoundingBox(new AxisAlignedBB(
					aabb.minX, aabb.minY, aabb.minZ,
					aabb.minX + width,
					aabb.minY + height,
					aabb.minZ + width
				))
				if (width > currentWidth && !self.getEntityWorld.isRemote) {
					self.moveEntity(currentWidth - width, 0, currentWidth - width)
				}
			}
			// End

		}
	}

}
