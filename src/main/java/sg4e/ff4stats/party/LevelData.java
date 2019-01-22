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

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import sg4e.ff4stats.RecordParser;

/**
 *
 * @author sg4e
 */
public enum LevelData {
    DARK_KNIGHT_CECIL("Dark Knight Cecil", 200, 0, "party/DarkKnightCecil.csv"),
    KAIN("Kain", 190, 0, "party/Kain.csv"),
    RYDIA("Rydia", 30, 15, "party/Rydia.csv"),
    TELLAH("Tellah", 340, 90, "party/Tellah.csv"),
    EDWARD("Edward", 60, 0, "party/Edward.csv"),
    ROSA("Rosa", 150, 80, "party/Rosa.csv"),
    YANG("Yang", 300, 0, "party/Yang.csv"),
    POROM("Porom", 100, 50, "party/Porom.csv"),
    PALOM("Palom", 110, 50, "party/Palom.csv"),
    PALADIN_CECIL("Paladin Cecil", 600, 10, "party/PaladinCecil.csv"),
    CID("Cid", 788, 0, "party/Cid.csv"),
    EDGE("Edge", 790, 60, "party/Edge.csv"),
    FUSOYA("FuSoYa", 1900, 190, "party/FuSoYa.csv");
    private final String name;
    private final int startingHp, startingMp;
    private final RangeMap<Integer, Integer> experienceToLevel;
    private final Map<Integer, Stats> levelToStats;

    private static final String EXPERIENCE_COLUMN_HEADER = "xp";
    private static final String LEVEL_COLUMN_HEADER = "level";
    private static final String STRENGTH_COLUMN_HEADER = "str";
    private static final String AGILITY_COLUMN_HEADER = "agi";
    private static final String VITALITY_COLUMN_HEADER = "vit";
    private static final String WISDOM_COLUMN_HEADER = "wis";
    private static final String WILLPOWER_COLUMN_HEADER = "will";

    private LevelData(String name, int startingHp, int startingMp, String statFile) {
        this.name = name;
        this.startingHp = startingHp;
        this.startingMp = startingMp;
        File file = new File(ClassLoader.getSystemClassLoader().getResource(statFile).getFile());
        List<CSVRecord> recordList;
        try {
            Reader reader = new FileReader(file);
            recordList = CSVFormat.RFC4180.withHeader().parse(reader).getRecords();
        } catch(IOException ex) {
            ex.printStackTrace();
            recordList = new ArrayList<>();
        }
        ImmutableRangeMap.Builder<Integer, Integer> mapBuilder = ImmutableRangeMap.<Integer, Integer>builder();
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
        }
        experienceToLevel = mapBuilder.build();

        levelToStats = Collections.unmodifiableMap(recordList.stream().collect(Collectors.toMap(
                rec -> Integer.parseInt(rec.get(LEVEL_COLUMN_HEADER)), rec -> {
                    RecordParser p = new RecordParser(rec);
                    return new Stats(p.get(STRENGTH_COLUMN_HEADER), p.get(AGILITY_COLUMN_HEADER), p.get(VITALITY_COLUMN_HEADER),
                            p.get(WISDOM_COLUMN_HEADER), p.get(WILLPOWER_COLUMN_HEADER));
                })));
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

}
