package net.fabricmc.projectilepractice.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.thrown.ThrowableEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowballEntity.class)
public abstract class SnowballMixin extends ThrowableEntity{

	// counter for ticks after the projectile is thrown
	@Unique
	private int ticks = 0;

	// process server side only to prevent duplicate messages
	@Unique
	private final boolean isClient = this.world.isClient;

	// this just has to be here for it to extend the ThrowableEntity class. Something about abstraction
	public SnowballMixin(World world) {
		super(world);
	}

	// replace tick method from parent class ThrowableEntity
	@Override
	public void tick()
	{
		// do everything ThrowableEntity.tick() does
		super.tick();

		// increment tick counter
		if (!isClient)
		{
			ticks++;
		}
	}

	@Inject(at = @At("HEAD"), method = "onCollision")
	private void printTicksFlown(BlockHitResult result, CallbackInfo ci)
	{
		if (!isClient)
		{
			// send a message in chat to each player
			String msg = String.format("Snowball landed after %d ticks at (%.2f, %.2f, %.2f)",
					ticks, this.x, this.y, this.z);
			for (PlayerEntity player : this.world.playerEntities){
				player.sendMessage(new TranslatableText(msg));
			}
		}
	}
}
