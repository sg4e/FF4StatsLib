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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author sg4e
 */
public class FlagSet {
    
    private final static Pattern BINARY_FLAGSET_PATTERN = Pattern.compile(
            "b(?<version>[A-Za-z0-9_\\-]{4})" + // Version
            "(?<flags>[A-Za-z0-9_\\-]*)" +      // Flags
            "(?:\\.(?<seed>[A-Z0-9]{1,10})" +   // Seed
            "(?:\\.test\\.[0-9a-f]{8})?)?");    // Handler for test seeds
    
    private final TreeSet<Flag> flags = new TreeSet<>();
    private String version, binary;
    private String seed = "";
    private String readableString = "uninitialized";
    
    private FlagSet() {
        
    }

    public String getVersion() {
        return version;
    }

    private void setVersion(String version) {
        this.version = version;
    }

    public String getSeed() {
        return seed;
    }

    private void setSeed(String seed) {
        this.seed = seed;
    }
    
    public boolean hasSeed() {
        return seed != null && !"".equals(seed);
    }

    public String getBinary() {
        return binary;
    }

    private void setBinary(String binary) {
        this.binary = binary;
    }
    
    private void add(Flag flag) {
        flags.forEach(f -> {
            if(flag.getOffset() == f.getOffset() && flag.getValue() != f.getValue()) {
                throw new IllegalArgumentException("Error: Flag " + flag.getName() + " conflicts with already-set flag " + f.getName());
            }
        });
        flags.add(flag);
    }

    private void setReadableString(String readableString) {
        this.readableString = readableString;
    }
    
    @Override
    public String toString() {
        return readableString;
    }
    
    private String sorted() {
        //make string representation
        StringBuilder s = new StringBuilder();
        String lastFlag = "";
        for(Flag f : getFlags()) {
            String currentFlag = f.getName();
            char first = currentFlag.charAt(0);
            if(first != '-' && lastFlag.startsWith(first + "")) {
                s.append(currentFlag.substring(1));
            }
            else {
                if(s.length() != 0)
                    s.append(" ");
                s.append(currentFlag);
            }
            lastFlag = currentFlag;
        }
        
        return s.toString();
    }
    
    public Boolean contains(String flagString) {
        for(Flag flag : flags)
            if(flag.getName().equals(flagString))
                return true;
        return false;
    }
    
    public NavigableSet<Flag> getFlags() {
        return Collections.unmodifiableNavigableSet(flags);
    }
    
    public String toSeedUrl() {
        return "http://ff4fe.com/get?id=" + getBinary();
    }
    
    public String toFlagUrl() {
        return "http://ff4fe.com/make?flags=" + toString().replaceAll(" ", "+");
    }
    
    public static FlagSet fromUrl(String url) {
        return fromBinary(url);
    }
    
    public static FlagSet fromString(String text) {
       if(BINARY_FLAGSET_PATTERN.matcher(text).find())
           return fromBinary(text);
        
        FlagVersion version = FlagVersion.getFromVersionString(FlagVersion.latest);        
        
        String[] parts = text.split(" ");
        List<Flag> allFlags = version.getAllFlags();
        FlagSet flagSet = new FlagSet();
        HashSet<String> flagStrings = new HashSet<>();
        HashSet<String> incompatibleFlags = new HashSet<>();
        
        for(String part : parts) {
            Flag previousFlag = null;
            while(part.length() > 0) {
                Flag flag = FlagVersion.getFlagFromFlagString(version, part, previousFlag);
                if(flag == null) {
                    version = FlagVersion.getVersionFromFlagString(part);
                    previousFlag = null;
                    if(version == null)
                        throw new IllegalArgumentException("Error: Unrecognized flag: " + part);
                    flag = FlagVersion.getFlagFromFlagString(version, part, previousFlag);
                    incompatibleFlags.add(flag.getName());
                }
                flagStrings.add(flag.getName());
                if(flag.getName().length() == 1)
                    previousFlag = flag;
                if(part.equals(flag.getName()))
                    part = "";
                else {
                    if(part.startsWith("-"))
                        throw new IllegalArgumentException("Error: Unrecognized flag: " + part);
                    part = part.substring(0,1) + part.substring(flag.getName().length());
                }
            }
        }
        
        for (String part : flagStrings) {
            Flag flag = version.getFlagByName(part);
            if(flag == null) {
                incompatibleFlags.add(part);
                throw new IllegalArgumentException("Error: Incompatible flags specified: " + String.join(", ", incompatibleFlags));
            }
            flagSet.add(flag);
        }
        flagSet.setReadableString(flagSet.sorted());
        
        return flagSet;
    }
    
    public static FlagSet fromBinary(String binary) {
        //first character is 'b'; '+' is encoded as '-' and '/' as '_'
        Matcher matcher = BINARY_FLAGSET_PATTERN.matcher(binary);
        
        if(!matcher.find())
            throw new IllegalArgumentException("Not a binary flag string");
        String cleaned = binary.trim().substring(1);
        //next 4 chars encode version
        byte[] versionBytes = Base64.getUrlDecoder().decode(matcher.group("version"));
        int[] versionInts = new int[versionBytes.length];
        for(int i = 0; i < versionBytes.length; i++)
            versionInts[i] = (int) versionBytes[i];
        FlagSet flagSet = new FlagSet();
        flagSet.setBinary(binary);
        flagSet.setVersion(Arrays.stream(versionInts).mapToObj(Integer::toString).collect(Collectors.joining(".")));
        //next is {flags}.{seed}        
        byte[] flagStringDecoded = Base64.getUrlDecoder().decode(matcher.group("flags"));
        flagSet.setSeed(matcher.group("seed"));
        //check all the flags; a bunch of bitwise ops
        FlagVersion.getFromVersionString(flagSet.getVersion()).getAllFlags().forEach(f -> {
            int decodedValue = 0;
            int lowByteIndex = f.getOffset() >> 3;
            if(lowByteIndex < flagStringDecoded.length) {
                int lowByteShift = f.getOffset() & 7;
                decodedValue = flagStringDecoded[lowByteIndex] >> lowByteShift;
                int numOverflowBytes = ((f.getSize() - 1) >> 3) + 1;
                for(int i = 1; i <= numOverflowBytes; i++) {
                    if(lowByteIndex + i >= flagStringDecoded.length)
                        break;
                    decodedValue |= flagStringDecoded[lowByteIndex + i] << (8 * i - lowByteShift);
                }
                int mask = (1 << f.getSize()) - 1;
                decodedValue &= mask;
            }
            if(decodedValue == f.getValue())
                flagSet.add(f);
        });
        flagSet.setReadableString(flagSet.sorted());
        
        return flagSet;
    }
    
}
