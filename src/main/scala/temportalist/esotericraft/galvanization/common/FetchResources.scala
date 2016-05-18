package temportalist.esotericraft.galvanization.common

import java.io.{InputStreamReader, Reader}
import java.net.URL
import java.util

import com.google.gson.Gson
import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTBase
import temportalist.esotericraft.api.galvanize.IAbility

import scala.collection.mutable.ListBuffer
import scala.collection.{JavaConversions, mutable}

/**
  *
  * Created by TheTemportalist on 5/17/2016.
  *
  * @author TheTemportalist
  */
object FetchResources {

	val GSON = new Gson

	def runMorph(): Unit = {
		val sitePrefix = "https://raw.github.com/iChun/Morph/1.8/src/main/resources/assets/morph/mod/"
		new FetchJson("Morph", sitePrefix + "AbilitySupport.json").start()
	}

	def runGalvanize(): Unit = {

	}

	class FetchJson(name: String, private val url: String) extends Thread {

		this.setName("Thread Fetch Resources (" + name + ")")
		this.setDaemon(true)

		override def run(): Unit = {
			val fileIn: Reader = new InputStreamReader(new URL(this.url).openStream())
			val jsonMappings = GSON.fromJson(fileIn, classOf[java.util.Map[String, Array[String]]])
			val mappings: mutable.Map[String, Array[String]] = JavaConversions.mapAsScalaMap(jsonMappings)
			fileIn.close()
			if (mappings != null) for (entry <- mappings) {
				var classKey: Class[_] = null
				try {
					classKey = Class.forName(entry._1)
				}
				catch {
					case e: Exception =>
						//Galvanize.log("[Abilities] Entity class \'" + entry._1 + "\' not found. Skipping.")
				}
				if (classKey != null) {
					if (!classOf[EntityLivingBase].isAssignableFrom(classKey)) {
						Galvanize.log("[Abilities] Entity class in ability mappings named " +
								entry._1 + " is not a subclass of " +
								classOf[EntityLivingBase].getCanonicalName + ". Skipping.")
					}
					else {
						var abilities = ListBuffer[IAbility[_ <: NBTBase]]()

						try {
							var properties: Iterable[String] = null
							entry._2.asInstanceOf[AnyRef] match {
								case arr: Array[String] => properties = arr
								case collection: util.Collection[_] =>
									properties = JavaConversions.collectionAsScalaIterable(collection).asInstanceOf[Iterable[String]]
								case _ =>
									Galvanize.log("[Abilities] Error, object not calculated from json " + entry._2.getClass.getCanonicalName)
							}
							for (property <- properties) {
								var abilityName = property
								var argStrings = ""

								if (property.contains('|')) {
									val parts = property.split('|')
									abilityName = parts(0)
									argStrings = parts(1)
								}

								Galvanize.createAbility(abilityName, argStrings, property) match {
									case ability: IAbility[_] => abilities += ability.asInstanceOf[IAbility[_ <: NBTBase]]
									case _ =>
								}

							}
						}
						catch {
							case e: Exception => e.printStackTrace()
						}

						this.addAbilityMapping(
							classKey.asInstanceOf[Class[_ <: EntityLivingBase]],
							abilities.toArray
						)
					}
				}
			}
			Galvanize.log("Finished " + this.getName)
		}

		def addAbilityMapping(classKey: Class[_ <: EntityLivingBase], abilities: Array[IAbility[_ <: NBTBase]]): Unit = {
			Galvanize.MAP_CLASS_to_ABILITIES.put(classKey, abilities)
		}

	}

}
