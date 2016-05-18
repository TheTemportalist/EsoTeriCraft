package temportalist.esotericraft.galvanization.common.entity.emulator.ability

import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.nbt.NBTTagByte
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import temportalist.esotericraft.api.galvanize.IAbility.Ability
import temportalist.esotericraft.api.galvanize.ability.IAbilityFireImmunity

/**
  *
  * Created by TheTemportalist on 5/17/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "fireImmunity")
class AbilityFireImmunity extends AbilityBase[NBTTagByte] with IAbilityFireImmunity {

	private var isImmuneToFire_Default = false

	// ~~~~~ Naming

	override def getName: String = "Fire Immunity"

	// ~~~~~ NBT

	override def hasNBT: Boolean = true

	override def serializeNBT(): NBTTagByte = new NBTTagByte(
		(if (this.isImmuneToFire_Default) 1 else 0).toByte
	)

	override def deserializeNBT(nbt: NBTTagByte): Unit = {
		this.isImmuneToFire_Default = nbt.getByte > 0
	}

	// ~~~~~ Entity Handling

	override def onApplicationTo(entity: EntityLivingBase): Unit = {
		this.isImmuneToFire_Default = entity.isImmuneToFire
	}

	override def onUpdate(entity: EntityLivingBase): Unit = {
		if (!entity.isImmuneToFire) this.setImmuneToFire(entity, isImmune = true)
		if (entity.isBurning) entity.extinguish()
	}

	override def onRemovalFrom(entity: EntityLivingBase): Unit = {
		this.setImmuneToFire(entity, this.isImmuneToFire_Default)
	}

	def setImmuneToFire(entity: Entity, isImmune: Boolean): Unit ={
		ObfuscationReflectionHelper.setPrivateValue(classOf[Entity], entity,
			Boolean.box(isImmune), "isImmuneToFire", "field_70178_ae")
	}

}
