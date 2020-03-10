package objects;

import java.util.Arrays;
import java.util.Objects;

public class Moon extends ListElement {
    private final String name;
    private final String kingdom;
    private final String[] tags;
    private int level;
    private boolean firstVisit;
    private boolean crossedOff = false;

    public Moon(String[] moon){
        super(moon[0],moon[1], moon[1].equals("Achievements") || moon[2].equals("true"));
        name = moon[0];
        kingdom = moon[1];
        if(kingdom.equals("Achievements")) {
            level = Integer.parseInt(moon[2]);
            tags = new String[]{moon[3], moon[4]};
        }
        else {
            firstVisit = moon[2].equals("true");
            tags = moon.length>3 ? Arrays.copyOfRange(moon, 3, moon.length) : new String[]{};
        }
    }

    public Moon(String moonName, String kingdom, boolean first, String... tagArray) {
        super(moonName, kingdom, first);
        name = moonName;
        this.kingdom = kingdom;
        tags = tagArray;
        firstVisit = first;
    }

    public Moon(String achievementName, String kingdom, int level, String... tagArray) {
        super(achievementName, kingdom, true);
        name = achievementName;
        this.kingdom = kingdom;
        tags = tagArray;
        this.level = level;
    }

    public String toString() {
        /* A useful debugging toString alternative.
        if(!king.equals("Achievements"))
            return "{\n\t\"name\": \""+name+"\",\n\t\"kingdom\": \""+king+"\",\n\t\"first\": "+ getFirstVisit().toString()+"\n}";
        return "{\n\t\"name\": \""+name+"\",\n\t\"kingdom\": \""+king+"\",\n\t\"tag\": \""+achTags[0]+"\",\n\t\"count\": \""+achTags[1]+"\"\n}";
        */
        String out = name;

        if (kingdom.equals("Achievements")) {
            out += ": " + tags[1];
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
        return kingdom;
    }

    public boolean getFirstVisit() {
        return firstVisit;
    }

    public int getLevel() {
        return level;
    }

    public void toggleCrossedOff() {
        crossedOff = !crossedOff;
    }

    public boolean getCrossedOff() {
        return crossedOff;
    }

    @Override
    public String[] getTags(){
        return tags;
    }

    @Override
    public boolean checkTags(String target) {
        boolean tagged = false;
        for (String s: tags){
            if (s.equals(target)) {
                tagged = true;
                break;
            }
        }
        return tagged;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
