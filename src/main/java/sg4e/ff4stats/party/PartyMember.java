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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;

/**
 *
 * @author sg4e
 */
public class PartyMember {
    
    private int level, xp;
    private Stats currentStats, currentStatsMax;
    private final LevelData data;
    
    public PartyMember(LevelData data) {
        this(data, data.getStartingLevel());
    }
    
    public PartyMember(LevelData data, int startingLevel) {
        if(startingLevel > 99 || startingLevel < data.getStartingLevel())
            throw new IllegalArgumentException("Not a valid level for this character");
        this.data = data;
        this.level = startingLevel;
        this.xp = 0;
        currentStats = data.getMinStatsForLevel(level);
        currentStatsMax = data.getMaxStatsForLevel(level);
    }
    
    public void gainXp(int xpGained) {
        int oldXp = xp;
        if(xpGained != 0) {
            xp += xpGained;
            int newLevel = data.getLevelForTotalExperience(xp);
            int oldLevel = level;
            level = newLevel;
            Stats oldStats = currentStats;
            currentStats = data.getMinStatsForLevel(level);
            currentStatsMax = data.getMaxStatsForLevel(level);
            pcs.firePropertyChange("xp", oldXp, xp);
            if(oldLevel != newLevel) {
                pcs.firePropertyChange("level", oldLevel, newLevel);
            }
            if(!oldStats.equals(currentStats)) {
                pcs.firePropertyChange("stats", oldStats, currentStats);
            }
        }
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public Stats getStats() {
        return currentStats;
    }
    
    public Stats getStatsMax() {
        return currentStatsMax;
    }

    public LevelData getData() {
        return data;
    }
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
    
    public boolean hasPropertyChangeListener(PropertyChangeListener listener) {
        return Arrays.stream(this.pcs.getPropertyChangeListeners()).anyMatch(l -> l.equals(listener));
    }

}
