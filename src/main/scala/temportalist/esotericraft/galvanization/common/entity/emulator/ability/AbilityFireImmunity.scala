package temportalist.esotericraft.galvanization.common.entity.emulator.ability

import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.nbt.{NBTBase, NBTTagByte}
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import temportalist.esotericraft.api.galvanize.IAbility
import temportalist.esotericraft.api.galvanize.IAbility.Ability

/**
  *
  * Created by TheTemportalist on 5/17/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "fireImmunity")
class AbilityFireImmunity extends IAbility[NBTTagByte] {

	private var isImmuneToFire_Default = false

	override def getName: String = "Fire Immunity"

	override def parseMappingArguments(args: Array[AnyRef]): Unit = {}

	override def encodeMappingArguments(): Array[String] = Array[String]()

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

	override def serializeNBT(): NBTTagByte = new NBTTagByte(
		(if (this.isImmuneToFire_Default) 1 else 0).toByte
	)

	override def deserialize(nbt: NBTBase): Unit = this.deserializeNBT(nbt.asInstanceOf[NBTTagByte])

	override def deserializeNBT(nbt: NBTTagByte): Unit = {
		this.isImmuneToFire_Default = nbt.getByte > 0
	}

}
