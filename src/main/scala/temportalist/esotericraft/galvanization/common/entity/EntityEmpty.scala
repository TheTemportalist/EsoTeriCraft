package temportalist.esotericraft.galvanization.common.entity

import io.netty.buffer.ByteBuf
import net.minecraft.entity._
import net.minecraft.entity.ai.attributes.IAttribute
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.entity.emulator.{EntityState, IEntityEmulator}

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class EntityEmpty(world: World) extends EntityCreature(world) with IEntityAdditionalSpawnData with IEntityEmulator {

	def this(world: World, entityName: String) {
		this(world)

		this.setEntityState(entityName, this.getEntityWorld)
		Galvanize.log("Init " + entityName)

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
		//this.tasks.addTask(1, new EntityAIPlayer(this))
		//this.tasks.addTask(2, new EntityAIWander(this, 0.6D))

	}

	override def onEntityConstructed(entity: EntityLivingBase): Unit = {
		Galvanize.log("on construct: " + entity.getClass.getSimpleName)
		this.updateAttributes()
		entity match {
			case e: EntityCreature =>
				e.tasks.taskEntries.clear()
				e.targetTasks.taskEntries.clear()
			case _ =>
		}
	}

	// ~~~~~ NBT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def writeEntityToNBT(nbt: NBTTagCompound): Unit = {

		if (this.getEntityName != null) nbt.setString("entity_name", this.getEntityName)
		if (this.getEntityState != null)
			nbt.setTag("entity_state", this.getEntityState.serializeNBT())

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
	}

	override def writeSpawnData(buffer: ByteBuf): Unit = {

		ByteBufUtils.writeUTF8String(buffer,
			if (this.getEntityName != null) this.getEntityName else "")

		ByteBufUtils.writeTag(buffer,
			if (this.getEntityState != null) this.getEntityState.serializeNBT() else new NBTTagCompound)

	}

	override def readSpawnData(buffer: ByteBuf): Unit = {

		this.setEntityName(ByteBufUtils.readUTF8String(buffer))

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

}
