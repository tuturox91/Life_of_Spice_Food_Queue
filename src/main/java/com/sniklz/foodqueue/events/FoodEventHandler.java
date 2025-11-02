package com.sniklz.foodqueue.events;

import com.sniklz.foodqueue.Config;
import com.sniklz.foodqueue.capability.FoodMemoryAttachment;
import com.sniklz.foodqueue.capability.ModAttachments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "foodqueue")
public class FoodEventHandler {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player newPlayer = event.getEntity();
        Player oldPlayer = event.getOriginal();

        FoodMemoryAttachment oldCap = oldPlayer.getData(ModAttachments.FOOD_MEMORY.get());
        FoodMemoryAttachment newCap = newPlayer.getData(ModAttachments.FOOD_MEMORY.get());

        if (event.isWasDeath() && Config.RESET_QUEUE_AFTER_DEATH.getAsBoolean()) {

            newCap.clear();
        }
        if (!event.isWasDeath() || !Config.RESET_QUEUE_AFTER_DEATH.getAsBoolean() ){
            newCap.deserializeNBT(null, oldCap.serializeNBT(null));
        }
    }

    @SubscribeEvent
    public static void onFoodStart(LivingEntityUseItemEvent.Start event) {

         if (!(event.getEntity() instanceof Player player)) return;

        ItemStack stack = event.getItem();
        if (stack.get(DataComponents.FOOD) == null) return;

        FoodMemoryAttachment memory = player.getData(ModAttachments.FOOD_MEMORY.get());
        memory.updateSizeFromPlaytime(player);


        if (!memory.canEat(stack.getItem())) {
            event.setCanceled(true);
            player.stopUsingItem();

            if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.translatable("gui.foodqueue.wrong_message"), true);
            }
        }
    }

    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack stack = event.getItem();
        if (stack.get(DataComponents.FOOD) == null) return;

        FoodMemoryAttachment memory = player.getData(ModAttachments.FOOD_MEMORY.get());
        if (memory == null) return;

        memory.addFood(player, stack.getItem());

        if(event.getEntity() instanceof ServerPlayer serverPlayer) serverPlayer.syncData(ModAttachments.FOOD_MEMORY.get());
    }



    private static final int beforeThreeSec = 3*20;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if(!Config.WANT_TO_FOOD_DISAPPEAR_AFTER_TIME.getAsBoolean()) return;
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        FoodMemoryAttachment memory = serverPlayer.getData(ModAttachments.FOOD_MEMORY.get());
        if (memory == null) return;
        if(memory.getSize() == 0) return;
        int interval = memory.getInterval();

        CompoundTag persistent = serverPlayer.getPersistentData();
        int ticks = persistent.getInt("foodqueue_tick_timer") + 1;

        if(ticks >= interval - beforeThreeSec) {
            memory.isBlinking = true;
            serverPlayer.syncData(ModAttachments.FOOD_MEMORY.get());
        }

        if (ticks >= interval) {
            ticks = 0;

            memory.isBlinking = false;
            memory.removeFirst(); // убираем верхний элемент из очереди
            serverPlayer.syncData(ModAttachments.FOOD_MEMORY.get());
        }

        persistent.putInt("foodqueue_tick_timer", ticks);
    }
}


