package temportalist.esotericraft.galvanization.common.entity.emulator.ability

import net.minecraft.entity.ai.RandomPositionGenerator
import net.minecraft.entity.{EntityCreature, EntityLivingBase}
import net.minecraft.nbt.NBTTagByte
import net.minecraft.pathfinding.{PathEntity, PathPoint}
import net.minecraft.util.math.Vec3d
import temportalist.esotericraft.api.galvanize.IAbility.Ability
import temportalist.esotericraft.api.galvanize.ability.IAbilityFear
import temportalist.esotericraft.galvanization.common.Galvanize

import scala.collection.JavaConversions
import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "fear")
class AbilityFear extends AbilityBase[NBTTagByte] with IAbilityFear {

	private var classesWhichFear: Array[Class[_]] = null
	private var radius = 0
	private var runSpeed = 0D

	// ~~~~~ Naming

	override def getName: String = "Fear"

	// ~~~~~ Map Parsing

	override def parseMappingArguments(args: Array[AnyRef], entry: String): Unit = {
		try {
			this.radius = args(0).toString.toLowerCase.toInt
			this.runSpeed = args(1).toString.toLowerCase.toDouble
			val classesWhichHaveLeFear = ListBuffer[Class[_]]()
			for (i <- 2 until args.length) {
				try {
					val clazz = Class.forName(args(i).toString)
					if (classOf[EntityCreature].isAssignableFrom(clazz))
						classesWhichHaveLeFear += clazz
				}
				catch {
					case e: Exception =>
						var arrStr = ""
						for (i <- args.indices)
							arrStr += "(" + i + ")[" + args(i) + "]"
						Galvanize.log("[AbilityFear] Could not parse class with name \'" +
								args(i).toString +
								"\'. The class may not be the canonical name. Args: " + arrStr + " | Entry: " + entry)
				}
			}
			this.classesWhichFear = classesWhichHaveLeFear.toArray
		}
		catch {
			case e: Exception =>
				Galvanize.log("[AbilityFear] Error parsing mapping arguments.")
				e.printStackTrace()
		}
	}

	override def encodeMappingArguments(): Array[String] = {
		var classesString = ""
		for (i <- this.classesWhichFear.indices) {
			classesString += this.classesWhichFear(i).getCanonicalName
			if (i < this.classesWhichFear.length - 1)
				classesString += ","
		}
		Array[String](
			this.radius.toString,
			this.runSpeed + "D",
			classesString
		)
	}

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {
		if (entity.getEntityWorld.getWorldTime % 22L != 0) return

		val entityList = JavaConversions.asScalaBuffer(
			entity.getEntityWorld.getEntitiesWithinAABBExcludingEntity(
				entity, entity.getEntityBoundingBox.expand(this.radius, this.radius, this.radius)
			)
		)
		if (entityList.isEmpty) return

		for (entityAround <- entityList) {
			entityAround match {
				case creature: EntityCreature =>
					if (!this.doesCreatureFearThis(creature)) return

					val vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(
						creature, 16, 7, new Vec3d(entity.posX, entity.posY, entity.posZ)
					)
					if (vec3 != null &&
							!(entity.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) <
									entity.getDistanceSqToEntity(creature))) {
						val newPath = new PathEntity(Array[PathPoint](
							new PathPoint(vec3.xCoord.toInt, vec3.yCoord.toInt, vec3.zCoord.toInt)
						))
						creature.getNavigator.setPath(newPath, 1D)
						creature.getNavigator.setSpeed(runSpeed)
					}

				case _ =>
			}
		}

	}

	def doesCreatureFearThis(creature: EntityCreature): Boolean = {
		var currentClass: Class[_] = creature.getClass
		while (currentClass != classOf[EntityCreature].getSuperclass) {
			if (this.classesWhichFear.contains(currentClass)) return true
			else currentClass = currentClass.getSuperclass
		}
		false
	}

}
