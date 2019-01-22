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

/**
 *
 * Stat-increase table for levels above 70.
 *
 * @author sg4e
 */
public class GrowthTable {

    private final Stats[] growthTable;

    public GrowthTable(Stats s1, Stats s2, Stats s3, Stats s4, Stats s5, Stats s6, Stats s7, Stats s8) {
        growthTable = new Stats[8];
        growthTable[0] = s1;
        growthTable[1] = s2;
        growthTable[2] = s3;
        growthTable[3] = s4;
        growthTable[4] = s5;
        growthTable[5] = s6;
        growthTable[6] = s7;
        growthTable[7] = s8;
    }

    public Stats get(int index) {
        if(index < 0 || index > growthTable.length) {
            throw new IllegalArgumentException("Illegal index: " + index + ". Must be an integer [0, " + growthTable.length + ")");
        }
        return growthTable[index];
    }

}
