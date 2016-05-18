package temportalist.esotericraft.galvanization.common.entity.emulator.ability

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTBase
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.api.galvanize.IAbility

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
abstract class AbilityBase[N <: NBTBase] extends IAbility[N] {

	// ~~~~~ Map Parsing

	override def parseMappingArguments(args: Array[AnyRef]): Unit = {}

	override def encodeMappingArguments(): Array[String] = Array[String]()

	// ~~~~~ NBT

	override def hasNBT: Boolean = false

	override def serializeNBT(): N = null.asInstanceOf[N]

	override def deserializeNBT(nbt: N): Unit = {}

	override def deserialize(nbt: NBTBase): Unit = this.deserializeNBT(nbt.asInstanceOf[N])

	// ~~~~~ Entity Handling

	override def onApplicationTo(entity: EntityLivingBase): Unit = {}

	override def onUpdate(entity: EntityLivingBase): Unit = {}

	override def onRemovalFrom(entity: EntityLivingBase): Unit = {}

	// ~~~~~ Rendering

	@SideOnly(Side.CLIENT)
	override def renderPost(entity: EntityLivingBase): Unit = {}

}
