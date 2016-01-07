package com.temportalist.esotericraft.common.world

import java.util.Random

import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.api.common.utility.{MathFuncs, NBTHelper}
import net.minecraft.block.material.MaterialLogic
import net.minecraft.init.Blocks
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.biome.{BiomeGenRiver, BiomeGenOcean, BiomeGenBase}
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.{ChunkCoordIntPair, World}
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.IWorldGenerator
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by TheTemportalist on 1/3/2016.
  */
object WorldGenEsoteric extends IWorldGenerator {

	private val esotericChunks = mutable.Map[ChunkCoordIntPair, Boolean]()

	private val biomeArray = Array[Array[Byte]] (
		Array.fill(256)(WorldEsoteric.biomeEsoteric.biomeID.toByte),
		Array.fill(256)(WorldEsoteric.biomeUmbra.biomeID.toByte)
	)

	override def generate(random: Random, chunkX: Int, chunkZ: Int, world: World,
			chunkGenerator: IChunkProvider, chunkProvider: IChunkProvider): Unit = {
		if (world.provider.getDimensionId != 0) return

		val pos = new ChunkCoordIntPair(chunkX, chunkZ)

		/*
		if (random.nextDouble() < 0.16) { // chance to spawn umbra
			this.convertChunkBiome(world, pos, 1)
			return
		}
		*/

		val chunk_and_distance = this.getClosestChunk(pos)
		val hasClosest = this.esotericChunks.nonEmpty
		val isWithinMin = chunk_and_distance._2 < 500 // chance to spawn esoteric
		val d = random.nextDouble()
		val shouldGen = if (hasClosest) !isWithinMin && d < 0.5 else d < 0.5
		if (!shouldGen) return

		val old_biome: BiomeGenBase = this.getAverageBiome(world, pos)
		this.convertChunkBiome(world, pos)

		// chance to spawn nexus & (5000 << 4 = block range)
		val hasNexus = !old_biome.isInstanceOf[BiomeGenOcean] && !old_biome.isInstanceOf[BiomeGenRiver] &&
				!this.hasNexusWithin(pos, 5000 << 4) && random.nextDouble() < 0.6 &&
				this.spawnNexus(world, pos, random)
		this.esotericChunks(pos) = hasNexus
		/*
		EsoTeriCraft.log("Generated esoteric biome at world pos " +
				pos.getCenterXPos + " x " + pos.getCenterZPosition +
				(if (hasNexus) " with " else " without ") + "a nexus.")
		*/

		EsotericWorldData.forWorld(world).markDirty()

	}

	private def getAverageBiome(world: World, pos: ChunkCoordIntPair): BiomeGenBase = {
		val chunk = world.getChunkFromChunkCoords(pos.chunkXPos, pos.chunkZPos)
		val sumBiomeList = mutable.Map[BiomeGenBase, Int]()
		chunk.getBiomeArray.foreach(biomeID => {
			if ((biomeID & 255) < 255) {
				val biome = BiomeGenBase.getBiome(biomeID & 255)
				sumBiomeList(biome) = sumBiomeList.getOrElse(biome, 0) + 1
			}
		})
		sumBiomeList.toSeq.sortWith(_._2 > _._2).head._1
	}

	private def iterateEsotericChunks[U](pos: ChunkCoordIntPair,
			f: ((ChunkCoordIntPair, Double, Boolean)) => U): Unit = {
		this.esotericChunks.foreach(esotericChunk_and_hasNexus => {
			val chunk = esotericChunk_and_hasNexus._1
			f(chunk, this.getDistance(pos, chunk), esotericChunk_and_hasNexus._2)
		})
	}

	private def getClosestChunk(
			possiblePos: ChunkCoordIntPair): (ListBuffer[ChunkCoordIntPair], Double) = {
		if (this.esotericChunks.isEmpty) (null, -1)
		else {
			var distanceToPossible_smallest = Double.MaxValue
			var closest = ListBuffer[ChunkCoordIntPair]()
			this.iterateEsotericChunks(possiblePos, set => {
				if (set._2 < distanceToPossible_smallest) {
					distanceToPossible_smallest = set._2
					closest.clear()
					closest += set._1
				}
				else if (set._2 == distanceToPossible_smallest) {
					closest += set._1
				}
			})
			(closest, distanceToPossible_smallest)
		}
	}

	private def getDistance(a: ChunkCoordIntPair, b: ChunkCoordIntPair): Double =
		MathFuncs.distance(a.getCenterXPos, a.getCenterZPosition,
			b.getCenterXPos, b.getCenterZPosition)

	private def convertChunkBiome(world: World, pos: ChunkCoordIntPair, typeBiome: Int = 0): Unit = {
		val chunk = world.getChunkFromChunkCoords(pos.chunkXPos, pos.chunkZPos)
		chunk.setBiomeArray(this.biomeArray(typeBiome))
		chunk.setChunkModified()
	}

	private def hasNexusWithin(pos: ChunkCoordIntPair, distance: Double): Boolean = {
		if (this.esotericChunks.isEmpty) return false
		else this.iterateEsotericChunks(pos, set => if (set._3  && set._2 <= distance) return true)
		false
	}

	private def spawnNexus(world: World, pos: ChunkCoordIntPair, rand: Random): Boolean = {
		val nexusSize = 7
		val nexusHeight = 5

		val sideBuffer = 16 - nexusSize
		val x = rand.nextInt(sideBuffer) + pos.getXStart
		val z = rand.nextInt(sideBuffer) + pos.getZStart
		val start = new V3O(this.getTopSolidOrLiquidBlock(world, x, z)) + V3O.UP

		/* Would like to find average of area (and remove outliers to do so),
		but for now, start point Y will do

		val yPos = this.getLowestBlockHeightInArea(world, start, nexusSize)
		if (yPos < 0 || yPos > 256) return false
		 */

		// puts a specific block state in where a fluid would have been
		this.solidifyWalls(world, start, nexusSize, nexusHeight)
		// clears the area, excluding first layer
		this.clearArea(world, start, nexusSize, nexusHeight)
		this.generatePlatform(world, start, nexusSize)
		// todo WHY SUBTRACT SINGLE
		StructureNexus.generate(world, start - V3O.SINGLE)
	}

	def getTopSolidOrLiquidBlock(world: World, x: Int, z: Int): BlockPos = {
		var pos = new BlockPos(x, 0, z)
		val chunk = world.getChunkFromBlockCoords(pos)
		pos = new BlockPos(x, chunk.getTopFilledSegment + 16, z)

		var foundTopBlock = false
		do {
			pos = pos.down()
			val block = chunk.getBlock(pos)
			// is solid (non liquid)
			if (block.getMaterial.blocksMovement())
				foundTopBlock = !block.isLeaves(world, pos) && !block.isFoliage(world, pos) &&
						!block.getMaterial.isInstanceOf[MaterialLogic]
			else foundTopBlock = !block.isAir(world, pos)
		} while (!foundTopBlock && pos.getY >= 0)

		pos
	}

	/*
	private def getLowestBlockHeightInArea(world: World, startXZ: V3O, sideLength: Int): Int = {
		val heights = new Array[Int](256)
		var averageHeight: Int = 0

		{
			var sum: Int = 0
			def indexOf(x: Int, z: Int): Int = (z & 15) << 4 | (x & 15)
			// Fill the heights map & find average
			for {x <- startXZ.x_i() to startXZ.x_i() + sideLength
			     z <- startXZ.z_i() to startXZ.z_i() + sideLength} {
				heights(indexOf(x, z)) = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY
				sum += heights(indexOf(x, z))
			}
			// determine average
			averageHeight = sum / heights.length
		}
		val distanceFromAverage = heights.clone()
		for (i <- distanceFromAverage.indices)
			distanceFromAverage(i) = Math.abs(averageHeight - distanceFromAverage(i))


		0
	}
*/

	private def solidifyWalls(world: World, start: V3O, sideLength: Int,  height: Int): Unit = {
		val platformState = Blocks.grass.getBlockState.getBaseState // grass
		val wallState = Blocks.cobblestone.getBlockState.getBaseState // Cobble
		def solidify(x: Int, y: Int, z: Int, isFloor: Boolean = false): Unit = {
			val pos = (start + (x, y, z)).toBlockPos
			val block = world.getBlockState(pos).getBlock
			val doesMove = !block.getMaterial.blocksMovement()
			val isAir = world.isAirBlock(pos) || block.getMaterial.isInstanceOf[MaterialLogic]
			val gen =
				if (!isFloor) !isAir && doesMove
				else isAir || doesMove
			if (gen)
				world.setBlockState(pos, if (isFloor) platformState else wallState)
		}
		def solidifyAxis(axis: EnumFacing.Axis, a: Int, b: Int): Unit = {
			axis match {
				case EnumFacing.Axis.Y =>
					solidify(a, -1, b, isFloor = true)
					solidify(a, height, b)
				case EnumFacing.Axis.Z =>
					solidify(a, b, -1)
					solidify(a, b, sideLength)
				case EnumFacing.Axis.X =>
					solidify(-1, a, b)
					solidify(sideLength, a, b)
				case _ =>
			}
		}
		// Y Axis
		for {x <- -1 to sideLength
		     z <- -1 to sideLength} solidifyAxis(EnumFacing.Axis.Y, x, z)
		// Z Axis
		for {x <- -1 to sideLength
		     y <- -1 to height} solidifyAxis(EnumFacing.Axis.Z, x, y)
		// X Axis
		for {y <- -1 to height
		     z <- -1 to sideLength} solidifyAxis(EnumFacing.Axis.X, y, z)
	}

	private def clearArea(world: World, start: V3O, sideLength: Int, height: Int): Unit = {
		for {x <- 0 until sideLength
		     z <- 0 until sideLength
		     y <- 0 until height}
			world.setBlockToAir((start + (x, y, z)).toBlockPos)
	}

	private def generatePlatform(world: World, start: V3O, sideLength: Int): Unit = {
		val dirtState = Blocks.dirt.getBlockState.getBaseState // Cobble
		var y = start.y_i() - 2
		var didAddLeg = false
		do {
			didAddLeg = false
			for (x <- start.x_i() - 1 to start.x_i() + sideLength) {
				for (z <- start.z_i() - 1 to start.z_i() + sideLength) {
					val pos = new BlockPos(x, y, z)
					if (world.isAirBlock(pos)) {
						didAddLeg = world.setBlockState(pos, dirtState) || didAddLeg
					}
				}
			}
			y -= 1
		} while (didAddLeg)
	}

	@SubscribeEvent
	def worldUnload(event: WorldEvent.Unload): Unit = this.esotericChunks.clear()

	def writeEsotericChunks(nbt: NBTTagCompound): Unit = {
		val nbtBiomeNexus = new NBTTagList
		this.esotericChunks.foreach(chunk_and_nexus => {
			val tag = new NBTTagCompound
			tag.setInteger("chunkX", chunk_and_nexus._1.chunkXPos)
			tag.setInteger("chunkZ", chunk_and_nexus._1.chunkZPos)
			tag.setBoolean("hasNexus", chunk_and_nexus._2)
			nbtBiomeNexus.appendTag(tag)
		})
		nbt.setTag("biomeAndNexus", nbtBiomeNexus)
	}

	def readEsotericChunks(nbt: NBTTagCompound): Unit = {
		this.esotericChunks.clear()
		val nbtBiomeNexus = nbt.getTagList("biomeAndNexus", NBTHelper.getNBTType[NBTTagCompound])
		for (i <- 0 until nbtBiomeNexus.tagCount()) {
			val tag = nbtBiomeNexus.getCompoundTagAt(i)
			this.esotericChunks(new ChunkCoordIntPair(
				tag.getInteger("chunkX"), tag.getInteger("chunkZ"))
			) = tag.getBoolean("hasNexus")
		}
	}

}
