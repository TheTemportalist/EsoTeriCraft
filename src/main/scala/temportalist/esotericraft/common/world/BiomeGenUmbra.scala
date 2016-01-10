package temportalist.esotericraft.common.world

import java.awt.Color

import net.minecraft.entity.monster.EntityZombie
import net.minecraft.world.biome.BiomeGenBase
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.common.ModOptions

/**
  * Created by TheTemportalist on 1/5/2016.
  */
class BiomeGenUmbra extends BiomeGenBase(ModOptions.biomeUmbraID) {

	this.setTemperatureRainfall(0.8F, 0.4F)
	this.setSpawnListWithMobs()

	private def setSpawnListWithMobs(): Unit = {
		this.spawnableCreatureList.clear()
		this.spawnableCaveCreatureList.clear()
		this.spawnableWaterCreatureList.clear()

		/*
		val spawnEntryMap = mutable.Map[Class[_ <: EntityLiving], SpawnListEntry]()
		// Hide this to make all mobs the same ratio

		JavaConversions.asScalaBuffer(this.spawnableMonsterList).foreach(
			entry => spawnEntryMap(entry.entityClass) = entry)

		this.spawnableMonsterList.clear()

		val entityClasses = JavaConversions.asScalaSet(EntityList.classToStringMapping.keySet())
		val entMobClass = classOf[EntityMob]
		entityClasses.foreach(entityClass => {
			if (entMobClass.isAssignableFrom(entityClass)) {
				val mobClass = entityClass.asInstanceOf[Class[_ <: EntityMob]]
				if (spawnEntryMap contains mobClass)
				// NOTICE ME!!!
				// if mobs should not all be the same ratio & group size, see above (spawnEntryMap)
				//this.spawnableMonsterList.add(spawnEntryMap.getOrElse(mobClass, new SpawnListEntry(mobClass, 100, 1, 1)))
				this.spawnableMonsterList.add(new SpawnListEntry(mobClass, 1, 1, 1))
			}
		})
		*/
		this.spawnableMonsterList.clear()
		this.spawnableMonsterList.add(new SpawnListEntry(classOf[EntityZombie], 1, 1, 20))

	}

	override def getSpawningChance: Float = 0.75f

	private def alter(original: Int): Int = new Color(original).darker().getRGB

	@SideOnly(Side.CLIENT)
	override def getSkyColorByTemp(temp : Float): Int = alter(super.getSkyColorByTemp(temp))

	override def getWaterColorMultiplier: Int = alter(16777215)

	override def getModdedBiomeFoliageColor(original: Int): Int = alter(original)

	override def getModdedBiomeGrassColor(original: Int): Int = alter(original)

}
