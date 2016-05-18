package temportalist.esotericraft.galvanization.common.entity.emulator.ability

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.nbt.NBTTagByte
import net.minecraft.util.math.{BlockPos, MathHelper}
import temportalist.esotericraft.api.galvanize.IAbility.Ability
import temportalist.esotericraft.api.galvanize.ability.IAbilitySunburn

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "sunburn")
class AbilitySunburn extends AbilityBase[NBTTagByte] with IAbilitySunburn {

	// ~~~~~ Naming

	override def getName: String = "Sunburn"

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {

		entity match {
			case player: EntityPlayer =>
				if (player.capabilities.isCreativeMode) return
			case _ =>
		}

		if (!entity.getEntityWorld.isRemote && entity.getEntityWorld.isDaytime) {
			val f = entity.getBrightness(1F)
			if (f > 0.5F && entity.getRNG.nextFloat() * 30F < (f - 0.4F) * 2F &&
					entity.getEntityWorld.canBlockSeeSky(entity.getPosition)) {
				val stackHelmet = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD)
				if (stackHelmet != null) {
					if (stackHelmet.isItemStackDamageable) {
						stackHelmet.setItemDamage(stackHelmet.getItemDamage + entity.getRNG.nextInt(2))
						if (stackHelmet.getItemDamage <= 0) {
							entity.renderBrokenItemStack(stackHelmet)
							entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, null)
						}
					}
					return
				}
				entity.setFire(8)
			}
		}

	}

}
