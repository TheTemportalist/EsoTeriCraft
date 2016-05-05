package temportalist.esotericraft.api.util.item

import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import temportalist.origin.api.common.utility.NBTHelper

import scala.collection.mutable
import scala.reflect.runtime.universe._

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
trait INBTHandler extends Item {

	// ~~~~~ General Getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final def hasTag(stack: ItemStack, key: String): Boolean = {
		stack.hasTagCompound && stack.getTagCompound.hasKey(key)
	}

	final def getTagOrElse(stack: ItemStack): NBTTagCompound = {
		if (stack.hasTagCompound) stack.getTagCompound
		else new NBTTagCompound
	}

	final def getTagOrElseSet(stack: ItemStack): NBTTagCompound = {
		if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound)
		stack.getTagCompound
	}

	final def get[T: TypeTag](tag: NBTTagCompound, key: String): T = {
		if (tag.hasKey(key)) NBTHelper.get[T](tag.getTag(key))
		else null.asInstanceOf[T]
	}

	final def get[T: TypeTag](stack: ItemStack, key: String): T = {
		this.get[T](this.getTagOrElseSet(stack), key)
	}

	final def removeTag(stack: ItemStack, key: String): Boolean = {
		if (this.hasTag(stack, key)) {
			stack.getTagCompound.removeTag(key)
			true
		}
		else false
	}

	// ~~~~~ Custom Keys ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private val keyMap = mutable.Map[String, Type]()

	final def addKey[T: TypeTag](key: String): Boolean = {
		if (this.keyMap.contains(key)) false
		else {
			this.keyMap(key) = typeOf[T]
			true
		}
	}

	final def set[T](tag: NBTTagCompound, key: String, obj: T)(implicit tTag: TypeTag[T]): Boolean = {
		if (!this.keyMap.contains(key)) return false
		if (this.keyMap(key) != tTag.tpe) return false
		tag.setTag(key, NBTHelper.asTag(obj))
		true
	}

	final def set[T](stack: ItemStack, key: String, obj: T)(implicit tTag: TypeTag[T]): Boolean = {
		this.set(this.getTagOrElseSet(stack), key, obj)
	}

}
