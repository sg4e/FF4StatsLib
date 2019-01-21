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
package sg4e.ff4stats;

import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author sg4e
 */
public class RecordParser {
    
    private final CSVRecord rec;

    public RecordParser(CSVRecord record) {
        rec = record;
    }

    public int get(int i) {
        return Integer.parseInt(rec.get(i));
    }
    
    public int get(String columnHeader) {
        return Integer.parseInt(rec.get(columnHeader));
    }
    
}
