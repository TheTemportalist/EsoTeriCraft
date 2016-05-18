package temportalist.esotericraft.galvanization.common.entity.emulator.ability

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import temportalist.esotericraft.api.galvanize.IAbility.Ability
import temportalist.esotericraft.api.galvanize.ability.IAbilityResistancePoison

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "poisonResistance")
class AbilityResistancePoison extends AbilityBase[NBTTagByte] with IAbilityResistancePoison {

	private val POISON_KEY = new ResourceLocation("poison")

	// ~~~~~ Naming

	override def getName: String = "Resistance to Poison"

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {
		val potion = Potion.REGISTRY.getObject(this.POISON_KEY)
		if (entity.isPotionActive(potion)) entity.removePotionEffect(potion)
	}

}
