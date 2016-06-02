package temportalist.esotericraft.emulation.common.ability

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagByte
import temportalist.esotericraft.api.emulation.IAbility
import temportalist.esotericraft.api.emulation.IAbility.Ability
import temportalist.esotericraft.api.emulation.ability.IAbilityStep
import temportalist.esotericraft.galvanization.common.Galvanize

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "step")
class AbilityStep extends AbilityBase[NBTTagByte] with IAbilityStep {

	private var stepHeight = 0F

	// ~~~~~ Naming

	override def getName: String = "Step Height"

	// ~~~~~ Map Parsing

	override def parseMappingArguments(args: Array[AnyRef], entry: String): Unit = {
		try {
			this.stepHeight = args(0).toString.toLowerCase.toFloat
		}
		catch {
			case e: Exception =>
				Galvanize.log("[AbilityStep] Error parsing mapping arguments.")
				e.printStackTrace()
		}
	}

	override def encodeMappingArguments(): Array[String] = {
		Array[String](
			this.stepHeight + "F"
		)
	}

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {
		if (entity.stepHeight != this.stepHeight) entity.stepHeight = this.stepHeight
	}

	override def onRemovalFrom(entity: EntityLivingBase): Unit = {
		if (entity.stepHeight == this.stepHeight) entity.stepHeight = 0.5F
	}

}
