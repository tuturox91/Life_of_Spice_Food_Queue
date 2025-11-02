package com.sniklz.foodqueue.capability;


import com.sniklz.foodqueue.FoodQueue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FoodQueue.MODID);

    public static final Supplier<AttachmentType<FoodMemoryAttachment>> FOOD_MEMORY =
            ATTACHMENT_TYPES.register("food_memory", () ->
                    AttachmentType.builder(FoodMemoryAttachment::new)
                            .serialize(FoodMemoryAttachment.CODEC, cap -> true)
                            .sync((holder, player) -> holder == player,  // 👈 только себе!
                                    StreamCodec.of(
                                            // encode (сервер → клиент)
                                            (buf, cap) -> {
                                                CompoundTag tag = cap.serializeNBT(null);
                                                buf.writeNbt(tag);
                                            },
                                            // decode (клиент получает)
                                            buf -> {
                                                FoodMemoryAttachment cap = new FoodMemoryAttachment();
                                                CompoundTag tag = buf.readNbt();
                                                if (tag != null) cap.deserializeNBT(null, tag);
                                                return cap;
                                            }
                                    )
                            )
                            .build()
            );


    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
