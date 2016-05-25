package temportalist.esotericraft.galvanization.common.task

import net.minecraft.item.ItemStack
import net.minecraft.nbt._

import scala.reflect.runtime.universe._

/**
  *
  * Created by TheTemportalist on 5/24/2016.
  *
  * @author TheTemportalist
  */
trait INBTCreator {

	private val nbtTypes = Map[Type, Int](
		typeOf[Byte] -> 1, typeOf[NBTTagByte] -> 1,
		typeOf[Short] -> 2, typeOf[NBTTagShort] -> 2,
		typeOf[Int] -> 3, typeOf[NBTTagInt] -> 3,
		typeOf[Long] -> 4, typeOf[NBTTagLong] -> 4,
		typeOf[Float] -> 5, typeOf[NBTTagFloat] -> 5,
		typeOf[Double] -> 6, typeOf[NBTTagDouble] -> 6,
		typeOf[Array[Byte]] -> 7, typeOf[NBTTagByteArray] -> 7,
		typeOf[String] -> 8, typeOf[NBTTagString] -> 8,
		typeOf[NBTTagList] -> 9,
		typeOf[NBTTagCompound] -> 10,
		typeOf[Array[Int]] -> 11, typeOf[NBTTagIntArray] -> 11
	)

	final def getCompoundNew: NBTTagCompound = new NBTTagCompound

	final def checkStackNBT(stack: ItemStack): ItemStack = {
		if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound)
		stack
	}

	final def getNBTType[T: TypeTag]: Int = this.nbtTypes(typeOf[T])

	final def getTagList[T: TypeTag](tag: NBTTagCompound, key: String): NBTTagList = {
		tag.getTagList(key, this.getNBTType[T])
	}

	final def getTagListAsIterable[T: TypeTag](tagList: NBTTagList): Iterable[T] = {
		Iterable.apply(
			(for (i <- 0 until tagList.tagCount()) yield this.castNBT[T](tagList.get(i))):_*
		)
	}

	final def castNBT[T: TypeTag](base: NBTBase): T = {
		(this.getNBTType[T] match {
			case 1 => base.asInstanceOf[NBTTagByte].getByte
			case 2 => base.asInstanceOf[NBTTagShort].getShort
			case 3 => base.asInstanceOf[NBTTagInt].getInt
			case 4 => base.asInstanceOf[NBTTagLong].getLong
			case 5 => base.asInstanceOf[NBTTagFloat].getFloat
			case 6 => base.asInstanceOf[NBTTagDouble].getDouble
			case 7 => base.asInstanceOf[NBTTagByteArray].getByteArray
			case 8 => base.asInstanceOf[NBTTagString].getString
			case 9 => base.asInstanceOf[NBTTagList]
			case 10 => base.asInstanceOf[NBTTagCompound]
			case 11 => base.asInstanceOf[NBTTagIntArray].getIntArray
			case _ => null
		}).asInstanceOf[T]
	}

	final def getTagListEntry[T: TypeTag](tagList: NBTTagList, i: Int): T = {
		(this.getNBTType[T] match {
			case 0 => tagList.get(i).asInstanceOf[NBTTagByte].getByte
			case _ => null
		}).asInstanceOf[T]
	}

}
