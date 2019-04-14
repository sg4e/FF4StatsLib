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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author CaitSith2
 */
public class CSVParser {
    
    public final List<RecordParser> Records;
    
    public CSVParser(String csvfile) throws IOException {        
        List<CSVRecord> recordList = new ArrayList<>();
        List<RecordParser> recordParserList = new ArrayList<>();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(csvfile);
        Reader reader = new InputStreamReader(inputStream);
        recordList = CSVFormat.RFC4180.withHeader().parse(reader).getRecords();
        
        recordList.forEach(record -> {recordParserList.add(new RecordParser(record));});        
        Records = Collections.unmodifiableList(recordParserList);
    }
    
    
    
}
