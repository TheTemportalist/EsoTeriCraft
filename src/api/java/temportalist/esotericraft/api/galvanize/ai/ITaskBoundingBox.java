package temportalist.esotericraft.api.galvanize.ai;

import net.minecraft.util.math.AxisAlignedBB;

/**
 * Created by TheTemportalist on 5/26/2016.
 *
 * @author TheTemportalist
 */
public interface ITaskBoundingBox extends IGalvanizeTask {

	void updateBoundingBox();

	AxisAlignedBB getBoundingBox();

}
