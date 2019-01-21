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
public class LevelDataTest {

    @Test
    public void testBasicCsvLoading() {
        assertEquals(31, LevelData.DARK_KNIGHT_CECIL.getLevelForTotalExperience(156823));
    }
    
    @Test
    public void testOutOfRangeLow() {
        assertEquals(10, LevelData.DARK_KNIGHT_CECIL.getLevelForTotalExperience(1));
    }
    
    @Test
    public void testOutOfRangeNegative() {
        assertEquals(10, LevelData.DARK_KNIGHT_CECIL.getLevelForTotalExperience(-20));
    }
    
    @Test
    public void testOutOfRangeHigh() {
        assertEquals(99, LevelData.DARK_KNIGHT_CECIL.getLevelForTotalExperience(9_998_916 + 1_000_000));
    }
    
    @Test
    public void testGetStatsForLevel() {
        assertEquals(new Stats(22,17,17,9,1), LevelData.DARK_KNIGHT_CECIL.getStatsForLevel(21));
    }
    
    @Test
    public void testNoDataForLevel() {
        assertNull(LevelData.DARK_KNIGHT_CECIL.getStatsForLevel(4));
    }
    
}
