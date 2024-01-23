# Features

### Container Locks
- Placing a sign on a container and writing `[lock]` on it prohibits other players from opening it.

### Entity Locks
- Right clicking a passive mob with a nametag labelled `[ClaimToggle]` will prevent it from taking damage.
- Right clicking a villager with a nametag labelled `[TradeToggle]` will prevent other players from trading with it,

### Trust System
- The trust system allows players in your trust list to interract with items you have protected. Currently, this applies to containers and claimed mobs.
- Commands:
    - `/smp trustlist` lists out all the players you currently have trusted
    - `/smp trust [username]` adds a player to your trust list
    - `/smp untrust [username]` removes a player from your trust list

### Auto-Crop Replanter
- Right-clicking a crop with bare hands will harvest the crop and automatically replant it.

### Easy Concrete Mixing
- Concrete powder that is dropped into water will automatically be converted into concrete.

### Supporter Rank
- Supporter rank for Twitch subscribers and Discord server boosters includes:
    - `/enderchest` or `/ec` to open an enderchest
    - `/workbench` or `/wb` to open a crafting table
    - Ability to change name colors by using `/nick`. Example: `/nick &aNot&6ch` creates a green and orange name. [Use this color generator](https://minecraftitemids.com/color-codes)
    - 4 Homes with `/sethome`


### Custom Enchantments
- Timber (Axe Enchantment): When pressing shift, this enchantment breaks all the logs of a tree at once
- Vein Miner (Pickaxe Enchantment): When pressing shift, this enchantment mines a whole ore vein at once

### Custom Recipes
- More Fireworks durations
    - Firework durations limit is increased from duration of 3 (with 3 gunpowder) to duration of 8 (with 8 gunpowder)

### Dragon Egg Respawn
- Dragon eggs respawn on the end portal after a dragon death

### Anvil Price Limit Raised
- Raises the the XP cost at which the anvil will block you from repairing equipment / combining enchantments to 30 levels.

### Fixes
- Elytra damage recalculated to be smoother. Refer to PR [#43](https://github.com/Coolment-Content-Creation/SMP/pull/43)


## Admin Features
- Commands:
    - `/smp reload` reloads the SMP plugin.
    - `/smp teller (withdraw/deposit)` creates a bank teller at your location.
    - `/smp lockholder` returns the owner of a locked sign you are looking at.
    - `/smp forcelock [username]` sets a lock to be owned by a certain player
- Force unclaim entities with a namedtag labelled `[AdminUnclaim]`.

