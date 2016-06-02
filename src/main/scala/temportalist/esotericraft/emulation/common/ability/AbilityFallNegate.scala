package temportalist.esotericraft.emulation.common.ability

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagByte
import temportalist.esotericraft.api.emulation.IAbility
import temportalist.esotericraft.api.emulation.IAbility.Ability
import temportalist.esotericraft.api.emulation.ability.IAbilityFallNegate

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "fallNegate")
class AbilityFallNegate extends AbilityBase[NBTTagByte] with IAbilityFallNegate {

	// ~~~~~ Naming

	override def getName: String = "No Fall Damage"

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {

		entity.fallDistance = -0.5F

	}

}
