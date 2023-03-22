package xyz.sunrose.bouncycastle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import xyz.sunrose.bouncycastle.mixins.AccessorLivingEntity;

public class BouncyBlock extends Block implements SpecialCollisions {
	private static final double BOUNCE_DAMPENING_FACTOR = 0.95;
	private static final double SNEAK_AMPLIFYING_FACTOR = 0.7;
	private static final double BOUNCE_AMPLIFYING_FACTOR = 2;
	private static final double MAX_BOUNCE_SPEED = 0.7; //TODO figure out reasonable value for this
	private static final double SOUND_SPEED_THRESHOLD = 0.1;
	private static final double MAX_SOUND_VELOCITY = 2;

	public BouncyBlock(Settings settings) {
		super(settings);
	}


	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (entity.bypassesLandingEffects()) {
			super.onLandedUpon(world, state, pos, entity, fallDistance);
		} else {
			entity.handleFallDamage(fallDistance, 0.0F, entity.getDamageSources().fall());
		}

	}
	
	@Override
	public void onEntityLand(BlockView world, Entity entity) {
		if (entity.bypassesLandingEffects()) {
			super.onEntityLand(world, entity);
		} else {
			this.bouncy(world, entity, Direction.UP);
		}

	}
	public void bouncy(BlockView world, Entity entity, Direction dir) {
		boolean amplifying = false;
		if(entity instanceof LivingEntity e) {
			AccessorLivingEntity access = (AccessorLivingEntity) e;
			amplifying = access.bouncycastle$getJumping();
		}
		Vec3d velocity = entity.getVelocity();

		// multiply the bounce to amplify if jumping or dampen if not; dampen way more if sneaking
		double bounceFactor = amplifying ? BOUNCE_AMPLIFYING_FACTOR : BOUNCE_DAMPENING_FACTOR;
		bounceFactor *= entity.isSneaking() ? SNEAK_AMPLIFYING_FACTOR : 1;

		if (dir == Direction.DOWN || dir == Direction.UP){
			entity.setVelocity(velocity.x, Math.min(-velocity.y * bounceFactor, MAX_BOUNCE_SPEED), velocity.z);
		}
		if (dir == Direction.NORTH || dir == Direction.SOUTH){ // reverse Z
			((EntityVelDuck)entity).bouncycastle$PostHitSetZVelocity(Math.min(-velocity.z * bounceFactor, MAX_BOUNCE_SPEED));
		}
		if (dir == Direction.EAST || dir == Direction.WEST){ //reverse X
			((EntityVelDuck)entity).bouncycastle$PostHitSetXVelocity(Math.min(-velocity.x * bounceFactor,MAX_BOUNCE_SPEED));
		}
		entity.velocityDirty = true;


		// if we're bouncing hard enough, play a sound.
		// otherwise don't, cuz constant sounds while ur just sitting on a block is weird
		if(Math.abs(entity.getVelocity().getComponentAlongAxis(dir.getAxis())) > SOUND_SPEED_THRESHOLD) {
			playBounceSound(entity, bounceFactor * Math.min(1, entity.getVelocity().length() / MAX_SOUND_VELOCITY));
		}
	}

	@Override
	public void onSpecialCollision(BlockView world, Entity entity, Direction dir) {
		this.bouncy(world, entity, dir);
	}

	private void playBounceSound(Entity entity, double soundFactor){
		World world = entity.getWorld();
		entity.playSound(SoundEvents.BLOCK_SLIME_BLOCK_FALL,
				0.45f * (float) soundFactor,
				(float) (0.98 + world.random.nextGaussian() * 0.04)
		); //add a sound to the bounce... todo custom sounds?
		// TODO figure out why boats are silent?
	}
}
