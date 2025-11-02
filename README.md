<h1>Life of Spice: Food Queue</h1>
 Скачать: [CurseForge](https://curseforge.com/minecraft/mc-mods/life-of-spice-food-queue) 
 [Modrinth](https://modrinth.com/mod/life-of-spice-food-queue)
<hr>
<p><img style="float: left; margin-left: 15px;" src="https://media.forgecdn.net/attachments/description/null/description_417a370d-4449-446a-b6ff-947cbe1fbf74.gif" alt="Food disappear after time from queue" width="86" height="239"></p>
<h4 style="text-align: left;"> Now, when you eat food, it goes into the queue. Food in the queue cannot be eaten again, BUT the size of the queue, the size of the HUD, the increase in the size of the queue over time, and whether the food should disappear from the queue after a certain amount of time (e.g., after 3minutes) can all be configured in the settings. Do you want to start the game in your world with one blocking slot, and after an hour of play, add 2 more to the player? All of this can be configured!</h4>

<h1></h1>
<h1>Config</h1>

<p>The config you need is<code> foodqueue-common.toml</code></p>
<hr>
<div class="spoiler">
<ul>
<li>How big should the slots in the HUD?
<ul>
<li>Range: 0.1 ~ 2.0</li>
<li>slots_size = 0.75</li>
</ul>
</li>
</ul>
<ul>
<li>Reset the queue of eaten food after death?</li>
<ul>
<li>reset_queue_after_death = true</li>
</ul>
</ul>
<ul>
<li>When you first login the world, how many slots were there?
<ul>
<li>Range: 1 ~ 6</li>
<li>blocking_slots_count = 2</li>
</ul>
</li>
</ul>
<ul>
<li>Should food disappear from the queue of eaten items after a certain period of time?
<ul>
<li>want_to_food_disappear_after_time_from_queue = true</li>
</ul>
</li>
</ul>
<ul>
<li>After how many minutes should the food disappear from the queue?</li>
<ul>
<li>Range: 1 ~ 99999</li>
<li>how_many_minutes_should_to_food_disappear = 4</li>
</ul>
</ul>
<ul>
<li>Do you want to generally scale the number of slots over time? False here means disabling total scaling!</li>
<ul>
<li>blocking_slots_scale_total = true</li>
</ul>
</ul>
<ul>
<li>After how many MINUTES do you want to add the FIRST slot? (By default its 120 minutes or 2 hours)</li>
<ul>
<li>Range: 1 ~ 99999</li>
<li>first_slot_scale_after = 120</li>
</ul>
</ul>
<ul>
<li>How many slots do you want to add?</li>
<ul>
<li>Range: 1 ~ 3</li>
<li>how_many_slots_add_first_time = 1</li>
</ul>
</ul>
<ul>
<li>Do you want another slot to be added after the first one?</li>
<ul>
<li>locking_slots_scale_second = true</li>
</ul>
</ul>
<ul>
<li>After how many minutes do you want to add the SECOND slot?</li>
<ul>
<li>Range: 1 ~ 99999</li>
<li>second_slot_scale_after = 180</li>
</ul>
</ul>
<ul>
<li>How many slots do you want to add again?</li>
<ul>
<li>Range: 1 ~ 3</li>
<li>how_many_slots_add_second_time = 1<br><br></li>
</ul>
</ul>
</div>
