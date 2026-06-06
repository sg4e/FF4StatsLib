# FF4StatsLib documentation

FF4StatsLib is a Java 8 library for programs that analyze Final Fantasy IV data and Final Fantasy IV: Free Enterprise (FF4FE) seeds. It bundles game data as classpath resources and exposes object models for:

- boss substitutions, formations, and enemy statistics;
- character levels, experience thresholds, deterministic and possible stat growth, HP, and MP;
- weapons, armor, and their stat modifiers;
- FF4FE human-readable flags, binary flags, version-specific flag specifications, and seed URLs; and
- key items and the locations accessible with a given inventory.

The library has no executable entry point, command-line interface, server, or persistence layer. Consumers call its Java API; most reference data is loaded lazily when the corresponding class or enum is initialized.

## Documentation map

- [Getting started and common recipes](getting-started.md) — build the project, put it on a classpath, and perform common tasks.
- [Architecture and data flow](architecture.md) — understand packages, resource loading, initialization, algorithms, and extension points.
- [API reference](api-reference.md) — detailed behavior of every public type and method.
- [Data files and maintenance](data-files.md) — resource formats, dependencies, test strategy, and guidance for changing the library.
- [Behavioral caveats](#behavioral-caveats) — important mutability, failure, and compatibility details.

## Project at a glance

| Area | Main entry points | Backing resources |
| --- | --- | --- |
| Boss data | `Battle.getAllBosses()`, `Formation.getFor(...)` | `bosses.csv` |
| Character progression | `LevelData`, `PartyMember` | `party/*.csv` plus in-code post-level-70 growth tables |
| Equipment | `Weapon.getWeapon(...)`, `Armor.getArmor(...)` | `equipment/weapons.csv`, `equipment/armor.csv` |
| FF4FE flags | `FlagSet.from(...)`, `FlagVersion` | `fe/flagVersions/*.csv` and `*.json` |
| Key-item routing | `KeyItem`, `KeyItemLocation.getAccessibleLocations(...)` | enum declarations in Java source |
| Generic resource CSV access | `CSVParser`, `RecordParser` | any RFC 4180 CSV visible to the system class loader |

## Supported runtime and build

- The Gradle `java-library` plugin builds the project.
- Source and target compatibility are Java 8.
- `./gradlew test` runs the JUnit 4 test suite.
- `./gradlew build` creates the library JAR under `build/libs/`.
- The build does not configure a Maven group, artifact version, publishing repository, or application entry point. To consume a local build, add the generated JAR and its runtime dependencies to your application, or include this project as a Gradle project dependency.

The implementation depends on Apache Commons CSV, Apache Commons Text, Guava, Jackson Databind, and SLF4J API. See [Data files and maintenance](data-files.md#dependencies) for why each is used.

## Design principles

### Data is shipped with the library

Most lookups do not reach the network or a database. CSV and JSON resources in `src/main/resources` are packaged into the JAR and read through a class loader. This makes queries fast and reproducible, but changing reference data requires rebuilding the library.

### Domain objects and lookup catalogs are separate

Classes such as `Enemy`, `Battle`, `Stats`, `Weapon`, and `Armor` are value-like data holders. Static catalogs such as `Battle.getAllBosses()`, `Weapon.getWeapon(...)`, and enum constants in `LevelData` provide the bundled canonical data.

### FF4FE flags are versioned bit fields

A `FlagVersion` loads a flag specification describing each flag's name, bit offset, bit width, and encoded value. `FlagSet` converts between human-readable flags, URL-safe Base64 binary flags, and FF4FE URLs. Modern JSON specifications can also define rules that automatically enable or disable related flags.

### Character statistics distinguish guaranteed and possible growth

Levels through 70 use the bundled per-character CSV tables. Levels above 70 use one of eight growth-table rows. `getStatsForLevel(...)` follows a deterministic row choice, while minimum/maximum methods calculate the possible bounds across all growth rows.

## Behavioral caveats

These details are significant when integrating the API:

1. **Some returned collections are mutable live collections.** `Formation.getAllEnemies()`, `Enemy.getScriptValues()`, and `FlagVersion.getAllFlags()` expose their underlying lists. Mutating them changes the object/catalog visible to later callers. Treat them as read-only. In contrast, `Battle.getAllBosses()` and `CSVParser.Records` are unmodifiable wrappers, `FlagSet.getFlags()` returns a copy, and `KeyItemLocation.getRequiredItemsForAccess()` returns a copy.
2. **Lookup keys are case-sensitive unless documented otherwise.** Equipment lookups lowercase both name and type internally. `Formation.getFor(...)` and `Battle` equality use exact strings, so callers should use the title-cased boss and position names found in the bundled catalog.
3. **Resource-load failures are often logged rather than thrown.** Static boss/equipment/level/flag loaders catch broad exceptions and log through SLF4J. A missing or malformed resource can therefore result in empty catalogs, `null` values, or later failures. Provide an SLF4J binding in applications where diagnostics matter.
4. **Method names preserve historical misspellings.** The public enemy methods are `getAttackMultipler()` and `getDefenseMultipler()`, and the flag-version method is `getSeperator()`. Consumers must use these exact spellings.
5. **`PartyMember.gainXp(...)` does not validate the delta.** Negative XP and XP beyond expected bounds are not explicitly rejected. Use sensible non-negative inputs within the game's supported range.
6. **The library is not uniformly immutable or thread-safe.** Static catalogs initialize safely through JVM class initialization, but mutable returned lists and mutable `PartyMember`, `Formation`, and `FlagSet` internals should not be shared across threads without external coordination.
7. **`Stats` has equality but no custom `toString()`.** Log or display individual getters if human-readable output is required.
8. **This documentation describes the implementation, not every FF4/FF4FE game rule.** In particular, key-item accessibility models only the gates encoded by `KeyItemLocation`.

## License

The project is distributed under the GNU General Public License version 3 or later. See `LICENSE.txt` at the repository root.
