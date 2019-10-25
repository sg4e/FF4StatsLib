/*
 * Copyright (C) 2019 
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.base.Functions;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg4e.ff4stats.csv.*;

/**
 *
 * @author sg4e
 */
public enum FlagVersion {
    VERSION_3_0("3-0", "bAAMD",""),
    VERSION_3_4("3-4", "bAAME",""),
    VERSION_3_5("3-5", "bAAMG",""),
    VERSION_3_7("3-7", "bAAMI",""),
    VERSION_4_0_0("4-0-0", "bBAAA","/");
    
    private final Logger log = LoggerFactory.getLogger(FlagVersion.class);
    private static final HashSet<FlagVersion> triedVersions = new HashSet<>();
    private final List<Flag> flagSpec;
    private final Map<String, Flag> namesToFlags;
    private final Map<Flag, Integer> naturalOrder;
    private final String binaryVersion;
    private final String seperator;
    private final FlagRules flagRules;
    private JsonParser parser;
    
    private static final Logger LOG = LoggerFactory.getLogger(FlagVersion.class);
    
    public static final String latest = "4.0.0";
    public static final String earliest = "0.3.0";
    
    private FlagVersion(String filename, String binaryVersion, String seperator) {
        String filePath = "fe/flagVersions/" + filename;
        List<Flag> flags = parseCSV(filename);
        FlagRules fr = new FlagRules();
        if(flags == null)
        {
            flags = parseJSON(filename);
            if(flags == null) {
                log.error("Could not find file: " + filename);
                flags = new ArrayList<>();
            }
        }
        
        flagSpec = Collections.unmodifiableList(flags);
        namesToFlags = Collections.unmodifiableMap(
                flagSpec.stream().collect(Collectors.toMap(Flag::getName, Functions.identity())));
        int index = 0;
        Map<Flag, Integer> order = new HashMap<>();
        for(Iterator<Flag> iter = flagSpec.iterator(); iter.hasNext();) {
            order.put(iter.next(), index++);
        }
        naturalOrder = Collections.unmodifiableMap(order);
        this.binaryVersion = binaryVersion;
        this.seperator = seperator;
        FlagSet flagset;
        try {
            if(parser != null) {
                fr.parseJson(this, parser);
            }            
        }
        catch (Exception ex) {
            log.error("Error parsing rules for " + filePath, ex);
            fr = new FlagRules();
        }
        flagRules = fr;
    }
    
    private List<Flag> parseCSV(String filename) {
        String filePath = "fe/flagVersions/" + filename + ".csv";
        List<Flag> flags = new ArrayList<>();
        List<RecordParser> recordList = new ArrayList<>();
        try {
            recordList = new CSVParser(filePath).Records;
            if(recordList == null)
                return null;
        }
        catch(Exception ex) {
            log.error("Error loading flag spec data from " + filePath, ex);
            return null;
        }
        recordList.forEach(record -> {
            flags.add(new Flag(record.getString(0), record.getInteger(1), 
                    record.getInteger(2), record.getInteger(3), this));
        });
        return flags;
    }
    
    private List<Flag> parseJSON(String filename) {
        String filePath = "fe/flagVersions/" + filename + ".json"; 
        List<Flag> flags = new ArrayList<>();
        
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource(filePath);
            
            JsonFactory factory = new JsonFactory();
            parser = factory.createParser(resource);
            
            JsonToken nextToken = parser.nextToken();
            
            while (!JsonToken.START_ARRAY.equals(nextToken) || !"binary".equals(parser.getCurrentName()))
                nextToken = parser.nextToken();
            
            while (true) {
                nextToken = parser.nextToken(); // {
                if(!JsonToken.START_OBJECT.equals(nextToken)) {
                    return flags;
                }
                
                parser.nextToken(); // "name":"flag"
                parser.nextToken();
                String name = parser.getValueAsString();
                
                parser.nextToken(); // "offset": 0
                parser.nextToken();
                int offset = parser.getValueAsInt();
                
                parser.nextToken(); // "size": 0
                parser.nextToken();
                int size = parser.getValueAsInt();
                
                parser.nextToken(); // "value": 0
                parser.nextToken();
                int value = parser.getValueAsInt();
                flags.add(new Flag(name, offset, size, value, this));
                
                parser.nextToken(); // )
            }

        } catch (Exception ex) {
            log.error("Error loading flag spec data from " + filePath, ex);
            return null;
        }
    }
    
    public FlagRules getFlagRules() {
        return flagRules;
    }
    
    public String getSeperator() {
        return seperator;
    }
     
   public String getBinaryFlagVersion() {
        return binaryVersion;
    }
    
    int compare(Flag a, Flag b) {
        return naturalOrder.get(a) - naturalOrder.get(b);
    }
    
    public List<Flag> getAllFlags() {
        return flagSpec;
    }
    
    public Flag getFlagByName(String name) {
        return namesToFlags.get(name);
    }
    
    public String getVersion() {
        byte[] versionBytes = Base64.getUrlDecoder().decode(binaryVersion.substring(1));
        int[] versionInts = new int[versionBytes.length];
        for(int i = 0; i < versionBytes.length; i++)
            versionInts[i] = (int) versionBytes[i];
        return Arrays.stream(versionInts).mapToObj(Integer::toString).collect(Collectors.joining("."));
    }
    
    public static Flag getFlagFromFlagString(FlagVersion version, String flag, Flag previousFlag) {        
        for(Flag f : version.getAllFlags()) {
            if((previousFlag != null && previousFlag == f) || (flag.startsWith("-") && !flag.equals(f.getName())))
                continue;
            if(flag.startsWith(f.getName())) {
                if(previousFlag != null || flag.equals(f.getName()))
                    triedVersions.clear();
                return f;
            }
        }
        
        LOG.warn("Failed to find flag: {}", flag);
        triedVersions.add(version);
        return null;
    }
    
    public static FlagVersion getVersionFromFlagString(String flag) {
        if(getFlagFromFlagString(VERSION_4_0_0, flag, null) != null && !triedVersions.contains(VERSION_4_0_0))
            return VERSION_4_0_0;
        if(getFlagFromFlagString(VERSION_3_7, flag, null) != null && !triedVersions.contains(VERSION_3_7))
            return VERSION_3_7;
        if(getFlagFromFlagString(VERSION_3_5, flag, null) != null && !triedVersions.contains(VERSION_3_5))
            return VERSION_3_5;
        if(getFlagFromFlagString(VERSION_3_4, flag, null) != null && !triedVersions.contains(VERSION_3_4))
            return VERSION_3_4;
        if(getFlagFromFlagString(VERSION_3_0, flag, null) != null && !triedVersions.contains(VERSION_3_0))
            return VERSION_3_0;
        
        triedVersions.clear();
        return null;
    }
    
    /**
     * Returns the FlagVersion associated with the provided version string. If
     * the version is unrecognized/unsupported, the latest FlagVersion is
     * returned. This behavior allows for the library to support future versions
     * that do not alter the flag specification without needing recoding;
     * however, newer flag specification that are not yet supported will cause
     * errors elsewhere.
     * 
     * @param version the version string decoded from the FF4FE binary flag
     * representation
     * @return 
     */
    
    public static FlagVersion getFromVersionString(String version) {
        switch(version) {
            default:
                LOG.warn("Unrecognized flag version {}; using latest", version);
            case "4.0.0":
                return VERSION_4_0_0;
            case "0.3.8":
            case "0.3.7":
                return VERSION_3_7;
            case "0.3.6":
            case "0.3.5":
                return VERSION_3_5;
            case "0.3.4":
                return VERSION_3_4;
            case "0.3.3":
            case "0.3.2":
            case "0.3.1":
            case "0.3.0":
            case "0.3":
                return VERSION_3_0;
            
            
        }
    }
    
}
