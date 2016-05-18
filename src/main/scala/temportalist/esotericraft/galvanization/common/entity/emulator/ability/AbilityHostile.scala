package temportalist.esotericraft.galvanization.common.entity.emulator.ability

import net.minecraft.nbt.NBTTagByte
import temportalist.esotericraft.api.galvanize.IAbility.Ability
import temportalist.esotericraft.api.galvanize.ability.IAbilityHostile

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
