package temportalist.esotericraft.api;

import temportalist.esotericraft.common.tile.TENexusCrystal;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by TheTemportalist on 1/4/2016.
 */
public class EsotericraftModule {

	public final int getID() {
		return ApiEsotericraft.getID(this);
	}

	public boolean onImpartingStarted(EntityPlayer player, TENexusCrystal nexusTile) {
		return true;
	}

	public void onImpartingInterrupted(EntityPlayer player, TENexusCrystal nexusTile,
			float percentageDone) {}

	public boolean onImpartingFinished(EntityPlayer player, TENexusCrystal nexusTile) {
		return true;
	}

}
