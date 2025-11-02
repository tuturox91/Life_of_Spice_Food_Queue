package com.sniklz.foodqueue;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sniklz.foodqueue.capability.FoodMemoryAttachment;
import com.sniklz.foodqueue.capability.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.List;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = FoodQueue.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = FoodQueue.MODID, value = Dist.CLIENT)
public class foodqueueClient {
    public foodqueueClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        FoodQueue.LOGGER.info("HELLO FROM CLIENT SETUP");
        FoodQueue.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    private static int blinkTicks =0;
    @SubscribeEvent
    public static void onRenderHud(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        FoodMemoryAttachment memory = mc.player.getData(ModAttachments.FOOD_MEMORY.get());
        if (memory == null) return;

        List<Item> foods = memory.getFoods();

        if (foods.isEmpty()) return;

        GuiGraphics gui = event.getGuiGraphics();
        int screenHeight = gui.guiHeight();

        ResourceLocation SLOT_BG = ResourceLocation.fromNamespaceAndPath("foodqueue", "textures/gui/slot.png");

        RenderSystem.enableBlend();

        // базовый размер слота
        int baseSize = 22;
        // масштаб
        float scale = (float) Config.SLOTS_SIZE.getAsDouble();
        // фактический размер после масштабирования
        int size = (int)(baseSize * scale);

        // отступ и перекрытие
        int spacing = 0; // чтобы иконки немного пересекались
        int x = 4;
        int yStart = screenHeight - size - 4;

        for (int i = 0; i < foods.size(); i++) {
            Item item = foods.get(foods.size() - 1 - i);
            ItemStack stack = new ItemStack(item);

            int y = yStart - i * (size + spacing);

            float blinkAlpha = 1f;
            float blinkScale = scale;

            if (item == foods.getFirst()  && memory.getStartBlinking()) {
                blinkTicks++;
                float pulse = (float) Math.sin(blinkTicks / 6.0);
                blinkAlpha = 0.5f + 0.5f * pulse;
                blinkScale = scale * (1.0f + 0.05f * pulse);
                if (blinkTicks >= 60) { // 3 секунды мигания
                    memory.isBlinking = false;
                    blinkTicks = 0;
                     // или isBlinking = false
                }
            }

            gui.pose().pushPose();
            gui.pose().translate(x, y, 0);
            gui.pose().scale(blinkScale, blinkScale, 1.0f);
            RenderSystem.setShaderColor(1f, 1f, 1f, blinkAlpha);

            gui.blit(SLOT_BG, 0, 0, 0, 0, baseSize, baseSize, baseSize, baseSize);
            gui.renderItem(stack, 3, 2);
            gui.renderItemDecorations(mc.font, stack, 3, 3);

            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            gui.pose().popPose();
        }

        RenderSystem.disableBlend();
    }
}
