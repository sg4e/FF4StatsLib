# Data files, dependencies, testing, and maintenance

## Classpath resource contract

All bundled data lives under `src/main/resources` and is packaged at the root of the library JAR. Code refers to paths such as `bosses.csv`, `equipment/weapons.csv`, and `party/Kain.csv`, without a leading slash.

Resource-name case matters on common deployment platforms. When adding or renaming a file, update the Java reference with the exact same spelling. The loaders assume the resource schema and generally do not perform explicit schema validation, so tests are essential.

## Resource inventory and schemas

### `bosses.csv`

Loaded by `Battle` and transformed into grouped formations. The implementation uses positional columns:

| Index | Meaning |
| ---: | --- |
| 0 | Boss identity; title-cased for the `Battle` key |
| 1 | Position identity; text before `_slot` is title-cased for the key |
| 2 | Enemy name |
| 3 | Level |
| 4 | HP |
| 5 | EXP |
| 6 | GP |
| 7 | Attack multiplier |
| 8 | Hit percent |
| 9 | Attack |
| 10 | Defense multiplier |
| 11 | Evasion percent |
| 12 | Defense |
| 13 | Magic-defense multiplier |
| 14 | Magic-evasion percentage |
| 15 | Magic defense |
| 16 | Minimum speed |
| 17 | Maximum speed |
| 18 | Spell power |
| 19+ | Script values retained as strings |

Because parsing is positional, column reordering is a breaking data change even if headers remain descriptive.

### `equipment/weapons.csv`

Loaded by header name. Required headers are `name`, `type`, `atk`, `hitPercentage`, `str`, `agi`, `vit`, `wis`, `will`, and `canThrow`. Catalog lookup keys are lowercase `(name, type)`; preserve unique pairs.

### `equipment/armor.csv`

Loaded by header name. Required headers are `name`, `type`, `def`, `evade`, `magDef`, `magEvade`, `str`, `agi`, `vit`, `wis`, and `will`. Catalog lookup keys are lowercase `(name, type)`; preserve unique pairs.

### `party/*.csv`

Each character resource is referenced by one `LevelData` constant. Required headers are:

| Header | Meaning |
| --- | --- |
| `xp` | Total experience threshold for the row's level |
| `level` | Character level |
| `str`, `agi`, `vit`, `wis`, `will` | Primary stats at that level |
| `minHpGrowth`, `maxHpGrowth` | Possible HP increment represented by the row |
| `minMpGrowth`, `maxMpGrowth` | Possible MP increment represented by the row |

The code relies on complete, consistent level and XP ranges. The first represented level becomes `getStartingLevel()`. Level 70 is the baseline for post-70 primary-stat growth. HP/MP range maps are built cumulatively from the rows.

### `fe/flagVersions/*.csv`

Legacy flag specifications use positional columns:

1. canonical flag name;
2. bit offset;
3. bit width; and
4. encoded value.

Each row becomes a `Flag` in natural display/compare order.

### `fe/flagVersions/*.json`

Modern flag specifications provide a `binary` array containing objects with `name`, `offset`, `size`, and `value`, plus rule data consumed by `FlagRules`. The JSON parsers advance through an expected token layout; preserve the established structure and field ordering unless the parser is updated too.

## Dependencies

| Dependency | Role in the implementation |
| --- | --- |
| Apache Commons CSV | RFC 4180 CSV parsing and header/cell access |
| Apache Commons Text | Title-casing boss and position keys while loading `bosses.csv` |
| Guava | Immutable equipment tables, immutable/range maps, ranges, and flag ordering helpers |
| Jackson Databind/Core | Streaming parsing of modern FF4FE JSON specifications and rules |
| SLF4J API | Logging resource-load and flag-version warnings/errors |
| SLF4J Simple (test only) | Makes logs visible during tests |
| JUnit 4 (test only) | Regression tests |

The Gradle build declares runtime libraries with `implementation`, not `api`. Consumers building against public signatures that mention Guava (`LevelData` range methods) or Commons CSV (`RecordParser` constructor) may need to declare those dependencies explicitly depending on how the library is integrated.

## Existing test coverage

The JUnit suite checks:

- `Battle` equality and hash-code behavior;
- weapon and armor catalog loading;
- character base stats and growth-table values;
- key-item accessibility;
- individual flag behavior;
- readable flag parsing and normalization;
- binary flag encoding/decoding and round trips across versions; and
- modern/legacy compatibility behavior and malformed flags.

Run all checks with:

```bash
./gradlew clean test
```

Generate the JAR and all standard verification outputs with:

```bash
./gradlew build
```

## Maintenance workflows

### Change boss or enemy data

1. Edit `src/main/resources/bosses.csv` without reordering the fixed positional columns.
2. Confirm boss/position normalization gives the intended lookup strings.
3. Add a test that calls `Formation.getFor(...)` and validates representative enemy fields and script values.
4. Run the full test suite.

### Change equipment data

1. Edit the appropriate equipment CSV while retaining all required headers.
2. Ensure every lowercase `(name, type)` pair is unique.
3. Add or update lookup tests, including primary-stat modifiers.
4. Run the full test suite.

### Change character progression data

1. Edit the character CSV while preserving complete level/XP ordering and required headers.
2. If post-70 growth changes, edit the character's `GrowthTable` in `LevelData`.
3. Test starting level, representative pre-70 levels, level 70, post-70 deterministic stats, min/max stats, XP boundaries, and HP/MP ranges.
4. Run the full test suite.

### Add or change an FF4FE flag specification

1. Add/update the corresponding CSV or JSON resource.
2. For a new version, add a `FlagVersion` constant with the correct resource name, binary marker, and grouping separator.
3. Update newest-to-oldest version detection and dotted-version alias mapping.
4. Add readable-to-binary and binary-to-readable fixtures, rule implication tests, URL/seed tests, and cross-version compatibility tests.
5. Run the full test suite.

### Evolve public API safely

This project has no explicit semantic-version configuration, but consumers can compile directly against every public class and method. Preserve public names—including historical misspellings—unless making an intentional breaking change. Prefer adding overloads/new methods over changing signatures. When returning new collections, prefer defensive or unmodifiable views rather than repeating existing live-list behavior.

## Known implementation risks worth testing around

- Missing resources can yield `null` lists or empty catalogs after logged errors.
- Static catalog data can be modified through live formation/enemy/flag-spec lists.
- Out-of-range levels/XP often fail indirectly rather than through explicit validation.
- `GrowthTable.get(8)` reaches an array-bounds exception despite its custom check suggesting a curated illegal-argument error.
- Modern JSON flag parsing depends on the expected token structure/order.
- Unknown binary flag versions fall back to the latest known version.
- Equipment lookup lowercases using the JVM's default locale; unusual locale settings may affect certain strings.

When fixing one of these behaviors, add a regression test and document whether the change is backward-compatible.
