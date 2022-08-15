package xyz.sunrose.bouncycastle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import xyz.sunrose.bouncycastle.mixins.AccessorLivingEntity;

public class BouncyBlock extends Block implements specialCollisions{
	private static final double BOUNCE_DAMPENING_FACTOR = 0.7;
	private static final double BOUNCE_AMPLIFYING_FACTOR = 1.12;
	private static final double MAX_BOUNCE_SPEED = 0.312; //TODO figure out reasonable value for this

	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.5D, 0.5D, 0.5D, 15.5D, 15.5D, 15.5D);

	public BouncyBlock(Settings settings) {
		super(settings);
	}

	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}


	@Override
	public void onEntityLand(BlockView world, Entity entity) {
		if (entity.bypassesLandingEffects()) {
			super.onEntityLand(world, entity);
		} else {
			this.bouncy(entity, Direction.UP);
		}

	}
	public void bouncy(Entity entity, Direction dir) { //TODO make this work literally at all
		BouncyCastle.LOGGER.debug("TEST");
		boolean amplifying = false;
		if(entity instanceof LivingEntity e) {
			AccessorLivingEntity access = (AccessorLivingEntity) e;
			amplifying = access.bouncycastle$getJumping();
		}
		Vec3d velocity = entity.getVelocity();

		double bounceFactor = amplifying ? BOUNCE_AMPLIFYING_FACTOR : BOUNCE_DAMPENING_FACTOR;

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
	}

	@Override
	public void OnSpecialCollision(BlockView world, Entity entity, Direction dir) {
		this.bouncy(entity, dir);
	}
}
