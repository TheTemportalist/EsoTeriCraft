package temportalist.esotericraft.galvanization.common.entity

import java.lang.Iterable
import java.util

import net.minecraft.entity.ai.attributes.IAttribute
import net.minecraft.entity.{EntityList, EntityLivingBase, SharedMonsterAttributes}
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHandSide
import net.minecraft.world.World

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class EntityEmpty(world: World, private var modelEntityID: String = null)
		extends EntityLivingBase(world) {

	private var modelEntity: EntityLivingBase = _

	private final def createModelEntityByName(): Unit = {
		EntityList.createEntityByName(this.modelEntityID, this.getEntityWorld) match {
			case e: EntityLivingBase =>
				this.modelEntity = e
				this.setSize(e.width, e.height)
			case _ =>
		}
	}

	override def entityInit(): Unit = {
		super.entityInit()
		if (this.modelEntityID != null) this.createModelEntityByName()
	}

	override def applyEntityAttributes(): Unit = {
		super.applyEntityAttributes()
		this.updateAttributes()
	}

	def updateAttributes(): Unit = {
		this.setAttribute(SharedMonsterAttributes.MAX_HEALTH, 10)
	}

	def setAttribute(attribute: IAttribute, default: Double): Unit = {
		this.getEntityAttribute(attribute).setBaseValue(
			if (this.modelEntity != null) this.modelEntity.getEntityAttribute(attribute).getBaseValue
			else default
		)
	}

	final def getModelEntity: EntityLivingBase = this.modelEntity

	override def writeEntityToNBT(compound: NBTTagCompound): Unit = {
		compound.setString("modelEntityID", this.modelEntityID)

	}

	override def readEntityFromNBT(compound: NBTTagCompound): Unit = {
		this.modelEntityID = compound.getString("modelEntityID")
		this.createModelEntityByName()
		this.updateAttributes()

	}

	// ~~~~~ Reflection of Model Entity ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getPrimaryHand: EnumHandSide =
		if (this.modelEntity == null) EnumHandSide.RIGHT else this.modelEntity.getPrimaryHand

	override def getArmorInventoryList: Iterable[ItemStack] = new util.ArrayList[ItemStack]()

	def getItemStackFromSlot(slotIn: EntityEquipmentSlot): ItemStack = null

	override def setItemStackToSlot(slotIn: EntityEquipmentSlot, stack: ItemStack): Unit = {}

	override def isEntityUndead: Boolean = this.modelEntity != null && this.modelEntity.isEntityUndead

	override def getYOffset: Double = if (this.modelEntity == null) 0 else this.modelEntity.getYOffset
}
