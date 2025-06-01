package net.fabricmc.projectilepractice.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlMixin extends ThrownItemEntity {

	// counter for ticks after the projectile is thrown
	@Unique
	private int ticks = 0;

	// process server side only to prevent duplicate messages
	@Unique
	private final boolean isClient = this.world.isClient;

	// this just has to be here for it to extend the ThrowableEntity class. Something about abstraction
	public EnderPearlMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
		super(entityType, world);
	}

	// increment tick counter every tick
	@Inject(at = @At("TAIL"), method = "tick")
	public void incrementTickCount(CallbackInfo ci)
	{
		// increment tick counter
		if (!isClient)
		{
			ticks++;
		}
	}

	// when projectile lands
	@Inject(at = @At("HEAD"), method = "onCollision")
	private void printTicksFlown(CallbackInfo ci)
	{
		if (!isClient)
		{
			// send a message in chat to each player
			String msg = String.format("Ender pearl landed after %d ticks at (%.2f, %.2f, %.2f)",
										ticks, this.getX(), this.getY(), this.getZ());
			for (PlayerEntity player : this.world.getPlayers()){
				player.sendMessage(new TranslatableText(msg), false);
			}
		}
	}
}
