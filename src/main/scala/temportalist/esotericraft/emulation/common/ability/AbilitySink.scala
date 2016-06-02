package temportalist.esotericraft.emulation.common.ability

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagByte
import temportalist.esotericraft.api.emulation.IAbility
import temportalist.esotericraft.api.emulation.IAbility.Ability
import temportalist.esotericraft.api.emulation.ability.IAbilitySink

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "sink")
class AbilitySink extends AbilityBase[NBTTagByte] with IAbilitySink {

	private var isInWater = false

	// ~~~~~ Naming

	override def getName: String = "Sinking"

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {

		val isFlying = this.isFlying(entity)

		if (entity.isInWater || entity.isInLava) {
			if (entity.isCollidedHorizontally)
				entity.motionY = 0.07D
			else if (entity.motionY > -0.07D && !isFlying)
				entity.motionY = -0.07D
		}

		if (!entity.isInWater && this.isInWater && !isFlying)
			entity.motionY = 0.32D

		this.isInWater = entity.isInWater

	}

}
