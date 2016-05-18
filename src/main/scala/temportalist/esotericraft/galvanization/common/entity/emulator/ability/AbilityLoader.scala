package temportalist.esotericraft.galvanization.common.entity.emulator.ability

import javax.annotation.Nullable

import net.minecraft.nbt.NBTBase
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import temportalist.esotericraft.api.galvanize.IAbility
import temportalist.esotericraft.api.galvanize.IAbility.Ability
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.main.common.api.AnnotationLoader

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
object AbilityLoader extends AnnotationLoader(classOf[Ability], classOf[IAbility[_ <: NBTBase]]) {

	def preInit(event: FMLPreInitializationEvent): Unit = {
		this.loadAnnotations(event)
	}

	override def onAnnotationClassFound(
			implementingClass: Class[_ <: IAbility[_ <: NBTBase]],
			annotationInfo: Map[String, AnyRef]): Unit = {
		val id = annotationInfo.getOrElse("id", "").toString
		if (id.nonEmpty) Galvanize.MAP_STRING_to_CLASS_ABILITIES.put(id, implementingClass)
		else {
			Galvanize.log("ID not found for Ability class \'" + implementingClass.getCanonicalName + "\'. Skipping.")
		}
	}

	@Nullable
	def getAbilityID(inst: IAbility[_ <: NBTBase]): String = this.getAbilityID(inst.getClass)

	@Nullable
	def getAbilityID(key: Class[_ <: IAbility[_ <: NBTBase]]): String = {
		val id = this.getAnnotationInfo(key).getOrElse("id", "").toString
		if (id.nonEmpty) id else null
	}

}
