package org.rolypolyvole.seacreatures.creatures;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.rolypolyvole.seacreatures.SeaCreaturesPlugin;

import java.util.List;
import java.util.Random;

public class LakeSquid extends AbstractSeaCreature<Squid> implements Listener {

    private LivingEntity target;

    private final Random random = new Random();

    public LakeSquid(Location location, Player player, SeaCreaturesPlugin main) {
        super(location, player, EntityType.SQUID, 20.0, main);
        target = player;
    }

    @Override
    public void startTasks() {
        applyBuffs();
        new TickManager().runTaskTimer(main, 0L, 1L);
    }

    @Override
    public Squid getEntity() {
        return creature;
    }

    @Override
    public double getVelocityScalar() {
        return 1.0;
    }

    public void applyBuffs() {
        PotionEffect effect = new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 0, false, false);

        creature.addPotionEffect(effect);
    }

    private void findNearbyTarget() {
        List<Entity> nearbyEntities = creature.getNearbyEntities(8.0, 8.0, 8.0);

        Location creatureLocation = creature.getLocation();

        List<Entity> filteredEntities = nearbyEntities.stream()
            .filter(entity -> {
                if (entity instanceof Player targetPlayer) {
                    GameMode playerGameMode = targetPlayer.getGameMode();
                    return playerGameMode == GameMode.SURVIVAL || playerGameMode == GameMode.ADVENTURE;
                }

                return entity instanceof Guardian;
            }).sorted((a, b) -> {
                    double distanceToA = a.getLocation().distance(creatureLocation);
                    double distanceToB = b.getLocation().distance(creatureLocation);

                    return Double.compare(distanceToB, distanceToA);
            }).toList();

        if (!filteredEntities.isEmpty()) {
            target = (LivingEntity) filteredEntities.get(0);
        } else {
            target = null;
        }
    }

    private void approachTarget() {
        Vector facing = new Vector(
            target.getX() - creature.getX(),
            target.getY() - creature.getY(),
            target.getZ() - creature.getZ()
        );

        Location newLocation = creature.getLocation().clone().setDirection(facing);
        creature.teleport(newLocation);
    }

    private void attackTarget() {
        Location squidLocation = creature.getLocation().add(0, 0.5, 0);
        Location targetLocation = target.getLocation().add(0, 0.5, 0);

        Vector directionVector = targetLocation.toVector().subtract(squidLocation.toVector()).normalize().multiply(0.5D);
        int particleCount = (int) squidLocation.distance(targetLocation) * 2;

        for (int i = 0; i < particleCount; i++) {
            World world = creature.getWorld();
            squidLocation.add(directionVector);
            world.spawnParticle(Particle.SQUID_INK, squidLocation, 1, 0, 0, 0);
        }

        if (target instanceof Player targetPlayer) {
            GameMode playerGamemode = targetPlayer.getGameMode();

            if (playerGamemode == GameMode.SURVIVAL || playerGamemode == GameMode.ADVENTURE) {
                target.setHealth(Math.max(0.0, target.getHealth() - 1.0)); //Magic
            }
        }

        target.damage(6.0, creature); //Normal

        PotionEffect effect = new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, false, false);
        target.addPotionEffect(effect);
    }

    private void spinSquid() {
        Location newLocation = creature.getLocation().clone();
        newLocation.setYaw((creature.getYaw() + 25.0F) % 360.0F);

        creature.setVelocity(new Vector(0, 0.05, 0));
        creature.teleport(newLocation);
    }

    private class TickManager extends BukkitRunnable {

        private int regenTicks = 0;
        private int idleTicks = 80;
        private int ticksSinceLastApproach = 0;
        private int ticksSinceLastAttack = 0;
        private int totalAttacks = 0;
        private boolean ready = false;

        @Override
        public void run() {
            if (!creature.isValid()) {
                cancel();
                return;
            }

            creature.setRemainingAir(500);

            if (regenTicks >= 10) {
                creature.setHealth(Math.min(20.0, creature.getHealth() + 1.0));
                regenTicks = 0;
            } else {
                regenTicks++;
            }

            if (ready) {
                spinSquid();

                if (ticksSinceLastAttack >= 35) {
                    findNearbyTarget();

                    if (target != null) {
                        attackTarget();

                        if (random.nextDouble() < 0.25 + (totalAttacks * 0.1)) ready = false;
                        totalAttacks++;
                    } else {
                        ready = false;

                        return;
                    }

                    ticksSinceLastAttack = 0;
                } else {
                    ticksSinceLastAttack++;
                }
            } else {
                totalAttacks = 0;

                if (target != null) {
                    approachTarget();
                }

                if (idleTicks >= 120) {
                    ready = true;
                    idleTicks = 0;
                } else {
                    idleTicks++;
                }

                if (ticksSinceLastApproach >= 10 && target != null) {
                    approachTarget();
                    ticksSinceLastApproach = 0;
                } else {
                    ticksSinceLastApproach++;
                }
            }
        }
    }
}
