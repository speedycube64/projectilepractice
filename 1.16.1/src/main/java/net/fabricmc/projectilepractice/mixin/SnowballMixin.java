package net.fabricmc.projectilepractice.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowballEntity.class)
public abstract class SnowballMixin extends ThrownItemEntity {

	// counter for ticks after the projectile is thrown
	@Unique
	private int ticks = 0;

	// process server side only to prevent duplicate messages
	@Unique
	private final boolean isClient = this.world.isClient;

	// this just has to be here for it to extend the ThrowableEntity class. Something about abstraction
	public SnowballMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
		super(entityType, world);
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

	// when projectile lands
	@Inject(at = @At("HEAD"), method = "onCollision")
	private void printTicksFlown(CallbackInfo ci)
	{
		if (!isClient)
		{
			// send a message in chat to each player
			String msg = String.format("Snowball landed after %d ticks at (%.2f, %.2f, %.2f)",
					ticks, this.getX(), this.getY(), this.getZ());
			System.out.println("snowball landed");
			for (PlayerEntity player : this.world.getPlayers()){
				player.sendMessage(new TranslatableText(msg), false);
			}
		}
	}
}
