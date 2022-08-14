package xyz.sunrose.bouncycastle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import xyz.sunrose.bouncycastle.mixins.AccessorLivingEntity;

public class BouncyBlock extends Block {
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
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) { //TODO make this work literally at all
		BouncyCastle.LOGGER.debug("TEST");
		Vec3d entityPos = entity.getPos();
		Direction collisionSide = getCollisionSide(pos, entityPos);
		boolean amplifying = false;
		if(entity instanceof LivingEntity e) {
			AccessorLivingEntity access = (AccessorLivingEntity) e;
			amplifying = access.bouncycastle$getJumping();
		}
		Vec3d velocity = entity.getVelocity();

		double bounceFactor = amplifying ? BOUNCE_AMPLIFYING_FACTOR : BOUNCE_DAMPENING_FACTOR;

		if (collisionSide == Direction.DOWN || collisionSide == Direction.UP){
			entity.setVelocity(velocity.x, Math.min(-velocity.y * bounceFactor, MAX_BOUNCE_SPEED), velocity.z);
		}
		else if (collisionSide == Direction.NORTH || collisionSide == Direction.SOUTH){ // reverse Z
			entity.setVelocity(velocity.x, velocity.y, Math.min(-velocity.z * bounceFactor, MAX_BOUNCE_SPEED));
		}
		else if (collisionSide == Direction.EAST || collisionSide == Direction.WEST){ //reverse X
			entity.setVelocity(Math.min(-velocity.x * bounceFactor,MAX_BOUNCE_SPEED), velocity.y, velocity.z);
		}
		entity.velocityDirty = true;
	}

	private Direction getCollisionSide(BlockPos blockPos, Vec3d entityPos){
		//get position relative to center of block
		double relX = entityPos.getX() - blockPos.getX() - 0.5;
		double relY = entityPos.getY() - blockPos.getY() - 0.5;
		double relZ = entityPos.getZ() - blockPos.getZ() - 0.5;
		//also get the absolute values for comparison purposes
		double absX = MathHelper.sign(relX) * relX;
		double absY = MathHelper.sign(relY) * relY;
		double absZ = MathHelper.sign(relZ) * relZ;

		if (relY >= absX && relY >= absZ){ //if above
			return Direction.UP;
		}
		else if (relY <= absX && relY <= absZ && absX < 0.5 && absZ < 0.5){ //if below
			return Direction.DOWN;
		}
		else if (relX < 0 && absX >= absZ) { //negative X - West
			return Direction.WEST;
		}
		else if (relX >= 0 && absX >= absZ) { //positive X - East
			return Direction.EAST;
		}
		else if (relZ < 0 && absZ > absX) { //negative Z - North
			return Direction.NORTH;
		}
		else if (relZ >= 0 && absZ > absX) { //positive Z - South
			return Direction.SOUTH;
		}
		else{
			BouncyCastle.LOGGER.debug("Invalid collision side, defaulting to top");
			return Direction.UP;
		}

	}
}
