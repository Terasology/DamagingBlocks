// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.damagingblocks.component;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;

/**
 * Marks entities which are being affected by damaging blocks.
 */
public class DamagedByBlockComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_OWNER)
    public long nextDamageTime;
}
