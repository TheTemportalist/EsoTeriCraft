package temportalist.esotericraft.common.world

import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import temportalist.origin.api.common.lib.V3O
import temportalist.origin.foundation.common.register.Register

import scala.collection.mutable.ListBuffer

/**
  * Created by TheTemportalist on 1/3/2016.
  */
abstract class WorldStructure(width: Int, height: Int, depth: Int) extends Register.Post {

	private val size = new V3O(width, height, depth)
	private val blockStatePositions = ListBuffer[(IBlockState, V3O, Int)]()

	protected final def addBlockState(state: IBlockState,
			offsetFromOrigin: V3O, flag: Int = 3): Unit = {
		this.blockStatePositions += ((state, offsetFromOrigin, flag))
	}

	final def getWidth: Int = this.size.x_i()

	final def getHeight: Int = this.size.y_i()

	final def getDepth: Int = this.size.z_i()

	protected final def iterateOverStates[U](f: ((IBlockState, V3O, Int)) => U): Unit = {
		this.blockStatePositions.foreach(set => f(set._1, set._2, set._3))
	}

	/**
	  * Generate the structure
	  * @param origin West, Down, North (-x, -y, -z)
	  */
	def generate(world: World, origin: BlockPos): Boolean = {
		this.generate(world, new V3O(origin))
	}

	def generate(world: World, origin: V3O): Boolean = {
		if (origin.y_i() + this.getHeight > world.provider.getHeight) return false
		this.iterateOverStates(set => {
			world.setBlockState((set._2 + origin).toBlockPos, set._1, set._3)
		})
		//println("done")
		true
	}

}
