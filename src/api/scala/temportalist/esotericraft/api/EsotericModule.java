package temportalist.esotericraft.api;

import net.minecraft.entity.player.EntityPlayer;

/**
 *
 * Created by TheTemportalist on 4/23/2016.
 * @author TheTemportalist
 */
public abstract class EsotericModule {

	abstract public ModuleTrigger[] createTriggers();

	public final int getID() {
		return ApiEsotericraft.getID(this);
	}

	public boolean onImpartingStarted(EntityPlayer player, ITileNexusCrystal nexusTile) {
		return true;
	}

	public void onImpartingInterrupted(EntityPlayer player, ITileNexusCrystal nexusTile,
			float percentageDone) {}

	public boolean onImpartingFinished(EntityPlayer player, ITileNexusCrystal nexusTile) {
		return true;
	}

	public Object getImpartingReturn(EntityPlayer player, ITileNexusCrystal nexusTile) {
		return null;
	}

}
