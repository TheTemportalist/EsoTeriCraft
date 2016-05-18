package temportalist.esotericraft.galvanization.common.entity.emulator.ability

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagByte
import temportalist.esotericraft.api.galvanize.IAbility.Ability
import temportalist.esotericraft.api.galvanize.ability.IAbilityClimb

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "climb")
class AbilityClimb extends AbilityBase[NBTTagByte] with IAbilityClimb {

	// ~~~~~ Naming

	override def getName: String = "Climbing"

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {

		if (entity.isCollidedHorizontally) {
			entity.fallDistance = 0F
			if (entity.isSneaking)
				entity.motionY = 0D
			else
				entity.motionY = 0.1176D // (0.2D - 0.08D) * 0.98D
		}

		if (!entity.getEntityWorld.isRemote) {
			val motionX = entity.posX - entity.lastTickPosX
			val motionY = entity.posY - entity.lastTickPosY
			val motionZ = entity.posZ - entity.lastTickPosZ
			if (motionY > 0 && (motionX == 0 || motionZ == 0))
				entity.fallDistance = 0F
		}

	}

}
