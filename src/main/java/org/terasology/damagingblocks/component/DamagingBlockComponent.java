// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.damagingblocks.component;

import org.terasology.engine.entitySystem.Component;

/**
 * A block which damages entities that touch it.
 */
public class DamagingBlockComponent implements Component {
    public int timeBetweenDamage = 1000;
    public int blockDamage;
}
