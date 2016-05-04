package temportalist.esotericraft.main.common.util

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import temportalist.origin.api.common.lib.Vect

import scala.reflect.runtime.universe._

/**
  *
  * Created by TheTemportalist on 4/26/2016.
  *
  * @author TheTemportalist
  */
object NBT {

	private val KEY_X = "x"
	private val KEY_Y = "y"
	private val KEY_Z = "z"

	def store(nbt: NBTTagCompound, x: Int, y: Int, z: Int): NBTTagCompound = {
		nbt.setInteger(KEY_X, x)
		nbt.setInteger(KEY_Y, y)
		nbt.setInteger(KEY_Z, z)
		nbt
	}

	def store(x: Int, y: Int, z: Int): NBTTagCompound = this.store(new NBTTagCompound, x, y, z)

	def store(nbt: NBTTagCompound, x: Double, y: Double, z: Double): NBTTagCompound = {
		nbt.setDouble(KEY_X, x)
		nbt.setDouble(KEY_Y, y)
		nbt.setDouble(KEY_Z, z)
		nbt
	}

	def store(x: Double, y: Double, z: Double): NBTTagCompound = this.store(new NBTTagCompound, x, y, z)

	def store(nbt: NBTTagCompound, pos: BlockPos): NBTTagCompound = {
		if (pos != null) this.store(nbt, pos.getX, pos.getY, pos.getZ)
		else nbt
	}

	def store(pos: BlockPos): NBTTagCompound = this.store(new NBTTagCompound, pos)

	@throws(classOf[ClassCastException])
	def store(nbt: NBTTagCompound, entity: Entity, which: EnumNBT): NBTTagCompound = {
		which match {
			case EnumNBT.ENTITY_POSITION => this.store(nbt, entity.posX, entity.posY, entity.posZ)
			case EnumNBT.ENTITY_MOTION => this.store(nbt, entity.motionX, entity.motionY, entity.motionZ)
			case EnumNBT.PLAYER_SPAWN =>
				entity match {
					case player: EntityPlayer =>
						val spawn = player.getBedLocation(player.dimension)
						if (spawn != null) this.store(nbt, spawn)
						else nbt
					case _ => throw new ClassCastException("Entity of class " + entity.getClass.getCanonicalName + " is not a subclass of " + classOf[EntityPlayer].getCanonicalName)
				}
			case _ => nbt
		}
	}

	def store(entity: Entity, which: EnumNBT): NBTTagCompound = this.store(new NBTTagCompound, entity, which)

	def storeEntityPosition(nbt: NBTTagCompound, entity: Entity): NBTTagCompound =
		this.store(nbt, entity, EnumNBT.ENTITY_POSITION)

	def storeEntityPosition(entity: Entity): NBTTagCompound =
		this.store(new NBTTagCompound, entity, EnumNBT.ENTITY_POSITION)

	def storeEntityMotion(nbt: NBTTagCompound, entity: Entity): NBTTagCompound =
		this.store(nbt, entity, EnumNBT.ENTITY_MOTION)

	def storeEntityMotion(entity: Entity): NBTTagCompound =
		this.store(new NBTTagCompound, entity, EnumNBT.ENTITY_MOTION)

	def storePlayerSpawn(nbt: NBTTagCompound, entity: Entity): NBTTagCompound =
		this.store(nbt, entity, EnumNBT.PLAYER_SPAWN)

	def storePlayerSpawn(entity: Entity): NBTTagCompound =
		this.store(new NBTTagCompound, entity, EnumNBT.PLAYER_SPAWN)

	def get[T: TypeTag](tag: NBTTagCompound, key: String): T = {
		(try {
			typeOf[T] match {
				case t if t =:= typeOf[Int] => tag.getInteger(key)
				case t if t =:= typeOf[Double] => tag.getDouble(key)
				case t if t =:= typeOf[BlockPos] =>
					val bTag = tag.getCompoundTag(key)
					new BlockPos(this.get[Int](bTag, KEY_X), this.get[Int](bTag, KEY_Y), this.get[Int](bTag, KEY_Z))
				case t if t =:= typeOf[Vect] =>
					val vTag = tag.getCompoundTag(key)
					new Vect(this.get[Double](vTag, KEY_X), this.get[Double](vTag, KEY_Y), this.get[Double](vTag, KEY_Z))
				case _ => null
			}
		}
		catch {
			case e: Exception =>
				e.printStackTrace()
				null
		}).asInstanceOf[T]
	}

}
