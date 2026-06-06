# Architecture and data flow

## Repository layout

```text
build.gradle                         Gradle Java-library build and dependencies
settings.gradle                      Names the root project FF4StatsLib
src/main/java/sg4e/ff4stats/         Boss/enemy/formation API
src/main/java/sg4e/ff4stats/csv/     Classpath CSV adapters
src/main/java/sg4e/ff4stats/party/   Character, stats, and equipment API
src/main/java/sg4e/ff4stats/fe/      FF4FE flags and key-item API
src/main/resources/                  Packaged game and flag-spec data
src/test/java/                       JUnit 4 regression tests
docs/                                Maintainer and consumer documentation
```

## Package responsibilities

### `sg4e.ff4stats`

This package models boss substitutions. On first use, `Battle` loads `bosses.csv`, groups rows by `(boss, position)`, and creates a `Formation` containing one `Enemy` per row. A `Battle` is the map key and compares by exact boss and position strings.

Data flow:

```text
bosses.csv
  -> CSVParser / RecordParser
  -> Battle static initializer
  -> Map<Battle, Formation>
  -> Formation List<Enemy>
```

`Battle.getAllBosses()` exposes an unmodifiable map. Each map value remains mutable because `Formation.addEnemy(...)` is public and `Formation.getAllEnemies()` returns its live list.

### `sg4e.ff4stats.csv`

`CSVParser` is a thin classpath-resource adapter around Apache Commons CSV. It uses `ClassLoader.getSystemClassLoader().getResourceAsStream(...)`, parses RFC 4180 data with a header row, wraps each Commons CSV record in `RecordParser`, and exposes an unmodifiable list through the public final `Records` field.

`RecordParser` provides typed cell access. Integer parsing returns a caller-supplied default (zero by default) only for `NumberFormatException`; missing headers and invalid indices still propagate underlying errors. Boolean parsing delegates to `Boolean.parseBoolean`, so only case-insensitive `"true"` becomes `true`; all other strings become `false`.

### `sg4e.ff4stats.party`

This package has three layers:

1. `Stats` and `Equipment` define shared data shapes.
2. `Weapon` and `Armor` load catalog rows into case-insensitive two-dimensional lookup tables keyed by lowercase name and type.
3. `LevelData`, `GrowthTable`, and `PartyMember` model progression.

#### Equipment initialization

The first active use of `Weapon` or `Armor` triggers a static initializer. It parses the corresponding CSV, creates an object per row, builds an immutable set and Guava `ImmutableTable`, and logs any load error. The set is retained internally; the table backs public lookup. Duplicate `(name, type)` keys would fail table construction and leave the static initialization path in its error behavior.

#### Character progression through level 70

Each `LevelData` enum constant identifies a character, starting HP/MP, a CSV resource, and eight possible post-70 growth deltas. During enum initialization, each CSV row becomes an internal level record containing:

- total XP threshold;
- level;
- five primary stats;
- minimum and maximum HP growth; and
- minimum and maximum MP growth.

The implementation builds maps/range maps for XP-to-level, level-to-XP, base stats, and cumulative HP/MP growth. Public methods then query those precomputed structures.

#### Character progression after level 70

Above level 70, the CSV's level-70 stats form the baseline. For deterministic `getStatsForLevel(level)`, every later level selects growth row `(level - 1) % 8` and adds that row's five deltas. For minimum and maximum calculations, each post-70 level independently chooses the minimum or maximum delta for each stat across all eight rows. Every resulting stat is capped at 99 on the upper end; negative post-70 results are not explicitly floored.

HP and MP ranges are cumulative ranges from the CSV growth columns. They are clamped to game caps: HP up to 9,999 and MP up to 999. They are exposed as Guava closed `Range<Integer>` values.

#### Mutable party members

`PartyMember` wraps a `LevelData` catalog entry with mutable current XP/level/stats and optional reset settings. `gainXp(...)` recalculates level and min/max stats, then fires JavaBeans property-change events. `resetXp()` restores configured starting XP/level, or derives defaults when a setting is absent.

### `sg4e.ff4stats.fe`

This package handles FF4FE flags and key-item reachability.

#### Flag specification model

A `Flag` represents one version-specific encoded choice:

| Property | Meaning |
| --- | --- |
| `name` | Canonical readable flag name |
| `offset` | Starting bit offset in the binary flag payload |
| `size` | Number of bits occupied |
| `value` | Value that identifies the flag at that offset |
| `version` | Owning `FlagVersion` |

`FlagVersion` enumerates supported specifications: `VERSION_3_0`, `VERSION_3_4`, `VERSION_3_5`, `VERSION_3_7`, `VERSION_4_0_0`, and `VERSION_4_5_0`. Legacy versions load CSV specifications; modern versions load JSON. A version also records its binary prefix, readable grouping separator, name lookup, natural ordering, and optional parsed `FlagRules`.

`Flag.compareTo(...)` delegates to its version's natural-order comparison. Comparing flags from inappropriate versions is not a supported use case. `Flag` does not override `equals`, `hashCode`, or `toString`; identity matters internally.

#### Readable flag parsing

`FlagSet.fromString(...)` performs these conceptual steps:

1. Tokenize grouped readable flags and account for supported shorthand/group syntax.
2. Infer a compatible version by trying specifications from newest to oldest.
3. Resolve every canonical flag name; incompatible names cause `IllegalArgumentException`.
4. Seed the set with base flags and apply modern JSON rules as flags are added.
5. Sort flags in the specification's natural order.
6. Format a normalized readable string (legacy or canonical modern style).
7. Pack flag values at their configured bit offsets and prepend the version's binary marker.

The readable representation is therefore normalized rather than guaranteed to preserve the caller's whitespace or grouping.

#### Binary flag parsing

`FlagSet.fromBinary(...)` validates the binary format, decodes the four-character version segment from URL-safe Base64, decodes the flag payload, and checks every flag in the selected specification. A flag is enabled when the bits at its offset equal its configured value. An optional suffix after `.` is retained as the seed identifier.

Supported version aliases are mapped by `FlagVersion.getFromVersionString(...)`. Unknown future versions deliberately fall back to the latest specification with a warning. This can work when the specification is unchanged, but it cannot guarantee correct parsing after a flag-layout change.

#### URL handling

`FlagSet.fromUrl(...)` extracts flags and optional seed data from supported URL shapes. `toFlagUrl()` generates `https://ff4fe.com/make?flags=<binary>`. `toSeedUrl()` generates `https://ff4fe.com/seed/<seed>` only when a seed exists.

#### Rule engine

Modern JSON flag specifications can contain rules. A rule has nested conditions (`AND`, `OR`, `NOT`, or default behavior) and consequences that enable or disable flags. `FlagSet` invokes rules during addition/removal so implied flags and mutually dependent choices stay consistent. `FlagRules` is public only as a type; most of its useful methods and nested types are protected or package-private, so normal consumers should rely on `FlagSet` rather than call it directly.

#### Key-item reachability

`KeyItem` is a display-named enum. `KeyItemLocation` stores a location display name, abbreviation, underworld marker, and required item set. Accessibility is a lightweight inventory filter, not a full progression solver: it does not simulate collecting items, ordering checks, characters, objectives, or every game-world constraint.

## Initialization, errors, and logging

Several catalogs initialize in static blocks or enum constructors. This has practical implications:

- The first reference can perform resource I/O and parsing.
- Data stays cached for the lifetime of the class loader.
- Most catalog-loading exceptions are caught and logged through SLF4J.
- If an application provides no SLF4J provider, diagnostics may be limited.
- A malformed packaged resource can lead to empty or partial data rather than an exception at the calling lookup.

## Mutability and thread safety matrix

| API value | Mutability visible to caller | Notes |
| --- | --- | --- |
| `Battle.getAllBosses()` | Map unmodifiable; values mutable | Do not mutate shared formations |
| `Formation.getAllEnemies()` | Live mutable list | Mutates that formation |
| `Enemy.getScriptValues()` | Live mutable list | Mutates that enemy's script data |
| `CSVParser.Records` | Unmodifiable list | `RecordParser` wrappers are effectively read-only |
| `Weapon`, `Armor`, `Stats`, `Battle`, `Enemy` scalar fields | Effectively immutable | Exceptions are exposed lists noted above |
| `FlagSet.getFlags()` | Defensive `TreeSet` copy | Individual `Flag` objects are effectively immutable |
| `FlagVersion.getAllFlags()` | Live list | Treat as read-only |
| `KeyItemLocation.getRequiredItemsForAccess()` | Defensive set copy | Safe to mutate the returned set |
| `KeyItemLocation.getAccessibleLocations()` | New mutable list | Does not mutate enum state |
| `PartyMember` | Mutable | Coordinate access across threads |

## Extension guidance

- To update bundled data without changing behavior, edit the appropriate resource and add or update tests.
- To add a character, add a `LevelData` enum constant, a matching party CSV, an eight-row `GrowthTable`, and progression tests.
- To add a flag version, add the spec resource and enum constant, then update version detection/mapping and round-trip tests.
- To add a new domain model, prefer an immutable object and defensive/unmodifiable collection views; existing live-list behavior is historical and should not be copied.
