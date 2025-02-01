package org.rolypolyvole.seacreatures.util;

import org.jetbrains.annotations.NotNull;
import org.rolypolyvole.seacreatures.creatures.Creature;

import java.util.Random;

public class CreatureUtil {

    private static final Random random = new Random();

    public static @NotNull Creature selectSeaCreature() {
        Creature[] creatures = Creature.values();

        int totalWeight = 0;

        for (Creature creature : creatures) {
            totalWeight += creature.getWeight();
        }

        int chosenWeight = random.nextInt(totalWeight) + 1;

        totalWeight = 0;

        for (Creature creature : creatures) {
            totalWeight += creature.getWeight();

            if (totalWeight >= chosenWeight) {
                return creature;
            }
        }

        return null;
    }
}
