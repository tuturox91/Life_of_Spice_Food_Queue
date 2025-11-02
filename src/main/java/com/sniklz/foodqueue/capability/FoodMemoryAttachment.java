package com.sniklz.foodqueue.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sniklz.foodqueue.Config;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class FoodMemoryAttachment implements INBTSerializable<CompoundTag> {

    private final Deque<Item> recentFoods = new ArrayDeque<>();
    private int maxSize = Config.DEFAULT_SLOTS.getAsInt();
    private int interval = (Config.HOW_MANY_MINUTES_SHOULD_TO_FOOD_DISAPPEAR.get() * 60) * 20; //3*60*20;//3 * 60 * 20;
    public boolean isBlinking = false;

    public boolean canEat(Item item) {
        return !recentFoods.contains(item);
    }

    public static final Codec<FoodMemoryAttachment> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("maxSize").forGetter(foodMemoryAttachment -> foodMemoryAttachment.maxSize),
                    ResourceLocation.CODEC.listOf().fieldOf("recentFoods").forGetter(cap ->
                            cap.recentFoods.stream()
                                    .map(BuiltInRegistries.ITEM::getKey)
                                    .collect(Collectors.toList())
                    )
            ).apply(instance, (maxSize,foods) -> {
                FoodMemoryAttachment cap = new FoodMemoryAttachment();
                for (ResourceLocation id : foods) {
                    Item item = BuiltInRegistries.ITEM.get(id);
                    if (item != null) {
                        cap.recentFoods.addLast(item);
                    }
                }
                cap.maxSize = maxSize;
                return cap;
            })
    );


    public void addFood(Player player, Item item) {
        updateSizeFromPlaytime(player);


        if (recentFoods.size() >= maxSize) {
            recentFoods.removeFirst();
        }
        recentFoods.addLast(item);
    }

    public Item removeFirst() {
        return recentFoods.removeFirst();
    }

    public int getSize() {
        return recentFoods.size();
    }

    public boolean getStartBlinking() {
        return isBlinking;
    }

    public int getInterval() {
        return interval;
    }

    private boolean fistSlotAdded = false;
    private boolean secondSlotAdded = false;
    public void updateSizeFromPlaytime(Player player) {
        if(!Config.WANT_TO_SCALE.get()) return;
        if(Config.DEFAULT_SLOTS.getAsInt() + Config.HOW_MANY_SLOTS_ADD_FIRST_TIME.getAsInt() + Config.HOW_MANY_SLOTS_ADD_SECOND_TIME.getAsInt() == maxSize) return;
        if(!(player instanceof ServerPlayer serverPlayer)) return;


        int ticksPlayed = serverPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));

        int minutes = ticksPlayed / 1200; // переводим в минуты

        int firstConfigItem = Config.FIRST_SLOT_SCALE.getAsInt();
        int secondConfigItem = Config.SECOND_SLOT_SCALE.getAsInt();
        int firstTime = 0;
        int secondTime = 0;

        if(firstConfigItem == secondConfigItem) secondConfigItem++;
        if(firstConfigItem > secondConfigItem) {firstTime = secondConfigItem; secondTime = firstConfigItem; }
        else if(firstConfigItem < secondConfigItem) {firstTime = firstConfigItem; secondTime = secondConfigItem; }

        if (minutes >= firstTime && !fistSlotAdded) {
            maxSize = maxSize+Config.HOW_MANY_SLOTS_ADD_FIRST_TIME.getAsInt();
            fistSlotAdded = true;
        } else if (minutes >= secondTime && Config.WANT_TO_SCALE_AGAIN.getAsBoolean() && !secondSlotAdded) {
            maxSize = maxSize+Config.HOW_MANY_SLOTS_ADD_SECOND_TIME.getAsInt();
            secondSlotAdded = true;
        }
        serverPlayer.syncData(ModAttachments.FOOD_MEMORY.get());
    }

//    public void setMaxSize(int size) {
//        this.maxSize = size;
//    }

    public List<Item> getFoods() {
        return List.copyOf(recentFoods);
    }


    public void clear() {
        recentFoods.clear();
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("maxSize", this.maxSize);
        tag.putBoolean("isBlinking", this.isBlinking);

        ListTag listTag = new ListTag();
        for (Item item : recentFoods) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id != null) {
                listTag.add(StringTag.valueOf(id.toString()));
            }
        }
        tag.put("recentFoods", listTag);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        recentFoods.clear();
        maxSize = compoundTag.getInt("maxSize");
        isBlinking = compoundTag.getBoolean("isBlinking");

        ListTag listTag = compoundTag.getList("recentFoods", 8); //8 is string
        for (int i = 0; i < listTag.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(listTag.getString(i));
            Item item = BuiltInRegistries.ITEM.get(id);
            if (item != null) {
                recentFoods.addLast(item);
            }
        }

    }
}
