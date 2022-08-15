package xyz.sunrose.bouncycastle.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.sunrose.bouncycastle.BouncyCastle;
import xyz.sunrose.bouncycastle.EntityVelDuck;
import xyz.sunrose.bouncycastle.SpecialCollisions;

import java.util.ArrayList;


@Mixin(Entity.class)
abstract public class EntityMoveMixin implements EntityVelDuck {
	@Shadow
	abstract Vec3d adjustMovementForCollisions(Vec3d movement);
	@Shadow
	public World world;
	@Shadow
	public abstract float getHeight();
	@Shadow
	public abstract float getWidth();
	@Shadow
	private Vec3d pos;
	@Shadow
	public boolean verticalCollision;

	@Inject(method = "move", at = @At(value = "JUMP", opcode = Opcodes.IFEQ),
			slice = @Slice(
					from = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 3),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;", ordinal = 0)
			))
	public void bouncycastle$specialCollision(MovementType movementType, Vec3d movement, CallbackInfo ci){
		Vec3d collided = this.adjustMovementForCollisions(movement);
		boolean xTouch = !MathHelper.approximatelyEquals(movement.x, collided.x);
		boolean zTouch = !MathHelper.approximatelyEquals(movement.z, collided.z);
		boolean upTouch = this.verticalCollision && movement.y > 0.0; //inverse of the ground check
		ArrayList<Direction> dirs = new ArrayList<>();
		if(xTouch && movement.x > 0.0){
			dirs.add(Direction.EAST);
		}else if (xTouch && movement.x < 0.0) {
			dirs.add(Direction.WEST);
		}
		if (zTouch && movement.z > 0.0) {
			dirs.add(Direction.SOUTH);
		}else if (zTouch && movement.z < 0.0) {
			dirs.add(Direction.NORTH);
		}
		if(upTouch){
			dirs.add(Direction.UP);
		}
		for (Direction dir : dirs){
			BlockPos[] aa = bouncycastle$getPosition(0.2F, dir);
			for (BlockPos blockpos:aa) {
				BlockState block = this.world.getBlockState(blockpos);
				if(block.getBlock() instanceof SpecialCollisions b){
					b.onSpecialCollision( world, ((Entity) (Object)this), dir.getOpposite());
					//BouncyCastle.LOGGER.debug("bouncy?");
					break;
				}
			}
		}
	}

	private BlockPos[] bouncycastle$getPosition(float offset, Direction dir) {
		int height = BouncyCastle.round(this.getHeight());
		int xWidth = BouncyCastle.round(this.pos.x+((double)this.getWidth()/2)) - BouncyCastle.round(this.pos.x-((double)this.getWidth()/2));
		int zWidth = BouncyCastle.round(this.pos.z+((double)this.getWidth()/2)) - BouncyCastle.round(this.pos.z-((double)this.getWidth()/2));
		if (height < 1) height = 1;
		if (xWidth < 1) xWidth = 1;
		if (zWidth < 1) zWidth = 1;
		int yb = MathHelper.floor(this.pos.y);
		switch (dir){
			case SOUTH: {
				int x = MathHelper.floor(this.pos.x);
				int zb = MathHelper.floor(this.pos.z+((double)this.getWidth()/2) + (double)offset);
				BlockPos[] out = new BlockPos[zWidth*height];
				for(int z = 0; z < zWidth; z++){
					for(int y = 0; y < height; y++){
						out[z+(zWidth*y)] = new BlockPos(x, y+yb, z+zb);
					}
				}
				return out;
			}
			case NORTH: {
				int x = BouncyCastle.round(this.pos.x);
				int zb = BouncyCastle.round(this.pos.z-((double)this.getWidth()/2) - (double)offset);
				BlockPos[] out = new BlockPos[zWidth*height];
				for(int z = 0; z < zWidth; z++){
					for(int y = 0; y < height; y++){
						out[z+(zWidth*y)] = new BlockPos(x, y+yb, z+zb);
					}
				}
				return out;
			}
			case WEST: {
				int xb = BouncyCastle.round(this.pos.x-((double)this.getWidth()/2) - (double)offset);
				int z = BouncyCastle.round(this.pos.z);
				BlockPos[] out = new BlockPos[xWidth*height];
				for(int x = 0; x < xWidth; x++){
					for(int y = 0; y < height; y++){
						out[x+(xWidth*y)] = new BlockPos(x+xb, y+yb, z);
					}
				}
				return out;
			}
			case EAST: {
				int xb = BouncyCastle.round(this.pos.x+((double)this.getWidth()/2) + (double)offset);
				int z = BouncyCastle.round(this.pos.z);
				BlockPos[] out = new BlockPos[xWidth*height];
				for(int x = 0; x < xWidth; x++){
					for(int y = 0; y < height; y++){
						out[x+(xWidth*y)] = new BlockPos(x+xb, y+yb, z);
					}
				}
				return out;
			}
			case UP : {
				int y = BouncyCastle.round(this.pos.y + (double)this.getHeight() + (double)offset);
				int xb = BouncyCastle.round(this.pos.x-((double)this.getWidth()/2));
				int zb = BouncyCastle.round(this.pos.z-((double)this.getWidth()/2));
				BlockPos[] out = new BlockPos[xWidth*zWidth];
				for(int x = 0; x < xWidth; x++){
					for(int z = 0; z < zWidth; z++){
						out[x+(xWidth*z)] = new BlockPos(x+xb, y, z+zb);
					}
				}
				return out;
			}
			default: return new BlockPos[0];
		}
	}
	double bouncycastle$xVel = 0.0D;
	double bouncycastle$zVel = 0.0D;

	@Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V", shift = At.Shift.AFTER))
	public void bouncycastle$InjectVelocity(MovementType movementType, Vec3d movement, CallbackInfo ci){
		Vec3d temp = ((Entity)(Object)this).getVelocity();
		double xVel = this.bouncycastle$xVel==0.0?temp.x:this.bouncycastle$xVel;
		double zVel = this.bouncycastle$zVel==0.0?temp.z:this.bouncycastle$zVel;
		((Entity)(Object)this).setVelocity(new Vec3d(xVel,temp.y,zVel));
		this.bouncycastle$xVel = 0D;
		this.bouncycastle$zVel = 0D;
	}
	@Override
	public void bouncycastle$PostHitSetVelocity(Vec3d vel){
		this.bouncycastle$xVel = vel.x;
		this.bouncycastle$zVel = vel.z;
	}
	@Override
	public void bouncycastle$PostHitSetVelocity(double x, double z){
		this.bouncycastle$xVel = x;
		this.bouncycastle$zVel = z;
	}
	@Override
	public void bouncycastle$PostHitSetZVelocity(double z){
		this.bouncycastle$zVel = z;
	}
	@Override
	public void bouncycastle$PostHitSetXVelocity(double x){
		this.bouncycastle$xVel = x;
	}

}
