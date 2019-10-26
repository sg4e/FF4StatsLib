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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static sg4e.ff4stats.fe.KeyItem.*;
import static sg4e.ff4stats.fe.KeyItemLocation.*;

/**
 *
 * @author sg4e
 */
public class KeyItemLocationTest {
    
    List<KeyItemLocation> expected;
    
    @Before
    public void setup() {
        
        expected = new ArrayList<>(Arrays.asList(new KeyItemLocation[] {START, ANTLION, FABUL, ORDEALS, BARON_INN, TOROIA}));
    }
    
    @Test
    public void testBasicProgression() {
        expected.addAll(Arrays.asList(new KeyItemLocation[] {MIST, OBJECTIVE}));
        List<KeyItem> kis = Arrays.asList(new KeyItem[] {});
        assertEquals(expected, KeyItemLocation.getAccessibleLocations(kis));
    }
    
    @Test
    public void testUnderworld() {
        expected.addAll(Arrays.asList(new KeyItemLocation[] {TOP_BABIL, DWARF_CASTLE, SHEILA_PANLESS, SUMMONED_MONSTERS_CHEST, LEVIATAN, ASURA, MIST, OBJECTIVE}));
        List<KeyItem> kis1 = Arrays.asList(new KeyItem[] {HOOK});
        assertEquals(expected, getAccessibleLocations(kis1));
        List<KeyItem> kis2 = Arrays.asList(new KeyItem[] {MAGMA_KEY});
        assertEquals(expected, getAccessibleLocations(kis2));
    }
    
    @Test
    public void testMoon() {
        expected.addAll(Arrays.asList(new KeyItemLocation[] {BAHAMUT, PALE_DIM, WYVERN, PLAGUE, DLUNAR, OGOPOGO, MIST, OBJECTIVE, ZEROMUS}));
        List<KeyItem> kis = Arrays.asList(new KeyItem[] {DARKNESS});
        assertEquals(expected, getAccessibleLocations(kis));
    }
    
    @Test
    public void testPass() {
        expected.addAll(Arrays.asList(new KeyItemLocation[] {MIST, OBJECTIVE, ZEROMUS}));
        List<KeyItem> kis = Arrays.asList(new KeyItem[] {PASS});
        assertEquals(expected, getAccessibleLocations(kis));
    }
    
    @Test
    public void testForge() {
        expected.addAll(Arrays.asList(new KeyItemLocation[] {TOP_BABIL, DWARF_CASTLE, SHEILA_PANLESS, SUMMONED_MONSTERS_CHEST, LEVIATAN, ASURA, MIST, KOKKOL, OBJECTIVE}));
        List<KeyItem> kis = Arrays.asList(new KeyItem[] {MAGMA_KEY, ADAMANT, LEGEND});
        assertEquals(expected, getAccessibleLocations(kis));
    }
    
    @Test
    public void testKeyItem() {
        expected.addAll(Arrays.asList(new KeyItemLocation[] {ZOT, MIST, OBJECTIVE}));
        List<KeyItem> kis = Arrays.asList(new KeyItem[] {EARTH});
        assertEquals(expected, getAccessibleLocations(kis));
    }
    
}
