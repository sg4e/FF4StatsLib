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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sg4e
 */
public class ArmorTest {
    
    @Test
    public void testArmorLoading() {
        //name,type,def,evade,magDef,magEvade,str,agi,vit,wis,will
        //Aegis,shield,4,34,5,4,0,0,0,3,0
        Armor aegis = Armor.getArmor("aegis", "shield");
        assertEquals("Aegis", aegis.getName());
        assertEquals("shield", aegis.getType());
        assertEquals(4, aegis.getDefense());
        assertEquals(34, aegis.getEvasion());
        assertEquals(5, aegis.getMagicDefense());
        assertEquals(4, aegis.getMagicEvasion());
        assertEquals(new Stats(0,0,0,3,0), aegis.getStats());
    }
    
}
