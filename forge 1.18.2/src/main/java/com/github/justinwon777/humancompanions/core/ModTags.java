package com.github.justinwon777.humancompanions.core;
import com.github.justinwon777.humancompanions.HumanCompanions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;


public class ModTags {
	public static final class Items {
		public static final TagKey<Item> AXE_WEAPONS = ItemTags.create(new ResourceLocation(HumanCompanions.MOD_ID,"axeweapons"));
		public static final TagKey<Item> SWORD_WEAPONS = ItemTags.create(new ResourceLocation(HumanCompanions.MOD_ID,"swordweapons"));
		
		
	}
}
