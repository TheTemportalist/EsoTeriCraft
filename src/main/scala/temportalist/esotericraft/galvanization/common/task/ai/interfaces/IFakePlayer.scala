package temportalist.esotericraft.galvanization.common.task.ai.interfaces

import com.mojang.authlib.GameProfile
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.{FakePlayer, FakePlayerFactory}

/**
  *
  * Created by TheTemportalist on 5/26/2016.
  *
  * @author TheTemportalist
  */
trait IFakePlayer {

	private val fakePlayerProfile =
		new GameProfile(null, "FakePlayer_" + this.getClass.getSimpleName)

	def getFakePlayer(world: WorldServer): FakePlayer =
		FakePlayerFactory.get(world, this.fakePlayerProfile)

}
