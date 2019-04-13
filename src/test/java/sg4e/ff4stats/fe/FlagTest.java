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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sg4e
 */
public class FlagTest {

    @Test
    public void testFlagCsvLoading() {
        assertTrue(FlagVersion.VERSION_3_0.getAllFlags().size() > 30);
    }
    
    @Test
    public void testFlagMapping() {
        Flag startRydia = FlagVersion.VERSION_3_0.getFlagByName("-startrydia");
        //name,offset,size,value
        //-startrydia,19,4,3
        assertEquals(19, startRydia.getOffset());
        assertEquals(4, startRydia.getSize());
        assertEquals(3, startRydia.getValue());
    }
    
}
