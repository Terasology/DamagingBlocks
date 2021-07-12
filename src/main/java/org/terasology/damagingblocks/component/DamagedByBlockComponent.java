// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.damagingblocks.component;

import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Marks entities which are being affected by damaging blocks.
 */
public class DamagedByBlockComponent implements Component<DamagedByBlockComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_OWNER)
    public long nextDamageTime;

    @Override
    public void copy(DamagedByBlockComponent other) {
        this.nextDamageTime = other.nextDamageTime;
    }
}
