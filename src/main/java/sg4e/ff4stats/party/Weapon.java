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

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import sg4e.ff4stats.RecordParser;

/**
 *
 * @author sg4e
 */
public class Weapon implements Equipment {
    
    private final String name, type;
    private final int atk, hitPercentage;
    private final boolean throwable;
    private final Stats stats;
    
    private static final String WEAPON_FILE = "equipment/weapons.csv";
    private static final Set<Weapon> ALL_WEAPONS;
    private static final Table<String, String, Weapon> WEAPON_TABLE;
    
    static {
        Set<Weapon> weapons = new HashSet<>();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(WEAPON_FILE);
        List<CSVRecord> recordList;
        try {
            Reader reader = new InputStreamReader(inputStream);
            recordList = CSVFormat.RFC4180.withHeader().parse(reader).getRecords();
        } catch(IOException ex) {
            ex.printStackTrace();
            recordList = new ArrayList<>();
        }
        recordList.forEach(rec -> {
            RecordParser p = new RecordParser(rec);
            //name,type,atk,hitPercentage,str,agi,vit,wis,will,canThrow
            Weapon w = new Weapon(rec.get(0), rec.get(1), p.get(2), p.get(3),
                    new Stats(p.get(4), p.get(5), p.get(6), p.get(7), p.get(8)), Boolean.parseBoolean(rec.get(9)));
            weapons.add(w);
        });
        ALL_WEAPONS = Collections.unmodifiableSet(weapons);
        
        ImmutableTable.Builder<String, String, Weapon> table = new  ImmutableTable.Builder<>();
        ALL_WEAPONS.forEach(w -> {
            table.put(w.getName().toLowerCase(), w.getType().toLowerCase(), w);
        });
        WEAPON_TABLE = table.build();
    }

    public Weapon(String name, String type, int atk, int hitPercentage, Stats stats, boolean throwable) {
        this.name = name;
        this.type = type;
        this.atk = atk;
        this.hitPercentage = hitPercentage;
        this.throwable = throwable;
        this.stats = stats;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Stats getStats() {
        return stats;
    }

    public int getAttack() {
        return atk;
    }

    public int getHitPercentage() {
        return hitPercentage;
    }

    public boolean isThrowable() {
        return throwable;
    }
    
    public static Weapon getWeapon(String name, String type) {
        return WEAPON_TABLE.get(name.toLowerCase(), type.toLowerCase());
    }
    
}
