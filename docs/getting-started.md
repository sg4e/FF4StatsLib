# Getting started and common recipes

## Build from source

Requirements: a JDK capable of building Java 8-targeted code. The repository includes Gradle Wrapper scripts, so a separate Gradle installation is unnecessary.

```bash
# Linux/macOS
./gradlew clean test
./gradlew build

# Windows
./gradlew.bat clean test
./gradlew.bat build
```

The JAR is emitted beneath `build/libs/`. The project is a library and has no `main` method to run.

## Consume the library

### Composite/multi-project Gradle build

Because this repository does not publish coordinates, the most direct development setup is to include it as a project and then declare an implementation dependency:

```groovy
// settings.gradle in a containing build
include ':FF4StatsLib'
project(':FF4StatsLib').projectDir = file('../FF4StatsLib')
```

```groovy
// consuming project's build.gradle
dependencies {
    implementation project(':FF4StatsLib')
}
```

The Java Library plugin exposes FF4StatsLib's public classes. At runtime, ensure its declared implementation dependencies are also present. If copying only the built JAR manually, resolve the dependencies listed in `build.gradle` yourself.

## Recipe: inspect a boss formation

```java
import sg4e.ff4stats.Enemy;
import sg4e.ff4stats.Formation;

Formation formation = Formation.getFor("Antlion", "Bahamut");
if (formation == null) {
    System.out.println("No matching boss/position pair");
} else {
    for (Enemy enemy : formation.getAllEnemies()) {
        System.out.printf("%s: level %d, HP %d, XP %d%n",
                enemy.getName(), enemy.getLevel(), enemy.getHp(), enemy.getExp());
    }
}
```

A formation lookup is exact and case-sensitive. To discover valid pairs, iterate the catalog:

```java
import java.util.Map;
import sg4e.ff4stats.Battle;
import sg4e.ff4stats.Formation;

for (Map.Entry<Battle, Formation> entry : Battle.getAllBosses().entrySet()) {
    System.out.println(entry.getKey() + " -> " + entry.getValue());
}
```

`Battle.toString()` produces `boss + " @ " + position`. The map itself is unmodifiable, but formations and their enemy lists should be treated as read-only to avoid modifying the shared catalog.

## Recipe: look up equipment

```java
import sg4e.ff4stats.party.Armor;
import sg4e.ff4stats.party.Stats;
import sg4e.ff4stats.party.Weapon;

Weapon crystalSword = Weapon.getWeapon("crystal", "sword");
if (crystalSword != null) {
    Stats bonus = crystalSword.getStats();
    System.out.printf("%s: %d attack, %d%% hit, +%d strength%n",
            crystalSword.getName(), crystalSword.getAttack(),
            crystalSword.getHitPercentage(), bonus.getStrength());
}

Armor armor = Armor.getArmor("adamant", "armor");
if (armor != null) {
    System.out.printf("%s: %d defense, %d magic defense%n",
            armor.getName(), armor.getDefense(), armor.getMagicDefense());
}
```

Both equipment lookup methods normalize `name` and `type` to lowercase, so their arguments are case-insensitive. They return `null` when no matching row exists. A name can occur under different types, which is why both dimensions are required.

## Recipe: calculate character progression

For stateless calculations, call `LevelData` directly:

```java
import com.google.common.collect.Range;
import sg4e.ff4stats.party.LevelData;
import sg4e.ff4stats.party.Stats;

LevelData yang = LevelData.YANG;
int level = yang.getLevelForTotalExperience(100_000);
Stats deterministic = yang.getStatsForLevel(level);
Stats minimum = yang.getMinStatsForLevel(level);
Stats maximum = yang.getMaxStatsForLevel(level);
Range<Integer> hpRange = yang.getHpRangeAtLevel(level);

System.out.printf("Level %d, STR %d..%d, HP %s%n",
        level, minimum.getStrength(), maximum.getStrength(), hpRange);
```

For a mutable character that gains XP and emits JavaBeans property changes, use `PartyMember`:

```java
import sg4e.ff4stats.party.LevelData;
import sg4e.ff4stats.party.PartyMember;

PartyMember member = new PartyMember(LevelData.KAIN);
member.addPropertyChangeListener(event ->
        System.out.printf("%s: %s -> %s%n",
                event.getPropertyName(), event.getOldValue(), event.getNewValue()));
member.gainXp(5_000);
```

The events emitted by XP operations use property names `xp`, `level`, and `stats`. Changing reset configuration emits `Start Level` and `Start XP`. `setStartingLevel(...)` and `setStartingXp(...)` configure what a later `resetXp()` does; they do not immediately change current XP, level, or stats.

## Recipe: parse and inspect FF4FE flags

`FlagSet.from(...)` is the convenient dispatcher. It recognizes an FF4FE URL, a binary string beginning with `b`, or a human-readable flag string:

```java
import sg4e.ff4stats.fe.FlagSet;

FlagSet flags = FlagSet.from("Kmain Pkey Cstandard Twild");
System.out.println(flags.toString());       // normalized readable form
System.out.println(flags.getBinary());      // URL-safe binary representation
System.out.println(flags.getVersion());     // decoded flag-spec version
System.out.println(flags.contains("Kmain"));
System.out.println(flags.toFlagUrl());
```

Direct parsers are also available:

```java
FlagSet human = FlagSet.fromString("Kmain Pkey Cstandard Twild");
FlagSet binary = FlagSet.fromBinary(human.getBinary());
FlagSet url = FlagSet.fromUrl("https://ff4fe.com/make?flags=" + human.getBinary());
```

Important parsing behavior:

- `from(null)` and `from("")` return `null`.
- `from(...)` tries URL, binary, then readable-string parsing.
- Readable parsing identifies a compatible `FlagVersion`, applies version rules, and produces a normalized readable string and binary string.
- Binary parsing decodes the embedded version and enabled bit fields. A binary string can optionally carry a seed suffix separated by `.`.
- Invalid or incompatible input produces `IllegalArgumentException` from the direct parser; `from(...)` may try another representation before ultimately failing readable parsing.
- `contains(...)` expects a complete canonical flag name, not a grouped display fragment.

Use `toSeedUrl()` only when `hasSeed()` is true; otherwise it returns `null`. `toFlagUrl()` always creates an FF4FE make URL containing the binary flags.

## Recipe: determine accessible key-item locations

```java
import java.util.EnumSet;
import java.util.List;
import sg4e.ff4stats.fe.KeyItem;
import sg4e.ff4stats.fe.KeyItemLocation;

EnumSet<KeyItem> inventory = EnumSet.of(KeyItem.MAGMA_KEY, KeyItem.BARON_KEY);
List<KeyItemLocation> accessible = KeyItemLocation.getAccessibleLocations(inventory);
for (KeyItemLocation location : accessible) {
    System.out.printf("%s (%s)%n",
            location.getLocation(), location.getAbbreviatedLocation());
}
```

The algorithm starts with every declared location, removes underworld locations unless the inventory has `MAGMA_KEY` or `HOOK`, removes `ZEROMUS` unless it has `PASS` or `DARKNESS`, and removes locations whose declared required-item set is not contained in the inventory. The returned list is a new mutable list.

## Recipe: read an additional classpath CSV

```java
import sg4e.ff4stats.csv.CSVParser;
import sg4e.ff4stats.csv.RecordParser;

CSVParser parser = new CSVParser("my-data.csv");
if (parser.Records != null) {
    for (RecordParser row : parser.Records) {
        String name = row.getString("name");
        int value = row.getInteger("value", -1);
    }
}
```

The file must be visible to the system class loader. The first row is interpreted as headers using RFC 4180 rules. A missing resource does not throw: `Records` is set to `null`. A present but unreadable/malformed file can throw `IOException` from the constructor.
