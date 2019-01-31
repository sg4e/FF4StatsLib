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

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sg4e
 */
public class FlagSetTest {

    @Test
    public void testFlagSet() {
        //Ji K Ps C -rescue T4gr S4 B -whyburn Gdmwl Etf Xsbk 
        List<Flag> expectedFlags = ImmutableList.<Flag>builder()
                .add(Flag.getFlagByName("Ji"))
                .add(Flag.getFlagByName("K"))
                .add(Flag.getFlagByName("Ps"))
                .add(Flag.getFlagByName("C"))
                .add(Flag.getFlagByName("-rescue"))
                .add(Flag.getFlagByName("T4"))
                .add(Flag.getFlagByName("Tg"))
                .add(Flag.getFlagByName("Tr"))
                .add(Flag.getFlagByName("S4"))
                .add(Flag.getFlagByName("B"))
                .add(Flag.getFlagByName("-whyburn"))
                .add(Flag.getFlagByName("Gd"))
                .add(Flag.getFlagByName("Gm"))
                .add(Flag.getFlagByName("Gw"))
                .add(Flag.getFlagByName("Gl"))
                .add(Flag.getFlagByName("Et"))
                .add(Flag.getFlagByName("Ef"))
                .add(Flag.getFlagByName("Xs"))
                .add(Flag.getFlagByName("Xb"))
                .add(Flag.getFlagByName("Xk"))
                .build();
        String seed = "MAIKACUTE";
        FlagSet actual = FlagSet.fromBinary("bAAMCJCQEAMAJBa8O." + seed);
        assertEquals(expectedFlags, actual.getFlags().stream().collect(Collectors.toList()));
        assertTrue(actual.hasSeed());
        assertEquals(seed, actual.getSeed());
        assertEquals("0.3.2", actual.getVersion());
        assertEquals("Ji K Ps C -rescue T4gr S4 B -whyburn Gdmwl Etf Xsbk", actual.toString());
    }
    
}
