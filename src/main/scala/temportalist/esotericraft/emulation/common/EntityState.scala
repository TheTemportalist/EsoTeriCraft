package temportalist.esotericraft.emulation.common

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable

/**
  *
  * Based HEAVILY on [[https://github.com/iChun/Morph/blob/master/src/main/java/morph/common/morph/MorphInfo.java]]
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
final class EntityState extends Comparable[EntityState] with INBTSerializable[NBTTagCompound] {

	private var entityType: EntityType = _
	private var instance: EntityLivingBase = _

	def this(entityType: EntityType) {
		this()
		this.entityType = entityType
	}

	def this(nbt: NBTTagCompound) {
		this()
		this.deserializeNBT(nbt)
	}

	def setType(entityType: EntityType): Unit = {
		this.entityType = entityType
		this.instance = null
	}

	def getType: EntityType = this.entityType

	def getName: String = {
		if (this.instance == null) this.entityType.getEntityName
		else this.instance.getName
	}

	def getInstance(world: World, onChange: (EntityLivingBase) => Unit = null): EntityLivingBase = {
		if (this.instance != null && this.instance.getEntityWorld != world)
			this.instance = null
		if (this.instance == null) {
			this.instance = this.entityType.createInstance(world)
			if (onChange != null) onChange(instance)
		}
		this.instance
	}

	override def compareTo(o: EntityState): Int = {
		this.getName.toLowerCase.compareTo(o.getName.toLowerCase)
	}

	override def equals(obj: scala.Any): Boolean = {
		obj match {
			case other: EntityState => this.entityType.equals(other.entityType)
			case _ => false
		}
	}

	override def serializeNBT(): NBTTagCompound = {
		this.entityType.serializeNBT()
	}

	override def deserializeNBT(nbt: NBTTagCompound): Unit = {
		this.entityType = new EntityType
		this.entityType.deserializeNBT(nbt)
	}

	def onUpdate(): Unit = {
		if (this.instance != null) this.instance.onUpdate()
	}

}
