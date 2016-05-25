package temportalist.esotericraft.galvanization.common.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;

/**
 * Created by TheTemportalist on 5/19/2016.
 *
 * @author TheTemportalist
 */
public abstract class EntityAIHelper extends EntityAIBase {

	public void setMutexBits(EnumAIMutex mutex) {
		super.setMutexBits(mutex.getMutexBits());
	}

}
