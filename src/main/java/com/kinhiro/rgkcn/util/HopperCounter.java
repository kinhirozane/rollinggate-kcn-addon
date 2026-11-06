/*
 * MIT License
 *
 * Copyright (c) 2025 Kinhiro Zane and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.kinhiro.rgkcn.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class HopperCounter {
    private static final ImmutableMap<DyeColor, HopperCounter> COUNTERS = ImmutableMap.<DyeColor, HopperCounter>builder()
        .putAll(
            Arrays.stream(DyeColor.values())
                .collect(Collectors.toUnmodifiableMap(dye -> dye, HopperCounter::new))
        )
        .build();

    private static final ImmutableMap<Block, DyeColor> CHANNELS = ImmutableMap.<Block, DyeColor>builder()
        .put(Blocks.WHITE_WOOL, DyeColor.WHITE)
        .put(Blocks.ORANGE_WOOL, DyeColor.ORANGE)
        .put(Blocks.MAGENTA_WOOL, DyeColor.MAGENTA)
        .put(Blocks.LIGHT_BLUE_WOOL, DyeColor.LIGHT_BLUE)
        .put(Blocks.YELLOW_WOOL, DyeColor.YELLOW)
        .put(Blocks.LIME_WOOL, DyeColor.LIME)
        .put(Blocks.PINK_WOOL, DyeColor.PINK)
        .put(Blocks.GRAY_WOOL, DyeColor.GRAY)
        .put(Blocks.LIGHT_GRAY_WOOL, DyeColor.LIGHT_GRAY)
        .put(Blocks.CYAN_WOOL, DyeColor.CYAN)
        .put(Blocks.PURPLE_WOOL, DyeColor.PURPLE)
        .put(Blocks.BLUE_WOOL, DyeColor.BLUE)
        .put(Blocks.BROWN_WOOL, DyeColor.BROWN)
        .put(Blocks.GREEN_WOOL, DyeColor.GREEN)
        .put(Blocks.RED_WOOL, DyeColor.RED)
        .put(Blocks.BLACK_WOOL, DyeColor.BLACK)
        .build();

    private static final ImmutableMap<Item, Block> ITEM_COLORS = ImmutableMap.<Item, Block>builder()
        .put(Items.ALLIUM, Blocks.MAGENTA_WOOL)
        .put(Items.AMETHYST_SHARD, Blocks.AMETHYST_BLOCK)
        .put(Items.APPLE, Blocks.RED_WOOL)
        .put(Items.ARMADILLO_SCUTE, Blocks.ANCIENT_DEBRIS)
        .put(Items.AZURE_BLUET, Blocks.WHITE_WOOL)
        .put(Items.BEEF, Blocks.NETHERRACK)
        .put(Items.BEETROOT, Blocks.NETHERRACK)
        .put(Items.BLAZE_POWDER, Blocks.GOLD_BLOCK)
        .put(Items.BLUE_ORCHID, Blocks.LIGHT_BLUE_WOOL)
        .put(Items.BONE, Blocks.BONE_BLOCK)
        .put(Items.BRICK, Blocks.BRICKS)
        .put(Items.BROWN_MUSHROOM, Blocks.BROWN_WOOL)
        .put(Items.CARROT, Blocks.ORANGE_WOOL)
        .put(Items.CHICKEN, Blocks.WHITE_TERRACOTTA)
        .put(Items.CHORUS_FRUIT, Blocks.PURPUR_BLOCK)
        .put(Items.CLAY_BALL, Blocks.CLAY)
        .put(Items.COCOA_BEANS, Blocks.COCOA)
        .put(Items.COD, Blocks.WHITE_TERRACOTTA)
        .put(Items.COD_BUCKET, Blocks.BROWN_TERRACOTTA)
        .put(Items.COPPER_INGOT, Blocks.COPPER_BLOCK)
        .put(Items.CORNFLOWER, Blocks.BLUE_WOOL)
        .put(Items.DANDELION, Blocks.YELLOW_WOOL)
        .put(Items.DIAMOND, Blocks.DIAMOND_BLOCK)
        .put(Items.EGG, Blocks.BONE_BLOCK)
        .put(Items.ENCHANTED_GOLDEN_APPLE, Blocks.GOLD_BLOCK)
        .put(Items.ENDER_PEARL, Blocks.WARPED_PLANKS)
        .put(Items.FEATHER, Blocks.WHITE_WOOL)
        .put(Items.FLINT, Blocks.BLACK_WOOL)
        .put(Items.GHAST_TEAR, Blocks.WHITE_WOOL)
        .put(Items.GLOWSTONE_DUST, Blocks.GLOWSTONE)
        .put(Items.GOLD_INGOT, Blocks.GOLD_BLOCK)
        .put(Items.GUNPOWDER, Blocks.GRAY_WOOL)
        .put(Items.HEART_OF_THE_SEA, Blocks.CONDUIT)
        .put(Items.HONEYCOMB, Blocks.HONEYCOMB_BLOCK)
        .put(Items.INK_SAC, Blocks.BLACK_WOOL)
        .put(Items.IRON_INGOT, Blocks.IRON_BLOCK)
        .put(Items.LAVA_BUCKET, Blocks.LAVA)
        .put(Items.LEATHER, Blocks.SPRUCE_PLANKS)
        .put(Items.LILAC, Blocks.MAGENTA_WOOL)
        .put(Items.LILY_OF_THE_VALLEY, Blocks.WHITE_WOOL)
        .put(Items.MELON_SLICE, Blocks.MELON)
        .put(Items.MILK_BUCKET, Blocks.WHITE_WOOL)
        .put(Items.MUTTON, Blocks.RED_WOOL)
        .put(Items.NAME_TAG, Blocks.BONE_BLOCK)
        .put(Items.NAUTILUS_SHELL, Blocks.BONE_BLOCK)
        .put(Items.NETHER_STAR, Blocks.DIAMOND_BLOCK)
        .put(Items.NETHERITE_INGOT, Blocks.NETHERITE_BLOCK)
        .put(Items.ORANGE_TULIP, Blocks.ORANGE_WOOL)
        .put(Items.OXEYE_DAISY, Blocks.WHITE_WOOL)
        .put(Items.PAPER, Blocks.WHITE_WOOL)
        .put(Items.PEONY, Blocks.PINK_WOOL)
        .put(Items.PHANTOM_MEMBRANE, Blocks.BONE_BLOCK)
        .put(Items.PINK_TULIP, Blocks.PINK_WOOL)
        .put(Items.POISONOUS_POTATO, Blocks.SLIME_BLOCK)
        .put(Items.POPPY, Blocks.RED_WOOL)
        .put(Items.PORKCHOP, Blocks.PINK_WOOL)
        .put(Items.POTATO, Blocks.WHITE_TERRACOTTA)
        .put(Items.PRISMARINE_CRYSTALS, Blocks.SEA_LANTERN)
        .put(Items.PRISMARINE_SHARD, Blocks.PRISMARINE)
        .put(Items.PUFFERFISH, Blocks.YELLOW_TERRACOTTA)
        .put(Items.PUFFERFISH_BUCKET, Blocks.YELLOW_TERRACOTTA)
        .put(Items.RABBIT, Blocks.PINK_WOOL)
        .put(Items.RABBIT_HIDE, Blocks.OAK_PLANKS)
        .put(Items.RED_MUSHROOM, Blocks.RED_MUSHROOM_BLOCK)
        .put(Items.RED_TULIP, Blocks.RED_WOOL)
        .put(Items.ROSE_BUSH, Blocks.RED_WOOL)
        .put(Items.ROTTEN_FLESH, Blocks.BROWN_WOOL)
        .put(Items.SALMON, Blocks.ACACIA_PLANKS)
        .put(Items.SALMON_BUCKET, Blocks.PINK_TERRACOTTA)
        .put(Items.SHULKER_SHELL, Blocks.SHULKER_BOX)
        .put(Items.SNOWBALL, Blocks.SNOW_BLOCK)
        .put(Items.SPIDER_EYE, Blocks.NETHERRACK)
        .put(Items.STICK, Blocks.OAK_PLANKS)
        .put(Items.SUGAR, Blocks.WHITE_WOOL)
        .put(Items.SUNFLOWER, Blocks.YELLOW_WOOL)
        .put(Items.TOTEM_OF_UNDYING, Blocks.YELLOW_TERRACOTTA)
        .put(Items.TRIDENT, Blocks.PRISMARINE)
        .put(Items.TROPICAL_FISH, Blocks.ORANGE_WOOL)
        .put(Items.TROPICAL_FISH_BUCKET, Blocks.ORANGE_TERRACOTTA)
        .put(Items.TURTLE_SCUTE, Blocks.LIME_WOOL)
        .put(Items.WATER_BUCKET, Blocks.WATER)
        .put(Items.WHEAT, Blocks.HAY_BLOCK)
        .put(Items.WHITE_TULIP, Blocks.WHITE_WOOL)
        .put(Items.WITHER_ROSE, Blocks.BLACK_WOOL)
        .build();

    public static final TextColor WHITE = TextColor.fromRgb(0xFFFFFF);

    public static @Nullable DyeColor getChannelAtPosition(final Level level, final BlockPos pos) {
        final var state = level.getBlockState(pos);
        return CHANNELS.get(state.getBlock());
    }

    public static HopperCounter getCounter(final DyeColor channel) {
        return COUNTERS.getOrDefault(channel, new HopperCounter(channel));
    }

    public static @Nullable HopperCounter getCounter(final String channel) {
        try {
            final var channelRef = DyeColor.byName(channel, DyeColor.WHITE);
            return COUNTERS.get(channelRef);
        } catch (final IllegalArgumentException _) {
            return null;
        }
    }

    public static void resetAll(final MinecraftServer server, final boolean refresh) {
        for (final var counter : COUNTERS.values()) {
            counter.reset(server);
            if (refresh) counter.startTick = -1L;
        }
    }

    public static List<Component> formatAll(final MinecraftServer server, final boolean realtime) {
        final var texts = new ArrayList<Component>();
        for (final var counter : COUNTERS.values()) {
            final var temp = counter.format(server, realtime, false);
            if (temp.size() > 1) {
                if (!texts.isEmpty()) texts.add(Messenger.text(""));
                texts.addAll(temp);
            }
        }

        if (texts.isEmpty()) texts.add(Messenger.text(Component.translatable("message.rgkcn.hopper_counter.empty")));
        return texts;
    }

    public static @Nullable TextColor getColor(final ServerLevel level, final Item item) {
        final var registryAccess = level.registryAccess();
        final var direct = fromItem(registryAccess, item);
        if (direct != null) return direct;
        final var id = registryAccess.lookupOrThrow(Registries.ITEM).getKey(item);
        if (id == null) return null;
        for (final var recipe : getRecipesForOutput(level, id))
            for (final var ingredient : recipe.placementInfo().ingredients()) {
                @SuppressWarnings("deprecation") final var match = ingredient.items()
                    .filter(stack -> fromItem(registryAccess, stack.value()) != null)
                    .findFirst();

                if (match.isPresent()) return fromItem(registryAccess, match.get().value());
            }

        return null;
    }

    public static @Nullable TextColor fromItem(final RegistryAccess registryAccess, final Item item) {
        if (ITEM_COLORS.containsKey(item)) {
            final var rgb = ITEM_COLORS.get(item).defaultMapColor().col;
            return TextColor.fromRgb(ARGB.multiplyAlpha(rgb, 0.7f));
        }

        if (item instanceof DyeItem dye) {
            final var dyeColor = dye.getDefaultInstance().get(DataComponents.DYE);
            final var color = dyeColor != null ? dyeColor : DyeColor.WHITE;
            return TextColor.fromRgb(color.getTextColor());
        }

        Block block = null;
        final var itemRegistry = registryAccess.lookupOrThrow(Registries.ITEM);
        final var blockRegistry = registryAccess.lookupOrThrow(Registries.BLOCK);
        final var id = itemRegistry.getKey(item);
        if (item instanceof BlockItem blockItem) block = blockItem.getBlock();
        else if (blockRegistry.getOptional(id).isPresent()) block = blockRegistry.getValue(id);

        if (block != null) {
            if (block instanceof AbstractBannerBlock banner) return TextColor.fromRgb(banner.getColor().getTextColor());
            if (block instanceof BeaconBeamBlock beam) return TextColor.fromRgb(beam.getColor().getTextColor());
        }

        return null;
    }

    public static @Nullable DyeColor findChannelForHopper(final HopperBlockEntity hopper) {
        if (hopper.getLevel() != null) {
            final var facing = hopper.getBlockState().getValue(HopperBlock.FACING);
            return getChannelAtPosition(hopper.getLevel(), hopper.getBlockPos().relative(facing));
        }

        return null;
    }

    private static List<Recipe<?>> getRecipesForOutput(final ServerLevel level, final Identifier id) {
        final var recipes = new ArrayList<Recipe<?>>();
        final var context = SlotDisplayContext.fromLevel(level);
        final var recipeManager = level.getServer().getRecipeManager();
        recipeManager.getRecipes().forEach(recipe -> {
            for (final var display : recipe.value().display()) {
                display.result().resolveForStacks(context).forEach(stack -> {
                    if (BuiltInRegistries.ITEM.wrapAsHolder(stack.getItem()).unwrapKey()
                        .map(ResourceKey::identifier)
                        .orElseThrow(IllegalStateException::new).equals(id))
                        recipes.add(recipe.value());
                });
            }
        });

        return recipes;
    }

    public final DyeColor channel;
    private final String colour;
    private final Map<Item, Long> counter = new LinkedHashMap<>();
    private long startTick = -1L;
    private long startMillis;

    private HopperCounter(final DyeColor channel) {
        this.channel = channel;
        var hexColor = Integer.toHexString(channel.getMapColor().col);
        if (hexColor.length() < 6) hexColor = "0".repeat(6 - hexColor.length()) + hexColor;
        colour = "#" + hexColor + " " + channel.getSerializedName();
    }

    public void add(final MinecraftServer server, final ItemStack stack) {
        if (startTick < 0) {
            startTick = server.overworld().getGameTime();
            startMillis = Util.getMillis();
        }

        final var item = stack.getItem();
        counter.put(item, counter.getOrDefault(item, 0L) + stack.getCount());
    }

    public void reset(final MinecraftServer server) {
        counter.clear();
        startTick = server.overworld().getGameTime();
        startMillis = Util.getMillis();
    }

    public List<Component> format(final MinecraftServer server, final boolean realtime, final boolean brief) {
        final var currentTick = server.overworld().getGameTime();
        final var ticks = Math.max(realtime ? (Util.getMillis() - startMillis) / 50L : currentTick - startTick, 1);
        if (startTick < 0 || ticks == 0) {
            if (brief) return Collections.singletonList(Messenger.compose("l" + colour, "0 : ", "i7 -, -/h, - m "));
            return Collections.singletonList(
                // %s--0 hasn't started counting yet
                Messenger.format(Component.translatable("message.rgkcn.hopper_counter.counting", colour))
            );
        }

        final var total = getTotalItems();
        if (total == 0L) {
            if (brief) return Collections.singletonList(Messenger.compose(
                "l" + colour, "0 : ", "l0 0", "0 , ", "l0 0", "0 /h, ",
                String.format(Locale.ROOT, "l0 %.1f ", ticks / (20d * 60d)), "0 m"
            ));

            return Collections.singletonList(
                // 0 No items for --%s--0 yet (%s m.%s)--le [x]--^7 reset--!/counter %s reset
                Messenger.format(Component.translatable("message.rgkcn.hopper_counter.total",
                    colour, String.format(Locale.ROOT, "l0 %.2f", ticks / (20d * 60d)), (realtime ? "🌏" : ""),
                    channel.getSerializedName()
                ))
            );
        }

        if (brief) return Collections.singletonList(Messenger.compose(
            "l" + colour, "0 : ", "l0 " + total, "0 , ", "l0 " + (total * (20 * 60 * 60) / ticks),
            "0 /h, ", String.format(Locale.ROOT, "l0 %.1f ", ticks / (20d * 60d)), "0 m"
        ));

        final var items = new ArrayList<Component>();
        // 0 Items for --%s--0 (--%s--0 m%s), --0 total: --10 %s--0 , (--%s--0 /h):--le [x]--^7 reset--!/counter %s reset
        items.add(Messenger.format(Component.translatable(
            "message.rgkcn.hopper_counter.items",
            colour, String.format(Locale.ROOT, "l0 %.2f", ticks / (20d * 60d)), (realtime ? "🌏" : ""),
            total, String.format(Locale.ROOT, "l0 %.1f", (total * (20d * 60d * 60d) / ticks)),
            channel.getSerializedName()
        )));

        items.addAll(counter.entrySet().stream()
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .map(entry -> {
                final var item = entry.getKey();
                final var name = Component.translatable(item.getDescriptionId());
                final var style = name.getStyle();
                final var color = getColor(server.overworld(), item);
                name.setStyle(color != null ? style.withColor(color) : style.withItalic(true));
                final var count = entry.getValue();
                return Messenger.compose(
                    "7 - ", name, "7 : ", "l0 " + count, "7 , ",
                    String.format(Locale.ROOT, "l0 %.1f", (count * (20d * 60d * 60d) / ticks)), "0 /h"
                );
            }).toList());

        return items;
    }

    public long getTotalItems() {
        return counter.isEmpty() ? 0 : counter.values().stream().mapToLong(Long::longValue).sum();
    }
}
