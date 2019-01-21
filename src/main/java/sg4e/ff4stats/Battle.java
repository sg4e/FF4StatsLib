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
package sg4e.ff4stats;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.WordUtils;

/**
 *
 * @author sg4e
 */
public class Battle {
    
    private static final Map<Battle, Formation> ALL_BATTLES;
    
    static {
        Map<Battle, Formation> battleMap = new HashMap<>();
        ClassLoader classLoader = Battle.class.getClassLoader();
	File file = new File(classLoader.getResource("bosses.csv").getFile());
        try {
            Reader reader = new FileReader(file);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(reader);
            //skip header line
            records.iterator().next();
            for (CSVRecord record : records) {
                Battle battle = new Battle(WordUtils.capitalize(record.get(0)), 
                        WordUtils.capitalize(record.get(1).split("_slot")[0]));
                Formation formation = battleMap.get(battle);
                if(formation == null) {
                    formation = new Formation();
                    battleMap.put(battle, formation);
                }
                formation.addEnemy(Enemy.fromRecord(record));
            }
        }
        catch(Exception ex) {
            System.err.println("Error loading boss data file");
            ex.printStackTrace();
        }
        ALL_BATTLES = Collections.unmodifiableMap(battleMap);
    }
    
    public static Map<Battle, Formation> getAllBosses() {
        return ALL_BATTLES;
    }
    
    private final String boss, position;

    public Battle(String boss, String position) {
        this.boss = boss;
        this.position = position;
    }

    public String getBoss() {
        return boss;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.boss);
        hash = 41 * hash + Objects.hashCode(this.position);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Battle other = (Battle) obj;
        if (!Objects.equals(this.boss, other.boss)) {
            return false;
        }
        return Objects.equals(this.position, other.position);
    }

    @Override
    public String toString() {
        return boss + " @ " + position;
    }
}
