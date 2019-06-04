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

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author CaitSith2
 */
@RunWith(Parameterized.class)
public class FlagSetBinaryTest {
    //The only versions that can be tested are the latest version itself, and the latest version of the flagset that has
    //a given flag before it was removed.  As a result, there is no way to test 0.3.4 or 0.3.5, as neither of those versions have
    //flags that are NOT present in the latest version.
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "bAAMDJCQEAMAJBa-OAQ", "Ji K Ps C -rescue T4gr S4 B -whyburn Gdmwl Etf Xsbk -myeyes -mute" }, 
            { "bAAMIJCQEAMAJCl47", "Ji K Ps C -rescue T4gr S4 B -whyburn Gdmwl Etdf Xsbk" },
        });
    }
    
    @Parameterized.Parameter(0)
    public String binaryFlagString;
    
    @Parameterized.Parameter(1)
    public String flagString;
    
    @Test
    public void testBinaryFlagEncoding() {
        FlagSet binaryTest = FlagSet.fromString(flagString);
        assertEquals(binaryTest.getBinary(), binaryFlagString);
        assertFalse(binaryTest.hasSeed());
    }
    
    @Test
    public void testBinaryFlagDecoding() {
        FlagSet binaryTest = FlagSet.fromBinary(binaryFlagString);
        assertEquals(binaryTest.toString(), flagString);
        assertFalse(binaryTest.hasSeed());
    }
}
