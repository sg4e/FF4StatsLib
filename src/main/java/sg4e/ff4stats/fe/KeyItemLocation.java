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
package sg4e.ff4stats.fe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static sg4e.ff4stats.fe.KeyItem.*;

/**
 *
 * @author sg4e
 */
public enum KeyItemLocation {
    //K
    START("Starting", "Start", false),
    ANTLION("Antlion", "Ant", false),
    FABUL("Fabul Guantlet", "Fabul", false),
    ORDEALS("Mt. Ordeals", "Ordls", false),
    BARON_INN("Baron Inn", "BInn", false),
    BARON_CASTLE("Baron Castle", "BCast", false, BARON_KEY),
    TOROIA("Edward in Toroia", "Troia", false),
    DARK_ELF("Dark Elf", "Delf", false, TWIN_HARP),
    ZOT("Tower of Zot", "Zot", false, EARTH),
    TOP_BABIL("Babil - Top", "BbTp", true),
    LOW_BABIL("Babil - Mid", "BbMd", true, TOWER_KEY),
    DWARF_CASTLE("Dwarf Castle", "Dwrf", true),
    SEALED_CAVE("Sealed Cave", "SdCv", true, LUCA_KEY),
    RAT_TAIL("Rat Tail", "RtTl", false, HOOK, KeyItem.RAT_TAIL),
    SHEILA_PANLESS("Sheila 1", "Shel1", true),
    SHEILA_PAN("Sheila 2", "Shel2", true, PAN),
    SUMMONED_MONSTERS_CHEST("Land of Summoned Monsters", "SM", true),
    //Kq
    ODIN("Odin", "Odin", false, BARON_KEY),
    LEVIATAN("Leviatan", "Levia", true),
    ASURA("Asura", "Asura", true),
    SYLPH("Sylph", "Sylph", true, PAN),
    BAHAMUT("Bahamut", "Baham", false, DARKNESS),
    //Km
    PALE_DIM("Pale Dim", "PlDm", false, DARKNESS),
    WYVERN("Wyvern", "Wyvn", false, DARKNESS),
    PLAGUE("Plague", "Plgue", false, DARKNESS),
    DLUNAR("D Lunar", "DLunr", false, DARKNESS),
    OGOPOGO("Ogopogo", "Ogpg", false, DARKNESS),
    //Nk
    MIST("D Mist", "Mist", false),
    //V1
    KOKKOL("Kokkol's", "Kokkl", true, ADAMANT, KeyItem.LEGEND),
    //Version 4 objective system
    OBJECTIVE("Objectives", "Obj", false),
    //K0
    ZEROMUS("Zeromus", "Zmus", false);    
    
    private final String name, abbreviation;
    private final Set<KeyItem> gatedBy;
    private final boolean inUnderworld;
    
    private KeyItemLocation(String name, String abbreviation, boolean underworld, KeyItem... gatingItems) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.inUnderworld = underworld;
        gatedBy = new HashSet<>(Arrays.asList(gatingItems));
    }

    public String getLocation() {
        return name;
    }

    public String getAbbreviatedLocation() {
        return abbreviation;
    }
    
    public boolean isInUnderworld() {
        return inUnderworld;
    }
    
    public Set<KeyItem> getRequiredItemsForAccess() {
        return new HashSet<>(gatedBy);
    }
    
    @Override
    public String toString() {
        return getLocation();
    }
    
    public static List<KeyItemLocation> getAccessibleLocations(Collection<KeyItem> keyItems) {
        List<KeyItemLocation> all = new ArrayList<>(Arrays.asList(KeyItemLocation.values()));
        if(!keyItems.contains(MAGMA_KEY) && !keyItems.contains(HOOK))
            all.removeIf(KeyItemLocation::isInUnderworld);
        if(!keyItems.contains(PASS) && !keyItems.contains(DARKNESS))
            all.remove(ZEROMUS);
        all.removeIf(loc -> !keyItems.containsAll(loc.getRequiredItemsForAccess()));
        return all;
    }
    
}
