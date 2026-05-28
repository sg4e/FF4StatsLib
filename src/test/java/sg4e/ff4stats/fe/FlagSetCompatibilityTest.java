package sg4e.ff4stats.fe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class FlagSetCompatibilityTest {

    private static final String MODERN_COMPAT_STRING =
            "Kmain/summon/moon/nofree Pkey Cstandard/nofree/j:spells,abilities Twild Sstandard/no:apples Bstandard Etoggle Glife/sylph/backrow/mp -spoon -vanilla:fusoya";
    private static final String MODERN_COMPAT_BINARY =
            "bBAUAAAAAAAAAAAAAXKgAAAAAAAAAABgAAiCIIIAOAEAQ";

    @Test
    public void modernStringParsesAndEncodesToExpectedBinary() {
        FlagSet fromString = FlagSet.fromString(MODERN_COMPAT_STRING);
        assertEquals(MODERN_COMPAT_BINARY, fromString.getBinary());
        assertTrue(fromString.contains("Kmain"));
        assertTrue(fromString.contains("Knofree"));
        assertTrue(fromString.contains("Cnofree"));
        assertTrue(fromString.contains("Cj:spells"));
        assertTrue(fromString.contains("Cj:abilities"));
        assertTrue(fromString.contains("Gsylph"));
        assertTrue(fromString.contains("Gbackrow"));
        assertTrue(fromString.contains("Gmp"));
        assertTrue(fromString.contains("-spoon"));
        assertTrue(fromString.contains("-vanilla:fusoya"));
        assertFalse(fromString.hasSeed());
    }

    @Test
    public void modernBinaryParsesAndRoundtripsToCanonicalString() {
        FlagSet fromString = FlagSet.fromString(MODERN_COMPAT_STRING);
        FlagSet fromBinary = FlagSet.fromBinary(MODERN_COMPAT_BINARY);
        assertEquals(MODERN_COMPAT_BINARY, fromBinary.getBinary());
        assertFalse(fromBinary.hasSeed());
        FlagSet fromStringBinary = FlagSet.fromBinary(fromString.getBinary());

        assertEquals(fromString.toString(), fromBinary.toString());
        assertEquals(fromString.getFlags(), fromBinary.getFlags());
        assertEquals(fromString.toString(), fromStringBinary.toString());
        assertEquals(fromString.getFlags(), fromStringBinary.getFlags());
    }

    @Test
    public void legacyFormattingIsPreservedForLegacyVersions() {
        FlagSet legacy = FlagSet.fromBinary("bAAMCJCQEAMAJBa8O");
        assertEquals(legacy.toStringLegacyStyle(), legacy.toString());
    }

    @Test
    public void canonicalFormattingPathIsEnabledForModernVersion() {
        FlagSet modern = FlagSet.fromString("Kmain");
        assertEquals(modern.toStringCanonical(), modern.toString());
    }
}
