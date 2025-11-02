package com.sniklz.foodqueue;


import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;


public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue SLOTS_SIZE = BUILDER
            .comment("How big should the slots in the HUD be?")
            .defineInRange("slots_size", 0.75,  0.1, 2);

    public static final ModConfigSpec.IntValue DEFAULT_SLOTS = BUILDER
            .comment("When you first login the world, how many slots were there?")
            .defineInRange("blocking_slots_count", 2,  1, 6);

    public static final ModConfigSpec.BooleanValue WANT_TO_FOOD_DISAPPEAR_AFTER_TIME = BUILDER
            .comment("Should food disappear from the queue of eaten items after a certain period of time?")
            .define("want_to_food_disappear_after_time_from_queue", true);


    public static final ModConfigSpec.BooleanValue RESET_QUEUE_AFTER_DEATH = BUILDER
            .comment("Is it necessary to reset the queue of eaten food after death?")
            .define("reset_queue_after_death", true);


    public static final ModConfigSpec.IntValue HOW_MANY_MINUTES_SHOULD_TO_FOOD_DISAPPEAR = BUILDER
            .comment("After how many minutes should the food disappear from the queue? ",
                    "(you need turn on want_to_food_disappear_after_time_from_queue to true for this)")
            .defineInRange("how_many_minutes_should_to_food_disappear", 4, 1, 99999);


    public static final ModConfigSpec.BooleanValue WANT_TO_SCALE = BUILDER
            .comment("The lines below are responsible for scaling quantity of the slots over time.",
                    "Do you want to generally scale the number of slots over time? False here means disabling total scaling!")
            .define("blocking_slots_scale_total", true);

    public static final ModConfigSpec.IntValue FIRST_SLOT_SCALE = BUILDER
            .comment("After how many MINUTES do you want to add the FIRST slot? (By default its 180 minutes or 2 hours)",
                    "(you need to set blocking_slots_scale_total to true!)")
            .defineInRange("first_slot_scale_after", 120, 1, 99999);


    public static final ModConfigSpec.IntValue HOW_MANY_SLOTS_ADD_FIRST_TIME = BUILDER
            .comment("How many slots do you want to add?",
                    "(you need to set blocking_slots_scale_total to true!)")
            .defineInRange("how_many_slots_add_first_time", 1, 1, 3);


    public static final ModConfigSpec.BooleanValue WANT_TO_SCALE_AGAIN = BUILDER
            .comment("Do you want another slot to be added after the first one?")
            .define("blocking_slots_scale_second", true);

    public static final ModConfigSpec.IntValue SECOND_SLOT_SCALE = BUILDER
            .comment("After how many minutes do you want to add the SECOND slot?",
                    "(you need to set blocking_slots_scale_total and blocking_slots_scale_second to true!)",
                    "If you set a value here that is less than the value of first_slot_scale_after, then you will most likely break the mod.")
            .defineInRange("second_slot_scale_after", 180, 1, 99999);

    public static final ModConfigSpec.IntValue HOW_MANY_SLOTS_ADD_SECOND_TIME = BUILDER
            .comment("How many slots do you want to add again?",
                    "(you need to set blocking_slots_scale_total and blocking_slots_scale_second to true!)")
            .defineInRange("how_many_slots_add_second_time", 1, 1, 3);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
