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

import com.google.common.base.Functions;
import java.util.ArrayList;
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
    VERSION_3_0("3-0"),
    VERSION_3_4("3-4"),
    VERSION_3_5("3-5"),
    VERSION_3_7("3-7");
    
    private static final HashSet<FlagVersion> triedVersions = new HashSet<>();
    private final List<Flag> flagSpec;
    private final Map<String, Flag> namesToFlags;
    private final Map<Flag, Integer> naturalOrder;
    
    private static final Logger LOG = LoggerFactory.getLogger(FlagVersion.class);
    
    public static final String latest = "0.3.7";
    public static final String earliest = "0.3.0";
    
    private FlagVersion(String filename) {
        String filePath = "fe/flagVersions/" + filename + ".csv";
        List<Flag> flags = new ArrayList<>();
        List<RecordParser> recordList = new ArrayList<>();
        try {
            recordList = new CSVParser("fe/flagVersions/" + filename + ".csv").Records;
        }
        catch(Exception ex) {
            //it's a compile-time error to call a non-constant static field in an enum's constructor
            //if this logger becomes used more than just here, move to the outside of the try block
            Logger log = LoggerFactory.getLogger(FlagVersion.class);
            log.error("Error loading flag spec data from " + filePath, ex);
        }
        recordList.forEach(record -> {
            flags.add(new Flag(record.getString(0), record.getInteger(1), 
                    record.getInteger(2), record.getInteger(3), this));
        });
        flagSpec = Collections.unmodifiableList(flags);
        namesToFlags = Collections.unmodifiableMap(
                flagSpec.stream().collect(Collectors.toMap(Flag::getName, Functions.identity())));
        int index = 0;
        Map<Flag, Integer> order = new HashMap<>();
        for(Iterator<Flag> iter = flagSpec.iterator(); iter.hasNext();) {
            order.put(iter.next(), index++);
        }
        naturalOrder = Collections.unmodifiableMap(order);
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
    
    public static FlagVersion getFromVersionString(String version) {
        switch(version) {
            default:
                LOG.warn("Unrecognized flag version {}; using latest", version);
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
