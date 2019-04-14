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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg4e.ff4stats.RecordParser;
import sg4e.ff4stats.CSVParser;

/**
 *
 * @author sg4e
 */
public enum FlagVersion {
    VERSION_3_0("3-0"),
    VERSION_3_4("3-4"),
    VERSION_3_5("3-5");
    
    private final List<Flag> flagSpec;
    private final Map<String, Flag> namesToFlags;
    private final Map<Flag, Integer> naturalOrder;
    
    private static final Logger LOG = LoggerFactory.getLogger(FlagVersion.class);
    
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
    public static FlagVersion getFromVersionString(String version) {
        switch(version) {
            case "0.3":
            case "0.3.0":
            case "0.3.1":
            case "0.3.2":
            case "0.3.3":
                return VERSION_3_0;
            case "0.3.4":
                return VERSION_3_4;
            default:
                LOG.warn("Unrecognized flag version {}; using latest", version);
            case "0.3.5":
            case "0.3.6":
                return VERSION_3_5;
        }
    }
    
}
