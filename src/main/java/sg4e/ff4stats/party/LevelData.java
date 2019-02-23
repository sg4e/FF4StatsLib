/*
 * Copyright (C) 2019 sg4e
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package sg4e.ff4stats.party;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import sg4e.ff4stats.RecordParser;

/**
 *
 * @author sg4e
 */
public enum LevelData {
    DARK_KNIGHT_CECIL("Dark Knight Cecil", 200, 0, "party/DarkKnightCecil.csv", new GrowthTable(
            new Stats(0,   0,  -1,   0,   0),
            new Stats(-1,  0,  -1,  -1,   0),
            new Stats(0,  -1,  -1,  -1,   0),
            new Stats(1,   1,   1,   1,   0),
            new Stats(1,   1,   1,   1,   0),
            new Stats(1,   1,   1,   0,   0),
            new Stats(2,   2,   2,   0,   0),
            new Stats(3,   0,   0,   0,   0)
    )),
    KAIN("Kain", 190, 0, "party/Kain.csv", new GrowthTable(
            new Stats(-1,  -1,  -1,  -1,  -1),
            new Stats(0,  -1,   0,   0,  -1),
            new Stats(1,   1,   1,   0,   0),
            new Stats(1,   0,   1,   0,   1),
            new Stats(1,   0,   1,   1,   0),
            new Stats(2,   0,   2,   2,   2),
            new Stats(0,   2,   2,   0,   0),
            new Stats(3,   3,   0,   0,   3)
    )),
    RYDIA("Rydia", 30, 15, "party/Rydia.csv", new GrowthTable(
            new Stats(-1,   0,   0,  -1,  -1),
            new Stats(-1,  -1,   0,   0,  -1),
            new Stats(-1,   0,  -1,   0,  -1),
            new Stats(1,   1,   1,   1,   1),
            new Stats(1,   1,   1,   1,   1),
            new Stats(2,   0,   2,   2,   2),
            new Stats(3,   3,   0,   3,   3),
            new Stats(0,   0,   0,   3,   3)
    )),
    TELLAH("Tellah", 340, 90, "party/Tellah.csv", new GrowthTable(
            new Stats(-1,  -1,  -1,  -1,  -1),
            new Stats(0,   0,   0,   0,   0),
            new Stats(0,   0,   0,   0,   0),
            new Stats(0,   0,   0,   0,   0),
            new Stats(1,   1,   0,   0,   0),
            new Stats(0,   1,   1,   1,   1),
            new Stats(0,   1,   0,   1,   0),
            new Stats(0,   1,   0,   0,   1)
    )),
    EDWARD("Edward", 60, 0, "party/Edward.csv", new GrowthTable(
            new Stats(-1,  -1,  -1,  -1,  -1),
            new Stats(-1,  -1,  -1,  -1,  -1),
            new Stats(-1,   0,  -1,  -1,  -1),
            new Stats(-1,   0,  -1,  -1,  -1),
            new Stats(1,   1,   0,   1,   1),
            new Stats(0,   2,   2,   2,   0),
            new Stats(3,   3,   0,   0,   3),
            new Stats(4,   0,   4,   4,   4)
    )),
    ROSA("Rosa", 150, 80, "party/Rosa.csv", new GrowthTable(
            new Stats(-1,   0,  -1,   0,   0),
            new Stats(0,   0,  -1,  -1,   0),
            new Stats(1,   0,   1,   0,   1),
            new Stats(1,   1,   1,   0,   1),
            new Stats(0,   1,   1,   1,   1),
            new Stats(0,   1,   1,   1,   1),
            new Stats(1,   1,   1,   1,   1),
            new Stats(2,   0,   0,   0,   2)
    )),
    YANG("Yang", 300, 0, "party/Yang.csv", new GrowthTable(
            new Stats(-1,  -1,   0,  -1,  -1),
            new Stats(1,   1,   1,   1,   0),
            new Stats(1,   1,   1,   0,   0),
            new Stats(1,   1,   1,   0,   0),
            new Stats(1,   0,   1,   0,   1),
            new Stats(2,   2,   2,   0,   0),
            new Stats(2,   0,   2,   0,   0),
            new Stats(2,   0,   2,   0,   0)
    )),
    POROM("Porom", 100, 50, "party/Porom.csv", new GrowthTable(
            new Stats(-1,   0,   0,   0,  -1),
            new Stats(-1,   0,  -1,   0,  -1),
            new Stats(1,   1,   0,   1,   0),
            new Stats(1,   1,   1,   1,   1),
            new Stats(0,   1,   0,   1,   1),
            new Stats(0,   0,   1,   1,   0),
            new Stats(0,   0,   2,   2,   2),
            new Stats(3,   0,   0,   3,   0)
    )),
    PALOM("Palom", 110, 50, "party/Palom.csv", new GrowthTable(
            new Stats(-1,   0,   0,  -1,   0),
            new Stats(-1,   0,  -1,  -1,   0),
            new Stats(1,   1,   0,   0,   1),
            new Stats(1,   1,   1,   1,   1),
            new Stats(0,   1,   1,   1,   1),
            new Stats(0,   1,   1,   0,   1),
            new Stats(2,   0,   0,   2,   2),
            new Stats(0,   0,   0,   0,   3)
    )),
    PALADIN_CECIL("Paladin Cecil", 600, 10, "party/PaladinCecil.csv", new GrowthTable(
            new Stats(-1,  -1,   0,  -1,  -1),
            new Stats(0,   1,   1,   1,   1),
            new Stats(1,   1,   1,   1,   0),
            new Stats(1,   1,   1,   0,   1),
            new Stats(0,   0,   1,   1,   0),
            new Stats(1,   0,   1,   0,   1),
            new Stats(2,   2,   2,   0,   2),
            new Stats(3,   0,   0,   0,   0)
    )),
    CID("Cid", 788, 0, "party/Cid.csv", new GrowthTable(
            new Stats(-1,  -1,   0,   0,  -1),
            new Stats(-1,   0,   0,   0,  -1),
            new Stats(0,  -1,   0,  -1,   0),
            new Stats(1,   1,   1,   1,   0),
            new Stats(2,   2,   2,   0,   0),
            new Stats(2,   2,   0,   0,   2),
            new Stats(2,   0,   2,   0,   0),
            new Stats(3,   0,   3,   0,   0)
    )),
    EDGE("Edge", 790, 60, "party/Edge.csv", new GrowthTable(
            new Stats(-1,  -1,   0,  -1,   0),
            new Stats(1,   0,   1,   0,   0),
            new Stats(1,   0,   0,   0,   0),
            new Stats(0,   1,   0,   0,   0),
            new Stats(1,   1,   0,   1,   0),
            new Stats(1,   1,   0,   1,   0),
            new Stats(2,   0,   2,   0,   0),
            new Stats(0,   3,   0,   3,   0)
    )),
    FUSOYA("FuSoYa", 1900, 190, "party/FuSoYa.csv", new GrowthTable(
            new Stats(-1,   0,  -1,   0,   0),
            new Stats(0,   0,   0,  -1,   0),
            new Stats(0,   0,   0,   0,  -1),
            new Stats(0,   0,   0,   0,   0),
            new Stats(0,   1,   0,   1,   1),
            new Stats(0,   1,   0,   1,   0),
            new Stats(0,   1,   0,   0,   1),
            new Stats(1,   0,   1,   0,   0)
    ));
    private final String name;
    private final int startingHp, startingMp;
    private final GrowthTable growth;
    private final RangeMap<Integer, Integer> experienceToLevel;
    private final Map<Integer, Stats> levelToStats;
    private final Map<Integer, Range<Integer>> hpGains, mpGains;

    private static final String EXPERIENCE_COLUMN_HEADER = "xp";
    private static final String LEVEL_COLUMN_HEADER = "level";
    private static final String STRENGTH_COLUMN_HEADER = "str";
    private static final String AGILITY_COLUMN_HEADER = "agi";
    private static final String VITALITY_COLUMN_HEADER = "vit";
    private static final String WISDOM_COLUMN_HEADER = "wis";
    private static final String WILLPOWER_COLUMN_HEADER = "will";
    private static final String MIN_HP_COLUMN_HEADER = "minHpGrowth";
    private static final String MAX_HP_COLUMN_HEADER = "maxHpGrowth";
    private static final String MIN_MP_COLUMN_HEADER = "minMpGrowth";
    private static final String MAX_MP_COLUMN_HEADER = "maxMpGrowth";

    private LevelData(String name, int startingHp, int startingMp, String statFile, GrowthTable growth) {
        this.name = name;
        this.startingHp = startingHp;
        this.startingMp = startingMp;
        this.growth = growth;
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(statFile);
        List<CSVRecord> recordList;
        try {
            Reader reader = new InputStreamReader(inputStream);
            recordList = CSVFormat.RFC4180.withHeader().parse(reader).getRecords();
        } catch(IOException ex) {
            ex.printStackTrace();
            recordList = new ArrayList<>();
        }
        ImmutableRangeMap.Builder<Integer, Integer> mapBuilder = ImmutableRangeMap.<Integer, Integer>builder();
        ImmutableMap.Builder<Integer, Range<Integer>> hpBuilder = ImmutableMap.<Integer, Range<Integer>>builder();
        ImmutableMap.Builder<Integer, Range<Integer>> mpBuilder = ImmutableMap.<Integer, Range<Integer>>builder();
        for(int i = 0, size = recordList.size(); i < size; i ++) {
            CSVRecord record = recordList.get(i);
            int xpValue = Integer.parseInt(record.get(EXPERIENCE_COLUMN_HEADER));
            //if first data point, all values below the next record's XP value are this level
            mapBuilder.put(i == 0 ? Range.closedOpen(Integer.MIN_VALUE, Integer.parseInt(recordList.get(i + 1).get(EXPERIENCE_COLUMN_HEADER)))
                    : //if final data point, all values above this value are this level
                    i == size - 1 ? Range.closed(xpValue, Integer.MAX_VALUE)
                            : //otherwise, all values between this and the next data point are this level
                            Range.closedOpen(xpValue, Integer.parseInt(recordList.get(i + 1).get(EXPERIENCE_COLUMN_HEADER))),
                    Integer.parseInt(record.get(LEVEL_COLUMN_HEADER)));
            //I made this level :)
            readRangeToMap(record, hpBuilder, MIN_HP_COLUMN_HEADER, MAX_HP_COLUMN_HEADER);
            readRangeToMap(record, mpBuilder, MIN_MP_COLUMN_HEADER, MAX_MP_COLUMN_HEADER);
        }
        experienceToLevel = mapBuilder.build();

        levelToStats = Collections.unmodifiableMap(recordList.stream().collect(Collectors.toMap(
                rec -> Integer.parseInt(rec.get(LEVEL_COLUMN_HEADER)), rec -> {
                    RecordParser p = new RecordParser(rec);
                    return new Stats(p.get(STRENGTH_COLUMN_HEADER), p.get(AGILITY_COLUMN_HEADER), p.get(VITALITY_COLUMN_HEADER),
                            p.get(WISDOM_COLUMN_HEADER), p.get(WILLPOWER_COLUMN_HEADER));
                })));
        hpGains = hpBuilder.build();
        mpGains = mpBuilder.build();
    }
    
    private static void readRangeToMap(CSVRecord record, ImmutableMap.Builder<Integer, Range<Integer>> map, String minHeader, String maxHeader) {
        map.put(Integer.parseInt(record.get(LEVEL_COLUMN_HEADER)), Range.<Integer>closed(Integer.parseInt(record.get(minHeader)), 
                Integer.parseInt(record.get(maxHeader))));
    }

    public int getLevelForTotalExperience(int totalXp) {
        if(totalXp < 0) {
            throw new IllegalArgumentException("EXP value cannot be negative");
        }
        return experienceToLevel.get(totalXp);
    }

    /**
     * Returns stats for level or throws {@code IllegalArgumentException} if
     * level is greater than 70 or below the level at which the character joins
     * the party.
     *
     * @param level
     * @return
     */
    public Stats getStatsForLevel(int level) {
        if(level > 70) {
            throw new IllegalArgumentException(level + " is greater than 70. Stat growth past level 70 is nondeterministic");
        }
        Stats stats = levelToStats.get(level);
        if(stats == null) {
            throw new IllegalArgumentException(level + " is below the minimum level of this party member: "
                    + levelToStats.keySet().stream().min(Integer::compareTo).get());
        }
        return stats;
    }

    public Stats getStatsForTotalExperience(int totalXp) {
        return getStatsForLevel(getLevelForTotalExperience(totalXp));
    }

    @Override
    public String toString() {
        return name;
    }

    public GrowthTable getGrowthTable() {
        return growth;
    }
    
    public int getStartingLevel() {
        return getLevelForTotalExperience(0);
    }
    
    public int getMinimumXpForLevel(int level) {
        return Math.max(0, experienceToLevel.asMapOfRanges().entrySet().stream().filter(entry -> entry.getValue() == level)
                .findAny().orElseThrow(() -> new IllegalArgumentException("Not a valid level for this character"))
                .getKey().lowerEndpoint());
    }
    
    public int getMaximumXpForLevel(int level) {
        return experienceToLevel.asMapOfRanges().entrySet().stream().filter(entry -> entry.getValue() == level)
                .findAny().orElseThrow(() -> new IllegalArgumentException("Not a valid level for this character"))
                .getKey().upperEndpoint();
    }
    
    public int getStartingHp() {
        return startingHp;
    }
    
    public int getStartingMp() {
        return startingMp;
    }
    
    public Range<Integer> getHpRangeAtLevel(int level) {
        return getAdditiveRange(startingHp, hpGains, level);
    }
    
    public Range<Integer> getMpRangeAtLevel(int level) {
        return getAdditiveRange(startingMp, mpGains, level);
    }
    
    private static Range<Integer> getAdditiveRange(int starting, Map<Integer, Range<Integer>> map, int upToInclusive) {
        AtomicInteger low = new AtomicInteger();
        AtomicInteger high = new AtomicInteger();
        IntStream.rangeClosed(0, upToInclusive).parallel().boxed().map(i -> map.get(i)).filter(Objects::nonNull).forEach(r -> {
            low.addAndGet(r.lowerEndpoint());
            high.addAndGet(r.upperEndpoint());
        });
        return Range.<Integer>closed(starting + low.get(), starting + high.get());
    }

}
