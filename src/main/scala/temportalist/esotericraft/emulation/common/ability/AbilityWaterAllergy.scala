package temportalist.esotericraft.emulation.common.ability

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.util.DamageSource
import temportalist.esotericraft.api.emulation.IAbility
import temportalist.esotericraft.api.emulation.IAbility.Ability
import temportalist.esotericraft.api.emulation.ability.IAbilityWaterAllergy

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "waterAllergy")
class AbilityWaterAllergy extends AbilityBase[NBTTagByte] with IAbilityWaterAllergy {

	// ~~~~~ Naming

	override def getName: String = "Water Allergy"

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {
		if (entity.isWet) entity.attackEntityFrom(DamageSource.drown, 1F)
	}

}
