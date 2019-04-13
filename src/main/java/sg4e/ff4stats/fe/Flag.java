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

import com.google.common.base.Functions;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import sg4e.ff4stats.RecordParser;

/**
 *
 * @author sg4e
 */
public class Flag implements Comparable<Flag> {
    
    private final String name;
    private final int offset;
    private final int size;
    private final int value;

    public Flag(String name, int offset, int size, int value) {
        this.name = name;
        this.offset = offset;
        this.size = size;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Flag o) {
        return NATURAL_ORDER.get(this) - NATURAL_ORDER.get(o);
    }
    
    private static final List<Flag> FLAG_SPEC;
    private static final Map<String, Flag> NAMES_TO_FLAGS;
    private static final Map<Flag, Integer> NATURAL_ORDER;
    
    static {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	InputStream inputStream = classLoader.getResourceAsStream("fe/flags.csv");
        List<Flag> flags = new ArrayList<>();
        List<CSVRecord> recordList = new ArrayList<>();
        try {
            Reader reader = new InputStreamReader(inputStream);
            recordList = CSVFormat.RFC4180.withHeader().parse(reader).getRecords();
        }
        catch(Exception ex) {
            System.err.println("Error loading flags.csv");
            ex.printStackTrace();
        }
        recordList.forEach(record -> {
            RecordParser p = new RecordParser(record);
            flags.add(new Flag(record.get(0), p.get(1), p.get(2), p.get(3)));
        });
        FLAG_SPEC = flags;
        NAMES_TO_FLAGS = Collections.unmodifiableMap(
                FLAG_SPEC.stream().collect(Collectors.toMap(Flag::getName, Functions.identity())));
        int index = 0;
        Map<Flag, Integer> order = new HashMap<>();
        for(Iterator<Flag> iter = FLAG_SPEC.iterator(); iter.hasNext();) {
            order.put(iter.next(), index++);
        }
        NATURAL_ORDER = Collections.unmodifiableMap(order);
    }
    
    public static List<Flag> getAllFlags() {
        return FLAG_SPEC;
    }
    
    public static Flag getFlagByName(String name) {
        return NAMES_TO_FLAGS.get(name);
    }
    
}
