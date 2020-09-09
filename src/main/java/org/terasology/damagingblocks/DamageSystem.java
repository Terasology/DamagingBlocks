// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.damagingblocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.damagingblocks.component.DamagedByBlockComponent;
import org.terasology.damagingblocks.component.DamagingBlockComponent;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.characters.events.OnEnterBlockEvent;
import org.terasology.engine.logic.destruction.EngineDamageTypes;
import org.terasology.engine.logic.inventory.PickupComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.WorldProvider;
import org.terasology.health.logic.event.DoDamageEvent;
import org.terasology.math.geom.Vector3f;

@RegisterSystem(RegisterMode.AUTHORITY)
public class DamageSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(DamageSystem.class);

    @In
    private BlockEntityRegistry blockEntityProvider;

    @In
    private Time time;

    @In
    private EntityManager entityManager;

    @In
    private WorldProvider worldProvider;

    /**
     * Calculates when the player should be given damage and applies damage to the players. Also destroys pickable items
     * if inside DamagingBlocks
     *
     * @param delta The time between frames (optional to account for lagging games)
     */
    @Override
    public void update(float delta) {
        long gameTime = time.getGameTimeInMs();

        for (EntityRef entity : entityManager.getEntitiesWith(DamagedByBlockComponent.class, LocationComponent.class)) {
            DamagedByBlockComponent damaged = entity.getComponent(DamagedByBlockComponent.class);
            LocationComponent loc = entity.getComponent(LocationComponent.class);

            if (gameTime > damaged.nextDamageTime) {
                //damage the entity
                EntityRef block = blockEntityProvider.getBlockEntityAt(loc.getWorldPosition());
                DamagingBlockComponent damaging = block.getComponent(DamagingBlockComponent.class);

                if (damaging != null) {
                    entity.send(new DoDamageEvent(damaging.blockDamage, EngineDamageTypes.PHYSICAL.get(), block));
                    // set the next damage time
                    damaged.nextDamageTime = gameTime + damaging.timeBetweenDamage;
                    entity.saveComponent(damaged);
                } else {
                    entity.removeComponent(DamagedByBlockComponent.class);
                }
            }
        }

        //Checks all pickable items to see if they're inside a damaging block and destroys them if they are.
        for (EntityRef entity : entityManager.getEntitiesWith(PickupComponent.class)) {
            LocationComponent loc = entity.getComponent(LocationComponent.class);
            if (loc == null) {
                continue;
            }

            Vector3f vLocation = loc.getWorldPosition();

            EntityRef block = blockEntityProvider.getBlockEntityAt(vLocation);
            if (block.getComponent(DamagingBlockComponent.class) != null) {
                entity.destroy();
            }
        }
    }

    /**
     * Marks the player as possibly about to be damaged if they enter a new block. As the player entity hasn't actually
     * been moved there yet and the event doesn't include the coordinates, checking that the block is actually damaging
     * is deferred to later.
     */
    @ReceiveEvent
    public void onEnterBlock(OnEnterBlockEvent event, EntityRef entity, LocationComponent loc) {
        if (event.getCharacterRelativePosition().y == 0) {
            if (entity.getComponent(DamagedByBlockComponent.class) == null) {
                DamagedByBlockComponent damaged = new DamagedByBlockComponent();
                damaged.nextDamageTime = time.getGameTimeInMs();
                entity.addComponent(damaged);
            }
        }
    }
}
