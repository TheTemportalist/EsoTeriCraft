package temportalist.esotericraft.sorcery.common.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import temportalist.esotericraft.api.sorcery.ISorceryPlayer;
import temportalist.origin.foundation.common.IModPlugin;
import temportalist.origin.foundation.common.capability.ExtendedHandler;
import temportalist.origin.foundation.common.network.PacketExtendedSync;

/**
 * Created by TheTemportalist on 5/6/2016.
 *
 * @author TheTemportalist
 */
public class HandlerSorceryPlayer
		extends ExtendedHandler.ExtendedEntity<NBTTagCompound, ISorceryPlayer, SorceryPlayer, EntityPlayer> {

	public static HandlerSorceryPlayer INSTANCE = null;
	@CapabilityInject(ISorceryPlayer.class)
	public static Capability<ISorceryPlayer> CAPABILITY = null;

	public static void init(IModPlugin mod) {
		HandlerSorceryPlayer.INSTANCE = new HandlerSorceryPlayer(mod);
	}

	private HandlerSorceryPlayer(IModPlugin mod) {
		super(ISorceryPlayer.class, SorceryPlayer.class, EntityPlayer.class);
		this.init(mod, "SorceryPlayer");
	}

	@Override
	public Capability<ISorceryPlayer> getCapabilityObject() {
		return HandlerSorceryPlayer.CAPABILITY;
	}

	@Override
	public boolean isValid(ICapabilityProvider e) {
		return e instanceof EntityPlayer;
	}

	@Override
	public EntityPlayer cast(ICapabilityProvider e) {
		return (EntityPlayer)e;
	}

	@Override
	public ISorceryPlayer getDefaultImplementation() {
		return null;
	}

	@Override
	public ISorceryPlayer getNewImplementation(EntityPlayer obj) {
		return new SorceryPlayer(obj);
	}

	@Override
	public Class<? extends PacketExtendedSync.Handler> getPacketHandlingClass() {
		return Handler.class;
	}

	public static class Handler extends PacketExtendedSync.Handler {

		public Handler() {
			super();
		}

		@Override
		public void deserialize(Entity entity, NBTTagCompound nbt) {
			entity.getCapability(CAPABILITY, null).deserializeNBT(nbt);
		}

	}

}
