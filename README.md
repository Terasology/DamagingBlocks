# DamagingBlocks

## Goal: 
To allow modders to create blocks which cause damage to players upon physically touching them or destroy objects which land on the blocks (i.e. Lava or Cacti)

## Usage:

The DamagingBlocks module uses a component and a system. For more information regarding components and systems, please check out https://github.com/MovingBlocks/Terasology/wiki/Entity-System-Architecture

Essentially, the custom block's prefab would have a DamagingBlockComponent attached and the DamageSystem would apply the damage or destroy the item.

## DamagingBlockComponent

Defines the rate and amount of damage which would be inflicted on the player.

<pre>
public class DamagingBlockComponent implements Component {
    public int timeBetweenDamage = 1000;        //The rate (value in milliseconds) at which the damage is inflicted
    public int blockDamage = 20;                //The damage the block inflicts to the player
    @Replicate(FieldReplicateType.SERVER_TO_OWNER)
    public long nextDamageTime;                 //Helper variable for the system to know when to inflict damage
}
</pre>

## DamageSystem

The DamageSystem is the code which applies the damage to players or destroys blocks if on DamagingBlocks. 

There are two types of damage which the system deals:
*Damage resulting from the player being inside the block (i.e. Player in a pool of Lava)
*Damage resulting from the player entering and touching the block (i.e. Player running into Cacti)

For further details, please refer to the Javadocs at [DamagingSystems](https://github.com/Terasology/DamagingBlocks/blob/master/src/main/java/org/terasology/damagingblocks/DamageSystem.java)
