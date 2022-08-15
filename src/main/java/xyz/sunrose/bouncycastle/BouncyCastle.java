package xyz.sunrose.bouncycastle;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BouncyCastle implements ModInitializer {
	public static final String MODID = "bouncycastle";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static List<Block> BOUNCY_BLOCKS = new ArrayList<>();
	public static List<Item> BOUNCY_BLOCK_ITEMS = new ArrayList<>();

	@Override
	public void onInitialize(ModContainer mod) {
		for(DyeColor color : DyeColor.values()){
			String name = color.asString()+"_bouncy_block";
			Identifier ID = new Identifier(MODID, name);
			final Block BLOCK = Registry.register(
					Registry.BLOCK, ID,
					new BouncyBlock(QuiltBlockSettings.of(Material.WOOL))
			);
			final Item ITEM = Registry.register(
					Registry.ITEM, ID,
					new BlockItem(BLOCK, new QuiltItemSettings().group(ItemGroup.DECORATIONS))
			);
			BOUNCY_BLOCKS.add(BLOCK);
			BOUNCY_BLOCK_ITEMS.add(ITEM);
		}
	}

	public static int round(double num){
		return num >0.5D? MathHelper.ceil(num) : MathHelper.floor(num);
	}
}
