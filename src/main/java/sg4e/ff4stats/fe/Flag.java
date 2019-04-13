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

/**
 * A representation of a FF4FE flag. Flags are tied to a {@link FlagVersion}.
 * Comparing {@code Flag}s across versions throws an exception.
 * 
 * @author sg4e
 */
public class Flag implements Comparable<Flag> {
    
    private final String name;
    private final int offset;
    private final int size;
    private final int value;
    private final FlagVersion version;

    public Flag(String name, int offset, int size, int value, FlagVersion version) {
        this.name = name;
        this.offset = offset;
        this.size = size;
        this.value = value;
        this.version = version;
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

    public FlagVersion getVersion() {
        return version;
    }

    @Override
    public int compareTo(Flag o) {
        if(version != o.getVersion()) {
            throw new IllegalArgumentException("Cannot compare Flags from different versions");
        }
        return version.compare(this, o);
    }
    
}
