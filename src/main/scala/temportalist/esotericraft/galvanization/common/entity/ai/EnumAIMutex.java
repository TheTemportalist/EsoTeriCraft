package temportalist.esotericraft.galvanization.common.entity.ai;

/**
 * Created by TheTemportalist on 5/19/2016.
 *
 * @author TheTemportalist
 */
public enum EnumAIMutex {

	SWIMMING_WATCHING(1),
	SWIMMING_NOT_WATCHING(3),
	WATCHING_NOT_SWIMMING(5),
	NOT_VANILLA(7),
	EVERYTHING_OKAY(8);

	private final int mutex;

	EnumAIMutex(int mutexBits) {
		this.mutex = mutexBits;
	}

	public int getMutexBits() {
		return mutex;
	}

}
