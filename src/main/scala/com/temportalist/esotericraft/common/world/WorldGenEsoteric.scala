package com.temportalist.esotericraft.common.world

import java.util.Random

import com.temportalist.esotericraft.common.EsoTeriCraft
import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.api.common.utility.{NBTHelper, MathFuncs}
import net.minecraft.init.Blocks
import net.minecraft.nbt.{NBTTagList, NBTTagCompound}
import net.minecraft.util.BlockPos
import net.minecraft.world.{ChunkCoordIntPair, World}
import net.minecraft.world.chunk.IChunkProvider
import net.minecraftforge.fml.common.IWorldGenerator

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by TheTemportalist on 1/3/2016.
  */
object WorldGenEsoteric extends IWorldGenerator {

	private val esotericChunks = mutable.Map[ChunkCoordIntPair, Boolean]()

	private val biomeArray = Array.fill(256)(WorldEsoteric.biomeEsoteric.biomeID.toByte)

	override def generate(random: Random, chunkX: Int, chunkZ: Int, world: World,
			chunkGenerator: IChunkProvider, chunkProvider: IChunkProvider): Unit = {
		println("trying to gen 1")
		if (world.provider.getDimensionId != 0) return
		val worldData = EsotericWorldData.forWorld(world)

		println("trying to gen 2")
		val pos = new ChunkCoordIntPair(chunkX, chunkZ)
		val chunk_and_distance = this.getClosestChunk(pos)
		if (chunk_and_distance._2 >= 0 &&
				(chunk_and_distance._2 < 200 || random.nextDouble() < 0.5)) return

		this.convertChunkBiome(world, pos)
		val hasNexus = !this.hasNexusWithin(pos, 5000) && random.nextDouble() < 0.8 &&
				this.spawnNexus(world, pos, random)
		this.esotericChunks(pos) = hasNexus
		EsoTeriCraft.log("Generated esoteric biome at world pos " +
				pos.getCenterXPos + " x " + pos.getCenterZPosition +
				(if (hasNexus) " with " else " without ") + "a nexus.")
		worldData.markDirty()

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
			var distanceToPossible_smallest = 0D
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

	private def convertChunkBiome(world: World, pos: ChunkCoordIntPair): Unit = {
		world.getChunkFromChunkCoords(
			pos.getXStart, pos.getZStart).setBiomeArray(this.biomeArray)
	}

	private def hasNexusWithin(pos: ChunkCoordIntPair, distance: Double): Boolean = {
		if (this.esotericChunks.isEmpty) return false
		else this.iterateEsotericChunks(pos, set => if (set._2 <= distance && set._3) return true)
		false
	}

	private def spawnNexus(world: World, pos: ChunkCoordIntPair, rand: Random): Boolean = {
		val nexusSize = 7
		//val nexusHeight = 5
		//val centerOffset = 4

		val sideBuffer = 16 - nexusSize
		val start = new V3O(rand.nextInt(sideBuffer), 0, rand.nextInt(sideBuffer))
		//val center = start + new V3O(centerOffset).suppressedYAxis()

		val tallestPos = this.getTallestHeightInArea(world, start, nexusSize)
		if (tallestPos < 0) return false
		this.generatePlatform(world, start, tallestPos, nexusSize)
		StructureNexus.generate(world, start + tallestPos + V3O.UP)
	}

	private def getTallestHeightInArea(world: World, startXZ: V3O, size: Int): Int = {
		(for {x <- startXZ.x_i() to startXZ.x_i() + size
		      z <- startXZ.z_i() to startXZ.z_i() + size}
			yield world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY
		).max
	}

	private def generatePlatform(world: World, start: V3O, top: Int, size: Int): Unit = {
		val platformState = Blocks.cobblestone.getBlockState.getBaseState // Cobble
		val legState = Blocks.log.getBlockState.getBaseState // Oak Log

		val topPos = start + V3O.UP * top
		val topMaterial = world.getBlockState(topPos.toBlockPos).getBlock.getMaterial
		val stableGround = topMaterial.isSolid && topMaterial.isOpaque
		val y = topPos.y + (if (stableGround) 0 else 1)

		val minX = start.x_i()
		val maxX = minX + size
		val minZ = start.z_i()
		val maxZ = minZ + size
		for {x <- minX to maxX
		     z <- minZ to maxZ}
			world.setBlockState(new BlockPos(x, y, z), platformState)

		var yPos = y - 1
		var pos: BlockPos = null
		var didAddLeg = false
		do {
			pos = new BlockPos(minX, yPos, minZ)
			didAddLeg = didAddLeg || world.setBlockState(pos, legState)
			pos = new BlockPos(minX, yPos, maxZ)
			didAddLeg = didAddLeg || world.setBlockState(pos, legState)
			pos = new BlockPos(maxX, yPos, minZ)
			didAddLeg = didAddLeg || world.setBlockState(pos, legState)
			pos = new BlockPos(maxX, yPos, maxZ)
			didAddLeg = didAddLeg || world.setBlockState(pos, legState)
			yPos -= 1
		} while (didAddLeg)

	}

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
