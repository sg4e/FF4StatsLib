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

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author sg4e
 */
public class BattleTest {
    
    Battle bat;
    Battle sameBat;
    Battle diffBat1;
    Battle diffBat2;
    
    public BattleTest() {
    }
    
    @Before
    public void setUp() {
        bat = new Battle("Antlion", "Bahamut");
        //avoid string intern
        sameBat = new Battle(new String(new char[]{'A','n','t','l','i','o','n'}),
                new String(new char[] {'B','a','h','a','m','u','t'}));
        diffBat1 = new Battle("Antlion", "Wyvern");
        diffBat2 = new Battle("Wyvern", "Bahamut");
    }
    
    @Test
    public void testEquals() {
        assertEquals(bat, sameBat);
    }
    
    @Test
    public void testNotEquals() {
        assertNotEquals(bat, diffBat1);
        assertNotEquals(bat, diffBat2);
        assertNotEquals(diffBat1, diffBat2);
    }

    @Test
    public void testHashcode() {
        int hashInvocation = bat.hashCode();
        assertEquals(hashInvocation, sameBat.hashCode());
        assertEquals(hashInvocation, bat.hashCode());
    }
    
}
