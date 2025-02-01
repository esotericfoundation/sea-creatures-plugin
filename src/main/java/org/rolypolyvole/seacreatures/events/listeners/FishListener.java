package org.rolypolyvole.seacreatures.events.listeners;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.rolypolyvole.seacreatures.SeaCreaturesPlugin;
import org.rolypolyvole.seacreatures.creatures.AbstractSeaCreature;
import org.rolypolyvole.seacreatures.creatures.Creature;
import org.rolypolyvole.seacreatures.creatures.LakeSquid;
import org.rolypolyvole.seacreatures.creatures.PufferfishStack;
import org.rolypolyvole.seacreatures.util.CreatureUtil;

import java.util.Random;

public class FishListener implements Listener {
    private final SeaCreaturesPlugin main;
    private final Random random = new Random();

    public FishListener(SeaCreaturesPlugin main) {
        this.main = main;
    } //"Pipe" principle by stephen

    @EventHandler
    public void onFish(@NotNull PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (random.nextDouble() < 0.01) {
                //Drop luck potion
                event.setCancelled(true);
                return;
            }

            if (random.nextDouble() < 0.99) {
                Player player = event.getPlayer();
                FishHook hook = event.getHook();

                Creature creature = CreatureUtil.selectSeaCreature();

                AbstractSeaCreature<? extends LivingEntity> seaCreature;

                if (creature == Creature.LAKE_SQUID) {
                    seaCreature = new LakeSquid(hook.getLocation(), player, main);
                } else {
                    seaCreature = new PufferfishStack(hook.getLocation(), player, main);
                }

                seaCreature.startTasks();

                LivingEntity seaCreatureEntity = seaCreature.getEntity();

                Vector velocity = new Vector(
                    player.getX() - seaCreatureEntity.getX(),
                    player.getY() - seaCreatureEntity.getY(),
                    player.getZ() - seaCreatureEntity.getZ()
                );

                seaCreatureEntity.setVelocity(
                    velocity
                        .normalize()
                        .multiply(seaCreature.getVelocityScalar())
                );

                event.setCancelled(true);
            }
        }
    }
}
