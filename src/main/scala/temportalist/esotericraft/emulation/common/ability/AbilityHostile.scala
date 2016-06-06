package temportalist.esotericraft.emulation.common.ability

import com.google.common.base.Predicate
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.{Entity, EntityLiving, EntityLivingBase}
import net.minecraft.nbt.NBTTagByte
import temportalist.esotericraft.api.emulation.IAbility.Ability
import temportalist.esotericraft.api.emulation.ability.IAbilityHostile

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "hostile")
class AbilityHostile extends AbilityBase[NBTTagByte] with IAbilityHostile {

	private val radius = 10

	// ~~~~~ Naming

	override def getName: String = "Hostile"

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {

		return // the below code does not work

		// very similar to fear
		// will make all mobs ignore you

		val entityList = JavaConversions.asScalaBuffer(
			entity.getEntityWorld.getEntitiesInAABBexcluding(entity,
				entity.getEntityBoundingBox.expand(50, 50, 50),
				new Predicate[Entity] {
					override def apply(input: Entity): Boolean = {
						input.isInstanceOf[IMob]
					}
				}
			)
		)

		if (entityList.isEmpty) return

		for (entityAround <- entityList) {
			entityAround match {
				case creature: EntityLiving =>

					if (creature.getAttackTarget != null &&
							creature.getAttackTarget.getEntityId == entity.getEntityId) {
						creature.setAttackTarget(null)
					}
					if (creature.getAITarget != null &&
							creature.getAITarget.getEntityId == entity.getEntityId) {
						creature.setRevengeTarget(null)
					}


				case _ =>
			}
		}

	}

}
