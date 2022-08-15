package xyz.sunrose.bouncycastle;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public interface SpecialCollisions {

	void onSpecialCollision(BlockView world, Entity entity, Direction dir);

}
