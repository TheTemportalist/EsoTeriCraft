package temportalist.esotericraft.galvanization.common.entity

import io.netty.buffer.ByteBuf
import net.minecraft.entity._
import net.minecraft.entity.ai.{EntityAINearestAttackableTarget, EntityAIWander}
import net.minecraft.entity.ai.attributes.IAttribute
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import temportalist.esotericraft.galvanization.common.entity.ai.EntityAIPlayer

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class EntityEmpty(world: World) extends EntityCreature(world) with IEntityAdditionalSpawnData {

	private var modelEntityID: String = null
	private var modelEntity: EntityLivingBase = _

	def this(world: World, modelEntityID: String) {
		this(world)
		this.modelEntityID = modelEntityID
	}

	private final def createModelEntityByName(): Unit = {
		if (this.modelEntityID == null) return
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
		this.setAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 0.25)
	}

	def setAttribute(attribute: IAttribute, default: Double): Unit = {
		this.getEntityAttribute(attribute).setBaseValue(
			if (this.modelEntity != null) this.modelEntity.getEntityAttribute(attribute).getBaseValue
			else default
		)
	}

	override def initEntityAI(): Unit = {
		//this.tasks.addTask(1, new EntityAIPlayer(this))
		this.tasks.addTask(2, new EntityAIWander(this, 0.6D))

	}

	final def setModelEntity(id: String): Unit = {
		this.modelEntityID = id
		this.createModelEntityByName()
		this.updateAttributes()

		this.modelEntity match {
			case e: EntityCreature =>
				e.tasks.taskEntries.clear()
				e.targetTasks.taskEntries.clear()
			case _ =>
		}
	}

	final def getModelEntity: EntityLivingBase = this.modelEntity

	override def writeEntityToNBT(compound: NBTTagCompound): Unit = {
		compound.setString("modelEntityID", this.modelEntityID)

	}

	override def readEntityFromNBT(compound: NBTTagCompound): Unit = {
		this.setModelEntity(compound.getString("modelEntityID"))

	}

	override def writeSpawnData(buffer: ByteBuf): Unit = {
		ByteBufUtils.writeUTF8String(buffer, this.modelEntityID)

	}

	override def readSpawnData(buffer: ByteBuf): Unit = {
		this.setModelEntity(ByteBufUtils.readUTF8String(buffer))

	}

	// ~~~~~ Reflection of Model Entity ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def onUpdate(): Unit = {
		super.onUpdate()
		try {
			if (this.modelEntity != null) this.modelEntity.onUpdate()
		}
		catch {
			case e: Exception => e.printStackTrace()
		}
	}

	override def isEntityUndead: Boolean = this.modelEntity != null && this.modelEntity.isEntityUndead

	override def getYOffset: Double = if (this.modelEntity == null) 0 else this.modelEntity.getYOffset

}
