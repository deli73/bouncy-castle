package xyz.sunrose.bouncycastle;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
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
					Registries.BLOCK, ID,
					new BouncyBlock(QuiltBlockSettings.of(Material.WOOL).sounds(BlockSoundGroup.WOOL).mapColor(color))
			);
			final Item ITEM = Registry.register(
					Registries.ITEM, ID,
					new BlockItem(BLOCK, new Item.Settings())
			);
			BOUNCY_BLOCKS.add(BLOCK);
			BOUNCY_BLOCK_ITEMS.add(ITEM);
		}

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COLORED_BLOCKS).register((content) -> {
			for(Item item : BOUNCY_BLOCK_ITEMS) {
				content.addItem(item);
			}
		});
	}

	public static int round(double num){
		return num >0.5D? MathHelper.ceil(num) : MathHelper.floor(num);
	}
}
