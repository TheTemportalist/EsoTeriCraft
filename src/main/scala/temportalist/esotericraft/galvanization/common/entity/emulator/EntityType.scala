package temportalist.esotericraft.galvanization.common.entity.emulator

import javax.annotation.{Nonnull, Nullable}

import net.minecraft.entity.{EntityList, EntityLiving, EntityLivingBase}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable

/**
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
class EntityType extends Comparable[EntityType] with INBTSerializable[NBTTagCompound] {

	private var entityName: String = _
	private var tagTemplate: NBTTagCompound = _
	private var tagEntity: NBTTagCompound = _

	def this(entityName: String) {
		this()
		this.entityName = entityName
	}

	def getEntityName: String = this.entityName

	def setTagTemplate(nbt: NBTTagCompound): Unit = this.tagTemplate = nbt

	def getTagTemplate: NBTTagCompound = this.tagTemplate

	def setTagEntity(nbt: NBTTagCompound): Unit = this.tagEntity = nbt

	def getTagEntity: NBTTagCompound = this.tagEntity

	@Nonnull
	def createInstance(world: World): EntityLivingBase = {
		var entity: EntityLivingBase = null

		try {
			val tagForCreation: NBTTagCompound = this.tagTemplate.copy()
					.asInstanceOf[NBTTagCompound]
			EntityList.createEntityFromNBT(tagForCreation, world) match {
				case living: EntityLivingBase => entity = living
				case _ =>
			}
		}
		catch {
			case e: Exception =>
		}

		if (entity == null)
			entity = EntityList.createEntityByName("Pig", world).asInstanceOf[EntityLivingBase]
		entity
	}

	def cleanForModelUsage(entity: EntityLivingBase, tag: NBTTagCompound): NBTTagCompound = {
		//Entity tags
		tag.removeTag("Fire")
		tag.removeTag("Riding")

		//EntityLivingBase tags
		tag.setFloat("HealF", Short.MaxValue)
		tag.setShort("Health", Short.MinValue)
		tag.removeTag("HurtTime")
		tag.removeTag("HurtByTimestamp")
		tag.removeTag("DeathTime")
		tag.removeTag("AbsorptionAmount")
		tag.removeTag("Attributes")
		tag.removeTag("ActiveEffects")

		//EntityAgeable tags
		tag.setInteger("Age", if (entity.isChild) -24000 else 0)
		tag.removeTag("ForcedAge")
		tag.removeTag("InLove")

		//EntityTameable tags
		tag.removeTag("Sitting")

		//EntityLiving tags
		if (entity.isInstanceOf[EntityLiving]) {
			tag.setBoolean("CanPickUpLoot", true)
			tag.setBoolean("PersistenceRequired", true)
			tag.setBoolean("NoAI", true)
		}
		tag.removeTag("Equipment")
		tag.removeTag("DropChances")
		tag.removeTag("Leashed")
		tag.removeTag("Leash")

		// TODO this sets modifiers NBTHandler.modifyNBT(living.getClass, tag)

		tag.removeTag("bukkit")

		tag
	}

	override def compareTo(o: EntityType): Int = this.entityName.compareTo(o.entityName)

	override def equals(obj: scala.Any): Boolean = {
		obj match {
			case other: EntityType =>
				this.entityName == other.entityName &&
					this.tagTemplate.equals(other.tagTemplate) &&
					this.tagEntity.equals(other.tagEntity)
			case _ => false
		}
	}

	override def serializeNBT(): NBTTagCompound = {
		val nbt = new NBTTagCompound
		nbt.setString("id", this.entityName)
		if (this.tagTemplate != null) nbt.setTag("tag_template", this.tagTemplate)
		if (this.tagEntity != null) nbt.setTag("tag_entity", this.tagEntity)
		nbt
	}

	override def deserializeNBT(nbt: NBTTagCompound): Unit = {
		this.entityName = nbt.getString("id")
		if (nbt.hasKey("tag_tempalte")) this.tagTemplate = nbt.getCompoundTag("tag_template")
		if (nbt.hasKey("tag_entity")) this.tagEntity = nbt.getCompoundTag("tag_entity")
	}

}
object EntityType {

	@Nullable
	def create(entity: EntityLivingBase): EntityType = {

		val tagTemplate = new NBTTagCompound

		if (!entity.writeToNBTOptional(tagTemplate)) return null

		val variant = new EntityType(tagTemplate.getString("id"))

		variant.cleanForModelUsage(entity, tagTemplate)

		val tagEntity = new NBTTagCompound
		entity.writeEntityToNBT(tagEntity)
		variant.cleanForModelUsage(entity, tagEntity)

		for (key <- Seq("ForgeData", "CustomName", "CustomNameVisible")) {
			if (tagTemplate.hasKey(key)) {
				tagEntity.setTag(key, tagTemplate.getTag(key))
				tagTemplate.removeTag(key)
			}
		}

		if (entity.getCustomNameTag != null && entity.getCustomNameTag.length > 0) {
			tagEntity.setString("CustomName", entity.getCustomNameTag)
			tagEntity.setBoolean("CustomNameVisible", entity.getAlwaysRenderNameTag)
		}

		variant.setTagTemplate(tagTemplate)
		variant.setTagEntity(tagEntity)

		variant
	}

}
