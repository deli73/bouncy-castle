package xyz.sunrose.bouncycastle.mixins;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public class LivingEntityJumpMixin {

	@ModifyArg(method = "jump", index = 1, at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V")
	)
	private double injected(double y) {
		return Math.max(((LivingEntity) (Object) this).getVelocity().y, y);
	}
}
