package temportalist.esotericraft.api;

/**
 * Created by TheTemportalist on 1/12/2016.
 */
public class Spell {

	private final String name;

	protected Spell(String name) {
		this.name = name;
		ApiEsotericraft.Spells.register(this);
	}

	public final String getName() {
		return this.name;
	}

	public final int getGlobalID() {
		return ApiEsotericraft.Spells.getGlobalID(this);
	}

}
