package temportalist.esotericraft.galvanization.common.entity.emulator.ability

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import temportalist.esotericraft.api.galvanize.IAbility.Ability
import temportalist.esotericraft.api.galvanize.ability.IAbilityResistanceWither

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "witherResistance")
class AbilityResistanceWither extends AbilityBase[NBTTagByte] with IAbilityResistanceWither {

	private val WITHER_KEY = new ResourceLocation("wither")

	// ~~~~~ Naming

	override def getName: String = "Resistance to Wither"

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {
		val potion = Potion.REGISTRY.getObject(this.WITHER_KEY)
		if (entity.isPotionActive(potion)) entity.removePotionEffect(potion)
	}

}
