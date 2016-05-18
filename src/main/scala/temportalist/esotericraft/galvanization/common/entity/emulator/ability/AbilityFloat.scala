package temportalist.esotericraft.galvanization.common.entity.emulator.ability

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagByte
import temportalist.esotericraft.api.galvanize.IAbility.Ability
import temportalist.esotericraft.api.galvanize.ability.IAbilityFloat
import temportalist.esotericraft.galvanization.common.Galvanize

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "float")
class AbilityFloat extends AbilityBase[NBTTagByte] with IAbilityFloat {

	private var terminalVelocity = -1000D
	private var negateFallDistance = false

	// ~~~~~ Naming

	override def getName: String = "Float"

	// ~~~~~ Map Parsing

	override def parseMappingArguments(args: Array[AnyRef]): Unit = {
		try {
			this.terminalVelocity = args(0).toString.toLowerCase.toDouble
			this.negateFallDistance = args(1).toString.toLowerCase.toBoolean
		}
		catch {
			case e: Exception =>
				Galvanize.log("[AbilityFloat] Error parsing mapping arguments.")
				e.printStackTrace()
		}
	}

	override def encodeMappingArguments(): Array[String] = {
		Array[String](
			this.terminalVelocity + "D",
			if (this.negateFallDistance) "true" else "false"
		)
	}

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {

		if (!this.isFlying(entity)) {
			if (entity.motionY < this.terminalVelocity)
				entity.motionY = this.terminalVelocity
			if (this.negateFallDistance)
				entity.fallDistance = 0F
		}

	}

}
