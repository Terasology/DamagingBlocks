// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.damagingblocks.component;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * A block which damages entities that touch it.
 */
public class DamagingBlockComponent implements Component<DamagingBlockComponent> {
    public int timeBetweenDamage = 1000;
    public int blockDamage;

    @Override
    public void copyFrom(DamagingBlockComponent other) {
        this.timeBetweenDamage = other.timeBetweenDamage;
        this.blockDamage = other.blockDamage;
    }
}
