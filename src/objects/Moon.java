package objects;

import java.util.Objects;

public class Moon extends ListElement {
    private String name;
    private String king;
    private String[] achTags;
    private int achLevel;
    private boolean firstVisit;
    private boolean crossedOff = false;

    public Moon(String moonName, String kingdom, boolean first, String... tagArray) {
        super(moonName, kingdom, first);
        name = moonName;
        king = kingdom;
        achTags = tagArray;
        firstVisit = first;
    }

    public Moon(String achievementName, String kingdom, int level, String... tagArray) {
        super(achievementName, kingdom, true);
        name = achievementName;
        king = kingdom;
        achTags = tagArray;
        achLevel = level;
    }

    public String toString() {
        /* A useful debugging toString alternative.
        if(!king.equals("Achievements"))
            return "{\n\t\"name\": \""+name+"\",\n\t\"kingdom\": \""+king+"\",\n\t\"first\": "+ getFirstVisit().toString()+"\n}";
        return "{\n\t\"name\": \""+name+"\",\n\t\"kingdom\": \""+king+"\",\n\t\"tag\": \""+achTags[0]+"\",\n\t\"count\": \""+achTags[1]+"\"\n}";
        */
        String out = name;

        if (king.equals("Achievements")) {
            out += ": " + achTags[1];
        }

        if (crossedOff) {
            return "<html><strike>" + out + "</strike></html>";
        }
        return out;
    }

    public String getName() {
        return name;
    }

    public String getKingdom() {
        return king;
    }

    public boolean getFirstVisit() {
        return firstVisit;
    }

    public int getLevel() {
        return achLevel;
    }

    public void toggleCrossedOff() {
        crossedOff = !crossedOff;
    }

    public boolean getCrossedOff() {
        return crossedOff;
    }

    @Override
    public String[] getTags(){
        return achTags;
    }

    @Override
    public boolean checkTags(String target) {
        boolean tagged = false;
        for (String s: achTags){
            if(s.equals(target))
                tagged = true;
        }
        return tagged;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
