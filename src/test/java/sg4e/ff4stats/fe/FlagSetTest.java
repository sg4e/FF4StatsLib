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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author sg4e
 */
@RunWith(Parameterized.class)
public class FlagSetTest {
    
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { FlagVersion.VERSION_3_0, "0.3.2", "bAAMCJCQEAMAJBa8O" }, { FlagVersion.VERSION_3_5, "0.3.6", "bAAMGJCQEAMAJCl4d" },  
        });
    }
    
    @Parameter(0)
    public FlagVersion flagVersion;
    
    @Parameter(1)
    public String versionString;
    
    @Parameter(2)
    public String binaryFlagString;

    @Test
    public void testFlagSet3_0() {
        //Ji K Ps C -rescue T4gr S4 B -whyburn Gdmwl Etf Xsbk 
        List<Flag> expectedFlags = ImmutableList.<Flag>builder()
                .add(flagVersion.getFlagByName("Ji"))
                .add(flagVersion.getFlagByName("K"))
                .add(flagVersion.getFlagByName("Ps"))
                .add(flagVersion.getFlagByName("C"))
                .add(flagVersion.getFlagByName("-rescue"))
                .add(flagVersion.getFlagByName("T4"))
                .add(flagVersion.getFlagByName("Tg"))
                .add(flagVersion.getFlagByName("Tr"))
                .add(flagVersion.getFlagByName("S4"))
                .add(flagVersion.getFlagByName("B"))
                .add(flagVersion.getFlagByName("-whyburn"))
                .add(flagVersion.getFlagByName("Gd"))
                .add(flagVersion.getFlagByName("Gm"))
                .add(flagVersion.getFlagByName("Gw"))
                .add(flagVersion.getFlagByName("Gl"))
                .add(flagVersion.getFlagByName("Et"))
                .add(flagVersion.getFlagByName("Ef"))
                .add(flagVersion.getFlagByName("Xs"))
                .add(flagVersion.getFlagByName("Xb"))
                .add(flagVersion.getFlagByName("Xk"))
                .build();
        String seed = "MAIKACUTE";
        FlagSet actual = FlagSet.fromBinary(binaryFlagString + "." + seed);
        assertEquals(expectedFlags, actual.getFlags().stream().collect(Collectors.toList()));
        assertTrue(actual.hasSeed());
        assertEquals(seed, actual.getSeed());
        assertEquals(versionString, actual.getVersion());
        assertEquals("Ji K Ps C -rescue T4gr S4 B -whyburn Gdmwl Etf Xsbk", actual.toString());
    }
    
}
