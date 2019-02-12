/*
 * Copyright (C) 2019 
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
package sg4e.ff4stats.fe;

/**
 *
 * @author sg4e
 */
public enum KeyItem {
    CRYSTAL("Crystal"),
    PASS("Pass"),
    HOOK("Hook"),
    DARKNESS("Darkness Crystal"),
    EARTH("Earth Crystal"),
    TWIN_HARP("Twin Harp"),
    PACKAGE("Package"),
    SAND_RUBY("Sand Ruby"),
    BARON_KEY("Baron Key"),
    MAGMA_KEY("Magma Key"),
    TOWER_KEY("Tower Key"),
    LUCA_KEY("Luca Key"),
    ADAMANT("Adamant"),
    LEGEND("Legend Sword"),
    PAN("Pan"),
    SPOON("Spoon"),
    RAT_TAIL("Rat Tail"),
    PINK_TAIL("Pink Tail");
    
    private final String name;
    
    private KeyItem(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
