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
public class WeaponTest {

    @Test
    public void testWeaponLoading() {
        //name,type,atk,hitPercentage,str,agi,vit,wis,will,canThrow
        //Crystal,sword,200,99,15,0,15,0,15,FALSE
        Weapon c = Weapon.getWeapon("crystal", "sword");
        assertEquals("Crystal", c.getName());
        assertEquals("sword", c.getType());
        assertEquals(200, c.getAttack());
        assertEquals(99, c.getHitPercentage());
        assertFalse(c.isThrowable());
        assertEquals(new Stats(15,0,15,0,15), c.getStats());
    }
    
}
