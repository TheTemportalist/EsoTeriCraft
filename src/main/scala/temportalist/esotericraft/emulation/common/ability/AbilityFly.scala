package temportalist.esotericraft.emulation.common.ability

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagByte
import temportalist.esotericraft.api.emulation.IAbility
import temportalist.esotericraft.api.emulation.IAbility.Ability
import temportalist.esotericraft.api.emulation.ability.IAbilityFly
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.transmorigification.common.capability.{HelperGalvanize, IPlayerGalvanize}

import scala.collection.JavaConversions
import scala.util.control.Breaks._

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "fly")
class AbilityFly extends AbilityBase[NBTTagByte] with IAbilityFly {

	private var slowdownInWater = false

	// ~~~~~ Naming

	override def getName: String = "Flight"

	// ~~~~~ Map Parsing

	override def parseMappingArguments(args: Array[AnyRef], entry: String): Unit = {
		try {
			this.slowdownInWater = args(0).toString.toLowerCase.toBoolean
		}
		catch {
			case e: Exception =>
				Galvanize.log("[AbilityFly] Cannot parse mapping argument.")
				e.printStackTrace()
		}
	}

	override def encodeMappingArguments(): Array[String] = {
		Array[String](if (this.slowdownInWater) "true" else "false")
	}

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {
		entity match {
			case player: EntityPlayer =>

				if (!player.capabilities.allowFlying) {
					player.capabilities.allowFlying = true
					player.sendPlayerAbilities()
				}

				val isClient = player.getEntityWorld.isRemote

				if (player.capabilities.isFlying && !player.capabilities.isCreativeMode) {
					val motionX = if (isClient) player.motionX
					else player.posX - player.lastTickPosX
					val motionZ = if (isClient) player.motionZ
					else player.posZ - player.lastTickPosZ

					/*
					val i = Math.round(
						MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ) * 100)
					if (i > 0 && i < 10) {
						if (this.slowdownInWater && player.isInWater)
							player.addExhaustion(0.125F * i.toFloat * 0.01F)
						else
							player.addExhaustion(0.035F * i.toFloat * 0.01F)
					}
					else
						player.addExhaustion(0.002F)
					*/

					if (this.slowdownInWater && !isClient && player.isInWater) {
						var hasSwim = false

						HelperGalvanize.get(player) match {
							case galvanized: IPlayerGalvanize =>
								breakable {
									for (ability <-
									     JavaConversions.iterableAsScalaIterable(
										     galvanized.getEntityAbilities)) {
										if (classOf[AbilitySwim]
												.isAssignableFrom(ability.getClass)) {
											hasSwim = true
											break()
										}
									}
								}
							case _ =>
						}

						if (!hasSwim) {
							player.motionX *= 0.65D
							player.motionZ *= 0.65D
							player.motionZ *= 0.2D
						}
					}

				}

				player.fallDistance = 0F
			case _ =>
		}
	}

	override def onRemovalFrom(entity: EntityLivingBase): Unit = {
		entity match {
			case player: EntityPlayer =>
				if (!player.capabilities.isCreativeMode) {
					player.capabilities.allowFlying = false
					if (player.capabilities.isFlying)
						player.capabilities.isFlying = false
					player.sendPlayerAbilities()
				}
			case _ =>
		}
	}

}
