package temportalist.esotericraft.galvanization.common.entity.emulator

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable

/**
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

	def setType(entityType: EntityType): Unit = {
		this.entityType = entityType
		this.instance = null
	}

	def getName: String = {
		if (this.instance == null) this.entityType.getEntityName
		else this.instance.getName
	}

	def getInstance(world: World): EntityLivingBase = {
		if (this.instance != null && this.instance.getEntityWorld != world)
			this.instance = null
		if (this.instance == null) this.instance = this.entityType.createInstance(world)
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
