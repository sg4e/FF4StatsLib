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
package sg4e.ff4stats.party;

/**
 *
 * @author sg4e
 */
public class Stats {
    
    private final int str, agi, vit, wis, will;

    public Stats(int str, int agi, int vit, int wis, int will) {
        this.str = str;
        this.agi = agi;
        this.vit = vit;
        this.wis = wis;
        this.will = will;
    }

    public int getStrength() {
        return str;
    }

    public int getAgility() {
        return agi;
    }

    public int getVitality() {
        return vit;
    }

    public int getWisdom() {
        return wis;
    }

    public int getWillpower() {
        return will;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.str;
        hash = 97 * hash + this.agi;
        hash = 97 * hash + this.vit;
        hash = 97 * hash + this.wis;
        hash = 97 * hash + this.will;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Stats other = (Stats) obj;
        if (this.str != other.str) {
            return false;
        }
        if (this.agi != other.agi) {
            return false;
        }
        if (this.vit != other.vit) {
            return false;
        }
        if (this.wis != other.wis) {
            return false;
        }
        return this.will == other.will;
    }
    
}
