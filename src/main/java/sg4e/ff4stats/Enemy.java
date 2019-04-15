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

import java.util.ArrayList;
import java.util.List;
import sg4e.ff4stats.csv.RecordParser;

/**
 *
 * @author sg4e
 */
public class Enemy {
    private final int level, hp, exp, gp, attackMultipler, hitPercent, attack, 
            defenseMultipler, evasionPercent, defense, magicDefenseMultiplier, 
            magicEvasionPercentage, magicDefense, minSpeed, maxSpeed, spellPower;
    private final String name;
    private final List<String> scriptValues;

    public Enemy(String name, int level, int hp, int exp, int gp, int attackMultipler, int hitPercent, 
            int attack, int defenseMultipler, int evasionPercent, int defense, 
            int magicDefenseMultiplier, int magicEvasionPercentage, int magicDefense, 
            int minSpeed, int maxSpeed, int spellPower, List<String> scriptValues) {
        this.name = name;
        this.level = level;
        this.hp = hp;
        this.exp = exp;
        this.gp = gp;
        this.attackMultipler = attackMultipler;
        this.hitPercent = hitPercent;
        this.attack = attack;
        this.defenseMultipler = defenseMultipler;
        this.evasionPercent = evasionPercent;
        this.defense = defense;
        this.magicDefenseMultiplier = magicDefenseMultiplier;
        this.magicEvasionPercentage = magicEvasionPercentage;
        this.magicDefense = magicDefense;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.spellPower = spellPower;
        this.scriptValues = scriptValues;
    }
    
    public static Enemy fromRecord(RecordParser record) {
        List<String> scriptValues = new ArrayList<>();
        for(int index = 19, length = record.size(); index < length; index++) {
            scriptValues.add(record.getString(index));
        }
        return new Enemy(record.getString(2), record.getInteger(3), record.getInteger(4), record.getInteger(5), record.getInteger(6), record.getInteger(7), 
                record.getInteger(8), record.getInteger(9), record.getInteger(10), record.getInteger(11), record.getInteger(12), record.getInteger(13), 
                record.getInteger(14), record.getInteger(15), record.getInteger(16), record.getInteger(17),
                record.getInteger(18), scriptValues);
    }

    @Override
    public String toString() {
        return "Enemy{" + "level=" + level + ", hp=" + hp + ", exp=" + exp + 
                ", gp=" + gp + ", attackMultipler=" + attackMultipler + 
                ", hitPercent=" + hitPercent + ", attack=" + attack + 
                ", defenseMultipler=" + defenseMultipler + ", evasionPercent=" + 
                evasionPercent + ", defense=" + defense + ", magicDefenseMultiplier=" 
                + magicDefenseMultiplier + ", magicEvasionPercentage=" + magicEvasionPercentage + 
                ", magicDefense=" + magicDefense + ", minSpeed=" + minSpeed + 
                ", maxSpeed=" + maxSpeed + ", spellPower=" + spellPower + 
                ", script=" + scriptValues + '}';
    }
    

    public int getLevel() {
        return level;
    }

    public int getHp() {
        return hp;
    }

    public int getExp() {
        return exp;
    }

    public int getGp() {
        return gp;
    }

    public int getAttackMultipler() {
        return attackMultipler;
    }

    public int getHitPercent() {
        return hitPercent;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefenseMultipler() {
        return defenseMultipler;
    }

    public int getEvasionPercent() {
        return evasionPercent;
    }

    public int getDefense() {
        return defense;
    }

    public int getMagicDefenseMultiplier() {
        return magicDefenseMultiplier;
    }

    public int getMagicEvasionPercentage() {
        return magicEvasionPercentage;
    }

    public int getMagicDefense() {
        return magicDefense;
    }

    public int getMinSpeed() {
        return minSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getSpellPower() {
        return spellPower;
    }

    public String getName() {
        return name;
    }

    public List<String> getScriptValues() {
        return scriptValues;
    }
    
}
