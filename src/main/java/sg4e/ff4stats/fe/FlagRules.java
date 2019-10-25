/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg4e.ff4stats.fe;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author CaitSith2
 */
public class FlagRules {
    
    private final HashMap<Flag, List<String>> ruleReference = new HashMap<>();
    private final HashMap<String, Rule> rules = new HashMap<>();
    
    private static enum OPERATOR {
        AND, OR, NOT, DEFAULT
    }
    
    private static class Conditions {
        public OPERATOR operator = OPERATOR.DEFAULT;
        public List<Object> conditions = new ArrayList<>();
        
        public boolean processCondition(Object condition, FlagSet flagset) {
            if(condition instanceof Conditions) {
                return ((Conditions)condition).check(flagset);
            }
            else if (condition instanceof String) {
                return flagset.contains((String)condition);
            }
            else {
                throw new IllegalArgumentException("condition not of known type");
            }
        }
        
        public boolean check(FlagSet flagset) {
            switch (operator) {
                case AND:
                    for(int i = 0; i < conditions.size(); i++) {
                        if (!processCondition(conditions.get(i), flagset))
                            return false;
                    }
                    return true;
                case OR:
                    for(int i = 0; i < conditions.size(); i++) {
                        if(processCondition(conditions.get(i), flagset))
                            return true;
                    }
                    return false;
                case NOT:
                    return !processCondition(conditions.get(0), flagset);
                default:
                    return processCondition(conditions.get(0), flagset);
            }
        }
        
        public boolean processCondition(Object condition, List<Flag> flags) {
            if(condition instanceof Conditions) {
                return ((Conditions)condition).check(flags);
            }
            else if (condition instanceof String) {
                String flag = (String)condition;
                for(int i = 0; i < flags.size(); i++)
                    if(flags.get(i).getName().equals(flag))
                        return true;
                return false;
            }
            else {
                throw new IllegalArgumentException("condition not of known type");
            }
        }
        
        public boolean check(List<Flag> flags) {
            switch (operator) {
                case AND:
                    for(int i = 0; i < conditions.size(); i++) {
                        if (!processCondition(conditions.get(i), flags))
                            return false;
                    }
                    return true;
                case OR:
                    for(int i = 0; i < conditions.size(); i++) {
                        if(processCondition(conditions.get(i), flags))
                            return true;
                    }
                    return false;
                case NOT:
                    return !processCondition(conditions.get(0), flags);
                default:
                    return processCondition(conditions.get(0), flags);
            }
        }
        
        public void parseJson(JsonParser parser) throws IOException {
            parser.nextToken();
            switch(parser.getValueAsString()) {
                case "and":
                    operator = OPERATOR.AND;
                    break;
                case "or":
                    operator = OPERATOR.OR;
                    break;
                case "not":
                    operator = OPERATOR.NOT;
                    break;
            }
            while(!JsonToken.END_ARRAY.equals(parser.nextToken())) {
                if(JsonToken.VALUE_STRING.equals(parser.getCurrentToken())) {
                    conditions.add(parser.getValueAsString());
                }
                else {
                    Conditions c = new Conditions();
                    c.parseJson(parser);
                    conditions.add(c);
                }
            }
        }
        
    }
    
    private static class Consequences {
        public boolean enable = true;
        public List<Flag> consequences = new ArrayList<>();
        
        public void applyConsequences(FlagSet flagset) {
            for(int i = 0; i < consequences.size(); i++) {
                if(enable)
                    flagset.rawAdd(consequences.get(i));
                else
                    flagset.remove(consequences.get(i));
            }
        }
        public void applyConsequences(List<Flag> flags) {
            for(int i = 0; i < consequences.size(); i++) {
                if(enable)
                    flags.add(consequences.get(i));
                else
                    flags.remove(consequences.get(i));
            }
        }
        
    }
    
    private static class Rule {
        public Conditions conditions = new Conditions();
        public List<Consequences> consequences = new ArrayList<>();
        
        public void applyRule(FlagSet flagset) {
            if(!conditions.check(flagset)) return;
            consequences.forEach(consequence -> consequence.applyConsequences(flagset));
        }
        
        public void applyRule(List<Flag> flags) {
            if(!conditions.check(flags)) return;
            consequences.forEach(consequence -> consequence.applyConsequences(flags));
        }
        
        public void parseJson(FlagVersion version, JsonParser parser) throws IOException {
            while (!"condition".equals(parser.getCurrentName()))
                parser.nextToken();
            if(JsonToken.VALUE_STRING.equals(parser.nextToken())) {
                conditions.conditions.add(parser.getValueAsString());
            }
            else {
                conditions.parseJson(parser);
            }
            
            while (!JsonToken.START_OBJECT.equals(parser.nextToken()) || !"consequences".equals(parser.getCurrentName())) {}
            
            
            while (!JsonToken.END_OBJECT.equals(parser.nextToken())) {
                Consequences c = new Consequences();
                consequences.add(c);
                c.enable = "enable".equals(parser.getCurrentName());
                parser.nextToken(); 
                while(!JsonToken.END_ARRAY.equals(parser.nextToken()))
                {
                    c.consequences.add(version.getFlagByName(parser.getValueAsString()));
                }
            }
            parser.nextToken();
        }
    }
    
    protected List<Flag> getBaseFlags() {
        List<Flag> flags = new ArrayList<>();
        rules.forEach((str, rule) -> {
            rule.applyRule(flags);
        });
        return flags;
    }
    
    protected void applyRules(FlagSet flagset, Flag flag) {
        if(flag == null)
        {
            rules.forEach((str, rule) -> {rule.applyRule(flagset);});
        }
        else {
            if(ruleReference.get(flag) == null) return;
            ruleReference.get(flag).forEach(rule -> {
                rules.get(rule).applyRule(flagset);});
        }
    }
    
    protected void parseJson(FlagVersion version, JsonParser parser) throws IOException {
        while (!JsonToken.START_OBJECT.equals(parser.nextToken()) || !"rules".equals(parser.getCurrentName())) {
            //if(parser.currentToken() == null) throw new IOException("Unexpected end of stream while looking for rules");
        }
        
        while(!JsonToken.END_OBJECT.equals(parser.nextToken())) {
            String name = parser.getCurrentName();
            Rule rule = new Rule();
            rule.parseJson(version, parser);
            rules.put(name, rule);
        }
        
        while (!JsonToken.START_OBJECT.equals(parser.nextToken()) || !"rule_references".equals(parser.getCurrentName())) {
            //if(parser.currentToken() == null) throw new IOException("Unexpected end of stream while looking for rules");
        }
        
        while(!JsonToken.END_OBJECT.equals(parser.nextToken())) {
            String name = parser.getCurrentName();
            List<String> refs = new ArrayList<>();
            parser.nextToken();
            while(!JsonToken.END_ARRAY.equals(parser.nextToken())) {
                refs.add(parser.getValueAsString());
            }
            Flag flag = version.getFlagByName(name);
            ruleReference.put(flag, refs);
        }
    }
}
