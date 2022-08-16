package xyz.sunrose.bouncycastle.mixins;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityJumpMixin {

	@ModifyVariable(method = "jump", at = @At("STORE"), ordinal = 0)
	private double injected(double y) {
		return Math.max(((LivingEntity) (Object) this).getVelocity().y, y);
	}
}
