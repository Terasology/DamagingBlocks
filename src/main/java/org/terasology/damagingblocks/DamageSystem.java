/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.damagingblocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.damagingblocks.component.DamagingBlockComponent;
import org.terasology.damagingblocks.component.DamagedByBlockComponent;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.characters.events.OnEnterBlockEvent;
import org.terasology.logic.health.event.DoDamageEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.logic.inventory.PickupComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;

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
     * Calculates when the player should be given damage and applies damage to the players.
     * Also destroys pickable items if inside DamagingBlocks
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
     * Marks the player as possibly about to be damaged if they enter a new block.
     * As the player entity hasn't actually been moved there yet and the event
     * doesn't include the coordinates, checking that the block is actually damaging
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
