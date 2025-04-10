# LootDrop Plugin

**LootDrop** is a Paper plugin for Minecraft 1.21.4 that enables configurable, scheduled loot chest drops in defined world areas.

---

## 📦 Features

- Define unlimited square-shaped drop areas in any world
- Assign custom loot tables with per-item probabilities
- Timed drops based on interval or `schedules.yml`
- Sync drops across multiple areas
- Title & chat announcements to players with permission
- Particle effects and sounds on drop
- PlaceholderAPI integration for countdowns
- Configurable via `config.yml` and `schedules.yml`

---

## 🚀 Commands

| Command | Description | Permission |
|--------|-------------|------------|
| `/lootdrop reload` | Reloads config and schedules | `lootdrops.admin` |
| `/lootdrop begin`  | Starts scheduled loot drops (if using `schedules.yml`) | `lootdrops.admin` |
| `/lootdrop drop <id>` | Instantly triggers a drop in area by ID | `lootdrops.admin` |

---

## ⚙ Configuration

### `config.yml`

```yaml
areas:
  1:
    world: world
    x1: -100
    z1: -100
    x2: 100
    z2: 100
    interval: 300
    sync: false
    lootTable:
      DIAMOND: 0.1
      IRON_SWORD: 0.3
      BREAD: 0.8
```

### `schedules.yml`

```yaml
1:
  times:
    - 00:00:10
    - 00:01:00
  loop: true
```

- Drop times are relative to `/lootdrop begin`
- Times are in `HH:MM:SS` format

---

## 🔧 Placeholders

Requires PlaceholderAPI.

| Placeholder | Description |
|-------------|-------------|
| `%lootdrops_nextdrop_1%` | Time until next drop in area 1 (mm:ss) |

---

## 🧱 Permissions

```yaml
lootdrops.admin:
  description: Admin access to all commands
  default: op

lootdrops.player:
  description: Receive drop location notifications
  default: true
```

---

## 📂 File Structure

- `config.yml` - area definitions & intervals
- `schedules.yml` - scheduled drop timings

---

## 🧪 Dependencies

- [Paper](https://papermc.io/) 1.21.4+
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) (optional)

---

## 👤 Author

Created by [Quttor](https://github.com/Quttor)

---

## 📃 License

MIT License

