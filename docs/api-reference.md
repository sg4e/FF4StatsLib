# API reference

This reference covers every public top-level type. Signatures that expose Guava, Commons CSV, Jackson, or JavaBeans types require those dependencies on the consumer's compile/runtime classpath as appropriate.

## Bosses and enemies — `sg4e.ff4stats`

### `Battle`

A `(boss, position)` lookup key. Both fields are required by convention but the constructor does not validate `null`.

- `Battle(String boss, String position)` — creates a key using exact strings.
- `static Map<Battle, Formation> getAllBosses()` — returns the unmodifiable, classpath-loaded boss map.
- `String getBoss()` / `String getPosition()` — return key components.
- `equals(Object)` / `hashCode()` — compare both components and make `Battle` suitable as a map key.
- `toString()` — returns `<boss> @ <position>`.

### `Formation`

An ordered list of enemies belonging to one boss/position pairing.

- `Formation()` — starts empty.
- `void addEnemy(Enemy enemy)` — appends an enemy; accepts `null` because no validation is performed.
- `List<Enemy> getAllEnemies()` — returns the live mutable list.
- `static Formation getFor(String boss, String position)` — exact lookup using a temporary `Battle`; returns `null` when absent.
- `toString()` — joins enemy `toString()` values in `Formation{...}`.

### `Enemy`

A complete enemy-stat row. The long public constructor accepts, in order: name; level; HP; EXP; GP; attack multiplier; hit percent; attack; defense multiplier; evasion percent; defense; magic-defense multiplier; magic-evasion percentage; magic defense; min speed; max speed; spell power; script values.

- `static Enemy fromRecord(RecordParser record)` — maps columns 2–18 to the fixed fields and columns 19 onward to script values. It assumes the `bosses.csv` column order.
- Getters expose every constructor field. Historical method spellings `getAttackMultipler()` and `getDefenseMultipler()` must be used exactly.
- `List<String> getScriptValues()` returns the live supplied list; neither the constructor nor getter makes a defensive copy.
- `toString()` returns a diagnostic representation of numeric and script fields. The current representation omits the enemy name.
- `Enemy` does not define value equality; instances compare by identity.

## CSV helpers — `sg4e.ff4stats.csv`

### `CSVParser`

- `CSVParser(String csvfile) throws IOException` — opens a system-class-loader resource, applies Apache Commons CSV RFC 4180 parsing with its first record as headers, and wraps all data rows.
- `public final List<RecordParser> Records` — an unmodifiable list when the resource exists; `null` when no resource exists at that path. The capitalized field name is part of the public API.

Streams/readers are not explicitly closed by this implementation. Prefer this helper for small packaged resources, which is how the library uses it.

### `RecordParser`

Typed access around one Apache Commons `CSVRecord`.

- `RecordParser(CSVRecord record)` — wraps the supplied record.
- `int getInteger(int index)` / `int getInteger(String header)` — parse an integer, returning zero on `NumberFormatException`.
- `int getInteger(int index, int defaultValue)` / header overload — return the supplied default on `NumberFormatException`.
- `String getString(int index)` / header overload — return raw cell text.
- `Boolean getBoolean(int index)` / header overload — delegate to `Boolean.parseBoolean`.
- `int size()` — return the number of values in the record.

Only malformed integer content is defaulted. Invalid indices or unknown headers can still throw exceptions from Commons CSV.

## Party and equipment — `sg4e.ff4stats.party`

### `Stats`

A five-integer stat tuple ordered as strength, agility, vitality, wisdom, and willpower.

- `Stats(int str, int agi, int vit, int wis, int will)` — creates a tuple with no range validation.
- Getters return each component.
- `equals(Object)` / `hashCode()` compare all five components.
- No arithmetic helpers or custom `toString()` are provided.

### `Equipment`

Shared interface implemented by `Weapon` and `Armor`:

- `String getName()`
- `String getType()`
- `Stats getStats()` — primary-stat modifiers associated with the equipment.

### `Weapon`

- `Weapon(String name, String type, int atk, int hitPercentage, Stats stats, boolean throwable)` — constructs an independent weapon with no validation.
- Getters: `getName()`, `getType()`, `getStats()`, `getAttack()`, `getHitPercentage()`, and `isThrowable()`.
- `static Weapon getWeapon(String name, String type)` — lowercases both arguments and performs a catalog lookup; returns `null` when absent. Passing `null` causes a `NullPointerException` during lowercasing.
- No value equality or `toString()` override is defined.

### `Armor`

- `Armor(String name, String type, int def, int evade, int magDef, int magEvade, Stats stats)` — constructs independent armor with no validation.
- Getters: `getName()`, `getType()`, `getStats()`, `getDefense()`, `getEvasion()`, `getMagicDefense()`, and `getMagicEvasion()`.
- `static Armor getArmor(String name, String type)` — lowercases both arguments and performs a catalog lookup; returns `null` when absent. Passing `null` causes a `NullPointerException`.
- No value equality or `toString()` override is defined.

### `GrowthTable`

Stores eight `Stats` deltas used for post-level-70 growth.

- The constructor requires eight `Stats` arguments, corresponding to indices `0` through `7`.
- `Stats get(int index)` returns one row. Intended valid indices are `0..7`. Avoid index `8`; despite the explicit bounds check's message, the implementation allows it past that check and the array access then throws `ArrayIndexOutOfBoundsException`.

### `LevelData`

Character progression catalog. Constants are `DARK_KNIGHT_CECIL`, `KAIN`, `RYDIA`, `TELLAH`, `EDWARD`, `ROSA`, `YANG`, `POROM`, `PALOM`, `PALADIN_CECIL`, `CID`, `EDGE`, and `FUSOYA`.

- `int getLevelForTotalExperience(int totalXp)` — returns the level whose XP range contains the value. Values beyond represented ranges can result in a failed lookup/unboxing error; use valid game XP.
- `Stats getStatsForLevel(int level)` — deterministic base/growth-table result.
- `Stats getStatsForTotalExperience(int totalXp)` — converts XP to level, then calls deterministic stat calculation.
- `Stats getMinStatsForLevel(int level)` / `getMaxStatsForLevel(int level)` — possible per-stat bounds, especially relevant above level 70.
- `GrowthTable getGrowthTable()` — returns the character's eight post-70 growth rows.
- `int getStartingLevel()` — returns the first level represented by the character resource.
- `int getMinimumXpForLevel(int level)` — inclusive lower XP threshold.
- `int getMaximumXpForLevel(int level)` — exclusive upper XP threshold; level 99 uses `Integer.MAX_VALUE`.
- `int getStartingHp()` / `getStartingMp()` — return the enum constant's starting values.
- `Range<Integer> getHpRangeAtLevel(int level)` / `getMpRangeAtLevel(int level)` — return closed cumulative possible ranges, capped at 9,999 HP and 999 MP.
- `toString()` — returns the character's display name.

Use supported character levels from `getStartingLevel()` through 99. Several methods assume valid levels and can fail indirectly for out-of-range input rather than throw a curated validation exception.

### `PartyMember`

A mutable progression state backed by one `LevelData` constant.

- `PartyMember(LevelData data)` — begins at that character's catalog starting level and minimum XP.
- `PartyMember(LevelData data, int startingLevel)` — begins at a selected level. Rejects levels below the character's start or above 99.
- `void gainXp(int xpGained)` — adds the delta, recalculates level/min/max stats, and emits changed `xp`, `level`, and `stats` properties.
- `void resetXp()` — resets using configured starting XP/level; absent settings fall back to zero XP and/or a derived level as implemented.
- `Integer getStartingLevel()` / `getStartingXP()` — nullable reset configuration, not necessarily the current starting state used by the constructor.
- `void setStartingLevel(Integer level)` — validates nullable reset level and emits `Start Level`; does not update current state.
- `void setStartingXp(Integer xp)` — when both XP and a starting level are non-null, validates XP against that level's range and emits `Start XP`; does not update current state.
- Current-state getters: `getLevel()`, `getXp()`, `getStats()` (minimum/current deterministic lower value), `getStatsMax()`, and `getData()`.
- Listener methods: `addPropertyChangeListener(...)`, `removePropertyChangeListener(...)`, and `hasPropertyChangeListener(...)`.

## FF4FE flags and key items — `sg4e.ff4stats.fe`

### `Flag`

- `Flag(String name, int offset, int size, int value, FlagVersion version)` — creates a version-owned bit-field choice.
- Getters expose every field.
- `compareTo(Flag)` orders flags by the owning version's natural specification order.
- No equality/hash/string override is defined; flag identity and canonical names are both used by different implementation paths.

### `FlagVersion`

Supported constants: `VERSION_3_0`, `VERSION_3_4`, `VERSION_3_5`, `VERSION_3_7`, `VERSION_4_0_0`, and `VERSION_4_5_0`.

Public constants `latest` (`"4.5.0"`) and `earliest` (`"0.3.0"`) describe the implementation's advertised range.

- `FlagRules getFlagRules()` — returns parsed rules; primarily for internal use.
- `String getSeperator()` — returns readable grouping separator using the historical spelling.
- `String getBinaryFlagVersion()` — returns the binary representation prefix for this spec.
- `List<Flag> getAllFlags()` — returns the live specification list; treat it as read-only.
- `Flag getFlagByName(String name)` — exact canonical-name lookup, or `null`.
- `String getVersion()` — decodes the binary version marker into dotted notation.
- `static Flag getFlagFromFlagString(FlagVersion version, String flag, Flag previousFlag)` — resolves a readable token according to grouping context; mainly an internal parsing helper.
- `static FlagVersion getVersionFromFlagString(String flag)` — probes newest-to-oldest specs for a readable flag.
- `static FlagVersion getFromVersionString(String version)` — maps dotted versions and aliases; unknown values warn and fall back to `VERSION_4_5_0`.

### `FlagRules`

`FlagRules` has a public zero-argument constructor, but its useful top-level operations are protected and nested rule types are package-private. It is exposed by `FlagVersion` for implementation purposes, not designed as a standalone consumer API. Use `FlagSet` to obtain rule-normalized behavior.

### `FlagSet`

A parsed and normalized collection of flags plus version, binary text, optional seed, and readable formatting state. It has no public constructor; use factory methods.

Factories:

- `static FlagSet from(String string)` — returns `null` for `null`/empty; otherwise tries URL, binary, then readable parsing.
- `static FlagSet fromUrl(String url)` — parse supported FF4FE URL forms; malformed URLs/unsupported forms raise `IllegalArgumentException` after URL parsing attempts.
- `static FlagSet fromString(String text)` — parse, infer version, apply rules, normalize text, and encode binary.
- `static FlagSet fromBinary(String binary)` — decode version, flags, and optional seed; rejects non-binary format.

Access and formatting:

- `String getVersion()` — dotted version decoded or inferred for the set.
- `String getSeed()` / `boolean hasSeed()` — inspect optional seed identifier.
- `String getBinary()` — binary flag string retained/generated by parsing.
- `Boolean contains(String flagString)` — exact lookup by canonical flag name.
- `NavigableSet<Flag> getFlags()` — sorted defensive copy.
- `toString()` — selected normalized style: canonical for modern versions and legacy for older versions.
- `toStringLegacyStyle()` — grouped legacy formatting.
- `toStringCanonical()` — modern canonical formatting.
- `String toFlagUrl()` — FF4FE make URL with binary flags.
- `String toSeedUrl()` — FF4FE seed URL, or `null` without a seed.

Protected `rawAdd`, `add`, and `remove` are available to subclasses/package implementation. Rule-aware `add`/`remove` can alter related flags.

### `KeyItem`

Enum constants: `CRYSTAL`, `PASS`, `HOOK`, `DARKNESS`, `EARTH`, `TWIN_HARP`, `PACKAGE`, `SAND_RUBY`, `BARON_KEY`, `MAGMA_KEY`, `TOWER_KEY`, `LUCA_KEY`, `ADAMANT`, `LEGEND`, `PAN`, `SPOON`, `RAT_TAIL`, and `PINK_TAIL`.

`toString()` returns a human-readable display name such as `"Darkness Crystal"` rather than the enum identifier.

### `KeyItemLocation`

Declared locations: `START`, `ANTLION`, `FABUL`, `ORDEALS`, `BARON_INN`, `BARON_CASTLE`, `TOROIA`, `DARK_ELF`, `ZOT`, `TOP_BABIL`, `LOW_BABIL`, `DWARF_CASTLE`, `SEALED_CAVE`, `RAT_TAIL`, `SHEILA_PANLESS`, `SHEILA_PAN`, `SUMMONED_MONSTERS_CHEST`, `ODIN`, `LEVIATAN`, `ASURA`, `SYLPH`, `BAHAMUT`, `PALE_DIM`, `WYVERN`, `PLAGUE`, `DLUNAR`, `OGOPOGO`, `MIST`, `KOKKOL`, `OBJECTIVE`, and `ZEROMUS`.

- `String getLocation()` — display name.
- `String getAbbreviatedLocation()` — short display code.
- `boolean isInUnderworld()` — declared underworld marker.
- `Set<KeyItem> getRequiredItemsForAccess()` — defensive copy of direct gates.
- `toString()` — same as display location.
- `static List<KeyItemLocation> getAccessibleLocations(Collection<KeyItem> keyItems)` — inventory-based filter. Requires a non-null collection and returns a new list in enum declaration order.
