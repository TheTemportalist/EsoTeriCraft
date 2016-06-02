package temportalist.esotericraft.emulation.common.ability

import net.minecraft.nbt.NBTTagByte
import temportalist.esotericraft.api.emulation.IAbility
import temportalist.esotericraft.api.emulation.IAbility.Ability
import temportalist.esotericraft.api.emulation.ability.IAbilityHostile

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "hostile")
class AbilityHostile extends AbilityBase[NBTTagByte] with IAbilityHostile {

	// ~~~~~ Naming

	override def getName: String = "Hostile"

}
