package temportalist.esotericraft.galvanization.common.capability

import javax.annotation.Nullable

import net.minecraft.client.Minecraft
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.entity.{EntityList, EntityLivingBase}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.nbt.{NBTTagCompound, NBTTagString}
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.galvanization.client.{EntityModel, ModelHandler}
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.capability.entity.{EntityState, EntityType}
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
		extends IPlayerGalvanize with IExtendedEntitySync[NBTTagCompound, EntityPlayer] {

	override def getNetworkMod: NetworkMod = Galvanize

	private var entityName: String = null
	private var entityState: EntityState = _
	@SideOnly(Side.CLIENT)
	private var entityModel: EntityModel[_ <: EntityLivingBase, _ <: EntityLivingBase] = _

	override def setEntityState(entityName: String, world: World): Unit = {
		this.entityName = entityName

		if (world != null) {
			if (!world.isRemote)
				this.sendNBTToClient(this.player, new NBTTagString(this.entityName))
			else if (this.entityModel != null) this.entityModel = null
			this.constructEntityState(world)
		}
	}

	private def constructEntityState(world: World): Unit = {
		EntityList.createEntityByName(this.entityName, world) match {
			case living: EntityLivingBase => this.setEntityState(living)
			case _ =>
		}
	}

	override def setEntityState(entity: EntityLivingBase): Unit = {
		if (entity == null) this.entityState = null
		else this.entityState = new EntityState(EntityType.create(entity))
	}

	override def getEntityState: EntityState = this.entityState

	override def clearEntityState(): Unit = this.entityState = null

	override def serializeNBT(): NBTTagCompound = {
		val nbt = new NBTTagCompound

		if (this.entityState != null) nbt.setTag("entity_state", this.entityState.serializeNBT())

		nbt
	}

	override def deserializeNBT(nbt: NBTTagCompound): Unit = {

		if (nbt.hasKey("main")) {
			nbt.getTag("main") match {
				case tagStr: NBTTagString =>
					this.setEntityState(tagStr.getString, this.getWorld)
				case tagCom: NBTTagCompound =>
					this.deserializeNBT(tagCom)
				case _ =>
			}
			return
		}

		if (nbt.hasKey("entityName")) {
			this.setEntityState(nbt.getString("entityName"), this.getWorld)
		} else this.entityName = null

		if (nbt.hasKey("entity_state")) {
			this.entityState = new EntityState
			this.entityState.deserializeNBT(nbt.getCompoundTag("entity_state"))
		} else this.entityState = null

	}

	def getWorld: World = this.player.getEntityWorld

	def getEntityStateInstance: EntityLivingBase = {
		if (this.entityState != null) this.entityState.getInstance(this.getWorld)
		else null
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	def getEntityModelInstance(world: World): EntityModel[_ <: EntityLivingBase, _ <: EntityLivingBase] = {
		if (this.entityModel == null)
			this.entityModel = ModelHandler.getEntityModel(this.entityState.getInstance(world))
		this.entityModel
	}

	@SideOnly(Side.CLIENT)
	override def onTickClient(): Unit = {

		if (this.entityState != null) {
			this.entityState.onUpdate()
			this.getEntityStateInstance match {
				case living: EntityLivingBase =>
					this.setSizeAndEye(living.width, living.height, living.getEyeHeight)
				case _ => // null
			}
			this.syncEntityWithSelf(this.getEntityStateInstance)
		}

	}

	override def onTickServer(): Unit = {

		if (this.entityState != null) {
			this.entityState.onUpdate()
			this.getEntityStateInstance match {
				case living: EntityLivingBase =>
					this.setSizeAndEye(living.width, living.height, living.getEyeHeight)
				case _ => // null
			}
		}

	}

	@SideOnly(Side.CLIENT)
	def syncEntityWithSelf(ent: EntityLivingBase): Unit = {

		//prevs
		ent.prevRotationYawHead = player.prevRotationYawHead
		ent.prevRotationYaw = player.prevRotationYaw
		ent.prevRotationPitch = player.prevRotationPitch
		ent.prevRenderYawOffset = player.prevRenderYawOffset
		ent.prevLimbSwingAmount = player.prevLimbSwingAmount
		ent.prevSwingProgress = player.prevSwingProgress
		ent.prevPosX = player.prevPosX
		ent.prevPosY = player.prevPosY
		ent.prevPosZ = player.prevPosZ

		//currents
		ent.rotationYawHead = player.rotationYawHead
		ent.rotationYaw = player.rotationYaw
		ent.rotationPitch = player.rotationPitch
		ent.renderYawOffset = player.renderYawOffset
		ent.limbSwingAmount = player.limbSwingAmount
		ent.limbSwing = player.limbSwing
		ent.posX = player.posX
		ent.posY = player.posY
		ent.posZ = player.posZ
		ent.motionX = player.motionX
		ent.motionY = player.motionY
		ent.motionZ = player.motionZ
		ent.ticksExisted = player.ticksExisted
		ent.isAirBorne = player.isAirBorne
		ent.moveStrafing = player.moveStrafing
		ent.moveForward = player.moveForward
		ent.dimension = player.dimension
		ent.worldObj = player.worldObj
		// ent.ridingEntity = player.ridingEntity
		ent.hurtTime = player.hurtTime
		ent.deathTime = player.deathTime
		ent.isSwingInProgress = player.isSwingInProgress
		ent.swingProgress = player.swingProgress
		ent.swingProgressInt = player.swingProgressInt

		val prevOnGround: Boolean = ent.onGround
		ent.onGround = player.onGround

		val mc = Minecraft.getMinecraft

		if (this.player != mc.thePlayer) {
			ent.noClip = false
			ent.setEntityBoundingBox(player.getEntityBoundingBox)
			ent.moveEntity(0.0D, -0.01D, 0.0D)
			ent.posY = player.posY
		}
		ent.noClip = player.noClip

		ent.setSneaking(player.isSneaking)
		ent.setSprinting(player.isSprinting)
		ent.setInvisible(player.isInvisible)
		ent.setHealth(ent.getMaxHealth * (player.getHealth / player.getMaxHealth))

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
				dragon.deathTicks = player.deathTime
			case _ =>
		}

		for (i <- EntityEquipmentSlot.values()) {
			if (ent.getItemStackFromSlot(i) == null && player.getItemStackFromSlot(i) != null ||
					ent.getItemStackFromSlot(i) != null && player.getItemStackFromSlot(i) == null ||
					ent.getItemStackFromSlot(i) != null && player.getItemStackFromSlot(i) != null &&
							!ent.getItemStackFromSlot(i).isItemEqual(player.getItemStackFromSlot(i))) {
				ent.setItemStackToSlot(i,
					if (player.getItemStackFromSlot(i) != null) player.getItemStackFromSlot(i).copy
					else null)
			}
		}

		ent match {
			case entPlayer: EntityPlayer =>
				for (hand <- EnumHand.values()) {
					if (entPlayer.getHeldItem(hand) != this.player.getHeldItem(hand))
						entPlayer.setHeldItem(hand, if (this.player.getHeldItem(hand) == null) null else this.player.getHeldItem(hand).copy())
				}
			case _ =>
		}

	}

	private def setSizeAndEye(width: Float, height: Float, eyeHeight: Float): Unit = {
		if (this.player != null) {

			player.eyeHeight = eyeHeight

			// Start; setSize because it protected
			if (width != player.width || height != player.height) {
				val currentWidth = player.width
				player.width = width
				player.height = height
				val aabb = player.getEntityBoundingBox
				player.setEntityBoundingBox(new AxisAlignedBB(
					aabb.minX, aabb.minY, aabb.minZ,
					aabb.minX + width,
					aabb.minY + height,
					aabb.minZ + width
				))
				if (width > currentWidth && !this.player.getEntityWorld.isRemote) {
					player.moveEntity(currentWidth - width, 0, currentWidth - width)
				}
			}
			// End

		}
	}

}
