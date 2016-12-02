# DamagingBlocks

## Goal: 
To allow modders to create blocks which cause damage to players upon physically touching them or destroy objects which land on the blocks (i.e. Lava)

## Usage:

The DamagingBlocks module uses a component and a system. Essentially, a component is a request for an entity to have a feature, and a system is provides instructions for the entity to perform the feature.

## DamagingBlockComponent

Defines the rate and amount of damage which would be inflicted on the player.

<pre>
public class DamagingBlockComponent implements Component {
    public int timeBetweenDamage = 1000; //The rate (value in milliseconds) at which the damage is inflicted
    public int blockDamage = 20;   //The damage the block inflicts to the player
    @Replicate(FieldReplicateType.SERVER_TO_OWNER)
    public long nextDamageTime; //Helper variable for the system to know when to inflict damage
}
</pre>

## DamageSystem

The logic for inflicting damage to players to destroying blocks when the block is physically touched.

### Methods in DamageSystem

#### update()
Calculates when damage should be inflicted to players and inflicts damage to players. Also destroies items touching the block.

#### onBlockEnter()
Starts the damage infliction when a player or item starts touching the block.
