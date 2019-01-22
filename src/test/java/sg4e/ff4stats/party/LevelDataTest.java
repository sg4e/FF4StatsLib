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
    
    @Test(expected = IllegalArgumentException.class)
    public void testOutOfRangeNegative() {
        LevelData.DARK_KNIGHT_CECIL.getLevelForTotalExperience(-20);
    }
    
    @Test
    public void testLevelAt70() {
        assertEquals(new Stats(61,41,41,24,1), LevelData.DARK_KNIGHT_CECIL.getStatsForLevel(70));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testLevelAbove70() {
        LevelData.DARK_KNIGHT_CECIL.getStatsForLevel(71);
    }
    
    public void testOutOfRangeHigh() {
        assertEquals(99, LevelData.DARK_KNIGHT_CECIL.getLevelForTotalExperience(9_998_916 + 1_000_000));
    }
    
    @Test
    public void testGetStatsForLevel() {
        assertEquals(new Stats(22,17,17,9,1), LevelData.DARK_KNIGHT_CECIL.getStatsForLevel(21));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNoDataForLevel() {
        LevelData.DARK_KNIGHT_CECIL.getStatsForLevel(4);
    }
    
    @Test
    public void testDarkNightCecil() {
        assertEquals(new Stats(13,10,11,6,3), LevelData.DARK_KNIGHT_CECIL.getStatsForLevel(10));
        assertEquals(new Stats(23,17,17,10,1), LevelData.DARK_KNIGHT_CECIL.getStatsForLevel(22));
        assertEquals(new Stats(39,28,28,15,1), LevelData.DARK_KNIGHT_CECIL.getStatsForLevel(43));
        assertEquals(new Stats(3,0,0,0,0), LevelData.DARK_KNIGHT_CECIL.getGrowthTable().get(7));
    }
    
    @Test
    public void testKain() {
        assertEquals(new Stats(9,11,9,6,12), LevelData.KAIN.getStatsForLevel(10));
        assertEquals(new Stats(20,18,20,7,16), LevelData.KAIN.getStatsForLevel(22));
        assertEquals(new Stats(45,32,37,9,25), LevelData.KAIN.getStatsForLevel(43));
        assertEquals(new Stats(3,3,0,0,3), LevelData.KAIN.getGrowthTable().get(7));
    }
    
    @Test
    public void testRydia() {
        assertEquals(new Stats(6,9,5,15,9), LevelData.RYDIA.getStatsForLevel(10));
        assertEquals(new Stats(12,13,11,24,20), LevelData.RYDIA.getStatsForLevel(22));
        assertEquals(new Stats(23,22,21,41,39), LevelData.RYDIA.getStatsForLevel(43));
        assertEquals(new Stats(0,0,0,3,3), LevelData.RYDIA.getGrowthTable().get(7));
    }
    
    @Test
    public void testTellah() {
        assertEquals(new Stats(5,8,7,16,16), LevelData.TELLAH.getStatsForLevel(20));
        assertEquals(new Stats(4,9,6,16,16), LevelData.TELLAH.getStatsForLevel(22));
        assertEquals(new Stats(1,17,1,17,17), LevelData.TELLAH.getStatsForLevel(43));
        assertEquals(new Stats(0,1,0,0,1), LevelData.TELLAH.getGrowthTable().get(7));
    }
    
    @Test
    public void testEdward() {
        assertEquals(new Stats(8,12,4,9,9), LevelData.EDWARD.getStatsForLevel(10));
        assertEquals(new Stats(15,18,9,13,13), LevelData.EDWARD.getStatsForLevel(22));
        assertEquals(new Stats(31,28,17,20,20), LevelData.EDWARD.getStatsForLevel(43));
        assertEquals(new Stats(4,0,4,4,4), LevelData.EDWARD.getGrowthTable().get(7));
    }
    
    @Test
    public void testRosa() {
        assertEquals(new Stats(8,8,6,10,18), LevelData.ROSA.getStatsForLevel(10));
        assertEquals(new Stats(18,13,14,12,29), LevelData.ROSA.getStatsForLevel(22));
        assertEquals(new Stats(35,22,26,16,51), LevelData.ROSA.getStatsForLevel(43));
        assertEquals(new Stats(2,0,0,0,2), LevelData.ROSA.getGrowthTable().get(7));
    }
    
    @Test
    public void testYang() {
        assertEquals(new Stats(12,8,15,2,3), LevelData.YANG.getStatsForLevel(10));
        assertEquals(new Stats(25,14,27,2,3), LevelData.YANG.getStatsForLevel(22));
        assertEquals(new Stats(54,24,48,2,3), LevelData.YANG.getStatsForLevel(43));
        assertEquals(new Stats(2,0,2,0,0), LevelData.YANG.getGrowthTable().get(7));
    }
    
    @Test
    public void testPorom() {
        assertEquals(new Stats(7,8,5,15,10), LevelData.POROM.getStatsForLevel(10));
        assertEquals(new Stats(16,11,13,27,12), LevelData.POROM.getStatsForLevel(22));
        assertEquals(new Stats(22,18,25,48,16), LevelData.POROM.getStatsForLevel(43));
        assertEquals(new Stats(3,0,0,3,0), LevelData.POROM.getGrowthTable().get(7));
    }
    
    @Test
    public void testPalom() {
        assertEquals(new Stats(8,7,6,10,15), LevelData.PALOM.getStatsForLevel(10));
        assertEquals(new Stats(13,13,11,12,27), LevelData.PALOM.getStatsForLevel(22));
        assertEquals(new Stats(17,22,19,16,48), LevelData.PALOM.getStatsForLevel(43));
        assertEquals(new Stats(0,0,0,0,3), LevelData.PALOM.getGrowthTable().get(7));
    }
    
    @Test
    public void testPaladinCecil() {
        assertEquals(new Stats(13,15,13,9,13), LevelData.PALADIN_CECIL.getStatsForLevel(10));
        assertEquals(new Stats(22,17,21,13,18), LevelData.PALADIN_CECIL.getStatsForLevel(22));
        assertEquals(new Stats(50,27,43,23,27), LevelData.PALADIN_CECIL.getStatsForLevel(43));
        assertEquals(new Stats(3,0,0,0,0), LevelData.PALADIN_CECIL.getGrowthTable().get(7));
    }
    
    @Test
    public void testCid() {
        assertEquals(new Stats(21,9,24,5,5), LevelData.CID.getStatsForLevel(20));
        assertEquals(new Stats(23,10,26,5,5), LevelData.CID.getStatsForLevel(22));
        assertEquals(new Stats(50,18,47,5,5), LevelData.CID.getStatsForLevel(43));
        assertEquals(new Stats(3,0,3,0,0), LevelData.CID.getGrowthTable().get(7));
    }
    
    @Test
    public void testEdge() {
        assertEquals(new Stats(25,27,21,14,14), LevelData.EDGE.getStatsForLevel(30));
        assertEquals(new Stats(27,28,22,15,14), LevelData.EDGE.getStatsForLevel(32));
        assertEquals(new Stats(38,36,29,21,18), LevelData.EDGE.getStatsForLevel(43));
        assertEquals(new Stats(0,3,0,3,0), LevelData.EDGE.getGrowthTable().get(7));
    }
    
    @Test
    public void testFusoya() {
        assertEquals(new Stats(10,20,10,40,40), LevelData.FUSOYA.getStatsForLevel(50));
        assertEquals(new Stats(10,20,10,40,40), LevelData.FUSOYA.getStatsForLevel(52));
        assertEquals(new Stats(10,20,10,40,40), LevelData.FUSOYA.getStatsForLevel(63));
        assertEquals(new Stats(1,0,1,0,0), LevelData.FUSOYA.getGrowthTable().get(7));
    }
    
}
