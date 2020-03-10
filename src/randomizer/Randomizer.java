package randomizer;

import objects.ListElement;
import objects.Lists;
import objects.Moon;
import objects.NecessaryAction;

import java.util.*;
import java.util.regex.Pattern;

public class Randomizer {

    private final boolean toadetteAchievements;
    private final boolean rollingInCoins;
    private final boolean purpleCoins;
    private final boolean jumpRope;
    private final boolean volleyball;
    private final int moonCount;
    private final Random rnd;

    public Randomizer(boolean toadetteAchievements, boolean rollingInCoins, boolean purpleCoins,
                      boolean jumpRope, boolean volleyball, long seed, int moonCount){
        this.toadetteAchievements = toadetteAchievements;
        this.rollingInCoins = rollingInCoins;
        this.purpleCoins = purpleCoins;
        this.jumpRope = jumpRope;
        this.volleyball = volleyball;
        this.moonCount = moonCount;
        rnd = new Random(seed);
    }

    public List<ListElement> randomize() {
        //TODO: FMS option?
        //TODO: Peace option?

        /*
        System.out.println("Options:\n\tToadette: " + toadetteAchievements + "\n\tRolling in Coins: " + rollingInCoins
                + "\n\tPurple Coins: " + purpleCoins + "\n\tJump Rope: " + jumpRope + "\n\tVolleyball: " + volleyball
                + "\nSeed: " + seed + "\n");
        */

        Map<String,List<ListElement>> sourcePoolReturn = setupSourcePool();
        List<ListElement> sourcePool = sourcePoolReturn.get("sourcePool");
        List<ListElement> standby = sourcePoolReturn.get("standby");

        boolean[] moonRockMoonPulled = new boolean[15];
        int[] kingdomFirstVisitMoons = new int[16];
        int[] achievementProgress = new int[Lists.ACHIEVEMENT_TAGS.length + 1];

        int moonsPulled = 0;

        List<ListElement> output = new ArrayList<>();
        List<Moon> remainingAchievements = new ArrayList<>();

        if(toadetteAchievements) {
            remainingAchievements = removeAchievementsFromList(sourcePool);
            moonsPulled += pullAchievementMoons(sourcePool, output, remainingAchievements, standby, moonRockMoonPulled, kingdomFirstVisitMoons, achievementProgress);
        }

        moonsPulled += pullStoryMoons(sourcePool, output, kingdomFirstVisitMoons);

        for(int i=0;i<Lists.kingdomFirstVisitRequiredMoons.length;i++){
            Collections.shuffle(sourcePool, rnd);
            moonsPulled += pullFirstVisitMoons(i, sourcePool, output, remainingAchievements, standby, moonRockMoonPulled, kingdomFirstVisitMoons, achievementProgress, moonsPulled);
        }

        while(moonsPulled < moonCount){
            Collections.shuffle(sourcePool, rnd);
            moonsPulled += pullMoon(sourcePool, output, remainingAchievements, standby, moonRockMoonPulled, kingdomFirstVisitMoons, achievementProgress, moonsPulled);
        }

        if(kingdomFirstVisitMoons[7] == Integer.parseInt(Lists.kingdomFirstVisitRequiredMoons[7][1])){
            for (ListElement m : output) {
                if(m.getName().equals("Secret Path to New Donk City")){
                    output.add(new NecessaryAction("Get Metro Painting Moon", "Sand", true));
                    break;
                }
            }
        }
        if(kingdomFirstVisitMoons[10] == Integer.parseInt(Lists.kingdomFirstVisitRequiredMoons[10][1])){
            for (ListElement m : output) {
                if(m.getName().equals("Secret Path to Mount Volbono!")){
                    output.add(new NecessaryAction("(Lake First) Get Luncheon Painting Moon", "Wooded", true));
                    output.add(new NecessaryAction("(Wooded First) Get Luncheon Painting Moon", "Lake", true));
                    break;
                }
            }
        }

        return output;
}

    private static ArrayList<ListElement> generateCompleteList(){
        ArrayList<ListElement> allMoons = new ArrayList<>();
        for(String[] moon : Lists.moons){
            allMoons.add(new Moon(moon));
        }
        return allMoons;
    }

    private Map<String,List<ListElement>> setupSourcePool(){
        List<ListElement> sourcePool = generateCompleteList();
        List<ListElement> standby = new ArrayList<>();

        // remove all disabled, prerequisite, and postrequisite moons from sourcePool.
        for(int i = 0; i<sourcePool.size(); i++) {
            boolean remove = false;
            boolean toStandby = false;

            String name = sourcePool.get(i).getName();
            if ((!toadetteAchievements && sourcePool.get(i).getKingdom().equals("Achievements")) ||
                    (!rollingInCoins && name.equals("Rolling in Coins")) ||
                    (!purpleCoins && (name.equals("Regional Coin Shopper")) || name.startsWith("Souvenir"))) {
                remove=true;
            }
            if (name.startsWith("Jump-Rope")) {
                if(!jumpRope) {
                    remove=true;
                }
                else {
                    toStandby=true;
                }
            }
            else if (name.startsWith("Beach")) {
                if (!volleyball) {
                    remove=true;
                }
                else {
                    toStandby=true;
                }
            }
            else if(sourcePool.get(i).checkTags("Tourist") ||
                    sourcePool.get(i).checkTags("Yoshi") ||
                    sourcePool.get(i).checkTags("Trace") ||
                    (sourcePool.get(i).checkTags("kfr") && !sourcePool.get(i).getKingdom().equals("Achievements")) ||
                    sourcePool.get(i).getKingdom().equals("Dark Side") ||
                    name.equals("Peach in the Moon Kingdom") ||
                    name.equals("Princess Peach, Home Again!") ||
                    name.matches("Found with Dark Side Art.*") ||
                    name.equals("Atop the Highest Tower") ||
                    name.equals("Moon Shards in the Sand") ||
                    name.equals("Hat-and-Seek: Mushroom Kingdom") ||
                    name.startsWith("RC Car") ||
                    name.matches("Iceburn Circuit.*")){
                toStandby=true;
            }

            if(remove){
                sourcePool.remove(i);
                i--;
            }
            else if(toStandby){
                standby.add(sourcePool.remove(i));
                i--;
            }
        }

        // add (possibly duplicated) prerequisite moons back into sourcePool.
        for(int i = 0; i<standby.size(); i++){
            String name = standby.get(i).getName();
            if(Pattern.matches(".*Regular Cup", name) ||
                    name.equals("Walking on the Moon!") ||
                    name.equals("Walking the Desert!")||
                    name.equals("Walking on Ice!") ||
                    name.equals("Atop the Highest Tower") ||
                    name.equals("RC Car Pro!") ||
                    name.equals("Jump-Rope Hero") ||
                    name.equals("Iceburn Circuit Class A") ||
                    name.equals("Beach Volleyball: Champ")){
                ListElement m = standby.remove(i);
                sourcePool.add(m);
                sourcePool.add(m);
                i--;
            }
            else if(name.equals("A Tourist in the Metro Kingdom!")){
                ListElement m = standby.remove(i);
                for(int j = 0; j<6; j++)
                    sourcePool.add(m);
                i--;
            }
            else if(name.equals("Gobbling Fruit with Yoshi")){
                ListElement m = standby.remove(i);
                for(int j = 0; j < 3; j++)
                    sourcePool.add(m);
                i--;
            }
            else if(standby.get(i).getKingdom().equals("Dark Side")){
                ListElement m = standby.get(i);
                if(m.getName().equals("Arrival at Rabbit Ridge!") || m.getName().equals("Captain Toad on the Dark Side!")) {
                    sourcePool.add(m);
                    standby.remove(i);
                    i--;
                }
            }
        }
        Map<String,List<ListElement>> map = new HashMap<>();
        map.put("sourcePool",sourcePool);
        map.put("standby",standby);
        return map;
    }

    private static ArrayList<ListElement> removeTaggedFromList(String tag, List<ListElement> parentList){
        ArrayList<ListElement> taggedMoons = new ArrayList<>();
        for (int i = 0; i<parentList.size(); i++) {
            if (parentList.get(i).checkTags(tag)){
                taggedMoons.add(parentList.remove(i));
                i--;
            }
        }
        return taggedMoons;
    }

    private static ArrayList<ListElement> generateFirstVisitList(String kingdom, List<ListElement> source){
        ArrayList<ListElement> taggedMoons = new ArrayList<>();
        for (int i = 0; i<source.size(); i++) {
            if (source.get(i).getKingdom().equals(kingdom) && source.get(i).getFirstVisit()){
                taggedMoons.add(source.remove(i));
                i--;
            }
        }
        return taggedMoons;
    }

    private static ArrayList<Moon> removeAchievementsFromList(List<ListElement> sourceList){
        ArrayList<Moon> achievements = new ArrayList<>();

        for(int i = 0; i <sourceList.size(); i++){
            if(sourceList.get(i).getKingdom().equals("Achievements")){
                achievements.add((Moon)sourceList.remove(i));
                i--;
            }
        }
        return achievements;
    }

    private int pullTaggedMoons(Moon achievement, List<ListElement> source, List<ListElement> output, List<Moon> remainingAchievements, List<ListElement> standby, boolean[] moonRockMoonPulled, int[] kingdomFirstVisitMoons, int[] achievementProgress, int moonsPulled){
        int moonsPulledAdditionally = 0;
        int tagValue = Lists.indexOfAchievementTag(achievement.getTags()[0]);
        if(tagValue == -1)
            return 0;

        ArrayList<ListElement> taggedList = removeTaggedFromList(achievement.getTags()[0], source);
        Collections.shuffle(taggedList, rnd);
        int neededProgress = Integer.parseInt(achievement.getTags()[1]);
        while(achievementProgress[tagValue]<neededProgress){
            moonsPulledAdditionally += pullMoon(taggedList, output, remainingAchievements, standby, moonRockMoonPulled, kingdomFirstVisitMoons, achievementProgress, moonsPulled);
        }
        source.addAll(taggedList);
        return moonsPulledAdditionally;
    }

    private int pullFirstVisitMoons(int kingdomIndex, List<ListElement> source, List<ListElement> output, List<Moon> remainingAchievements, List<ListElement> standby, boolean[] moonRockMoonPulled, int[] kingdomFirstVisitMoons, int[] achievementProgress, int moonsPulled){
        int moonsPulledAdditionally = 0;

        ArrayList<ListElement> firstVisitList = generateFirstVisitList(Lists.kingdomFirstVisitRequiredMoons[kingdomIndex][0], source);
        Collections.shuffle(firstVisitList, rnd);
        while(kingdomFirstVisitMoons[kingdomIndex] < Integer.parseInt(Lists.kingdomFirstVisitRequiredMoons[kingdomIndex][1])){
            moonsPulledAdditionally += pullMoon(firstVisitList, output, remainingAchievements, standby, moonRockMoonPulled, kingdomFirstVisitMoons, achievementProgress, moonsPulled);
        }
        source.addAll(firstVisitList);
        return moonsPulledAdditionally;
    }

    private int pullStoryMoons(List<ListElement> source, List<ListElement> output, int[] kingdomFirstVisitMoons){
        int moonsPulled = 0;
        for (int i = 0; i < source.size(); i++) {
            if(source.get(i).checkTags("Story")){
                ListElement m = source.remove(i);
                i--;
                if(m.checkTags("Multi"))
                    moonsPulled += 3;
                else
                    moonsPulled++;
                for (int j = 0; j < Lists.kingdomFirstVisitRequiredMoons.length; j++) {
                    if(Lists.kingdomFirstVisitRequiredMoons[j][0].equals(m.getKingdom())){
                        if (m.checkTags("Multi"))
                            kingdomFirstVisitMoons[j] += 3;
                        else
                            kingdomFirstVisitMoons[j]++;
                        break;
                    }
                }
                output.add(m);
            }
        }
        return moonsPulled;
    }

    // RC Race room does NOT require Remotely Captured Car due to an absurdly easy clip
    /**
     * @param source the current location of the moon getting pulled, which must be in index 0
     * @param output the destination of the moon getting pulled, always output
     *
     * Pulls the front moon off source and places it at the end of output. Increments moonsPulled.
     *
     * If pulled moon was a multimoon, increments moonsPulled twice more.
     *
     * If pulled moon was a Tourist moon, find all copies of that moon in the source and replace with the next Tourist.
     *      if pulled moon was specifically Metro tourist, add a NecessaryAction to Sand.
     *
     * If pulled moon was a Regular Cup kfr moon, find the copy of that moon in the source and replace with the Master.
     *
     * If pulled moon was a low-level Trace Walking moon, find the copy and replace it with the followup.
     *
     * If pulled moon was Atop the Highest Tower, find the copy and replace it with Moon Shards in the Sand.
     *
     * If pulled moon was RC Car Pro, find the copy of that moon in the source and replace it with "RC Car Champ!".
     *
     * If pulled moon was Jump-Rope Hero, find the copy and replace with "Jump-Rope Genius!".
     *
     * If pulled moon was Beach Volleyball: Champ, find the copy and replace with "Beach Volleyball: Hero of the Beach!".
     *
     * If pulled moon was Iceburn Circuit Class A, find the copy and replace with "Iceburn Circuit Class S".
     *
     * If pulled moon was the 13th Peach moon, add "Peach on the Moon" to the source from standby.
     * If pulled moon was Peach on the Moon, add "Princess Peach, Home Again!" and "Hat-and-Seek: Mushroom Kingdom" to
     *      the source from standby.
     *
     * If pulled moon had a "yoshi" tag, find the copies in source and replace with the next "yoshi" moon.
     *
     * If pulled moon was "Arrival at Rabbit Ridge!", add all other Dark Side moons to the source.
     *
     * If pulled moon was a Dark Side, level 1 Yoshi moon, find the copy in source and replace with the level 2 moon.
     *
     *
     * If achievements are enabled and the pulled moon has achievement tags, scan the list of possible tags for a match.
     *      When a match is found (since the moon has tags, at least one match will be found), increment the corresponding
     *      achievement progress. Check if this change needs to dynamically add a new achievement to output. If it does,
     *      find that achievement in remainingAchievements, then remove it from remainingAchievements and add it to output.
     *      Increment moonsPulled (as an achievement was pulled) and break out of the search loop as the only possible match
     *      was found.
     *      Return to the search for tag matches and repeat.
     *
     * NecessaryAction handler for hint art.
     */
    private int pullMoon(List<ListElement> source, List<ListElement> output, List<Moon> remainingAchievements, List<ListElement> standby, boolean[] moonRockMoonPulled, int[] kingdomFirstVisitMoons, int[] achievementProgress, int moonsPulled){
        int moonsPulledAdditionally = 0;
        ListElement m = source.remove(0);
        if(m.checkTags("Multi") && moonsPulled > moonCount - 3)
            return 0; // If pulled moon is a multi-moon and we only need less than 3 moons, cancel and let it pull a new moon
        if(toadetteAchievements) {
            if (willMeetAchievement(m, moonRockMoonPulled, achievementProgress) + moonsPulled + 1 > moonCount) {
                return 0;
            }
            if (!moonRockMoonPulled[14]) {
                moonRockMoonPulled[14] = true;
                for (int i = 0; i < moonRockMoonPulled.length; i++) {
                    if (!moonRockMoonPulled[i]) {
                        moonRockMoonPulled[14] = false;
                        break;
                    }
                }
                if (moonRockMoonPulled[14]) {
                    for (int i = 0; i < remainingAchievements.size(); i++) {
                        if (remainingAchievements.get(i).getName().equals("Moon Rock Liberator")) {
                            output.add(remainingAchievements.remove(i));
                        }
                    }
                }
            }
        }
        output.add(m);
        for (int i = 0; i < Lists.kingdomFirstVisitRequiredMoons.length; i++) {
            if(Lists.kingdomFirstVisitRequiredMoons[i][0].equals(m.getKingdom())){
                kingdomFirstVisitMoons[i]++;
                break;
            }
        }
        moonsPulledAdditionally++;
        if(m.checkTags("Multi"))
            moonsPulledAdditionally+=2;
        if(moonCount <= 0)
            return moonsPulledAdditionally;

        //<editor-fold desc="replacing action">
        if(m.checkTags("Tourist")){
            for (int i = 0; i < standby.size(); i++) {
                if(standby.get(i).checkTags("Tourist") && Integer.parseInt(m.getTags()[1]) == Integer.parseInt(standby.get(i).getTags()[1])-1){
                    ListElement nextTourist = standby.remove(i);
                    for (int j = 0; j < source.size(); j++) {
                        if(source.get(j) == m){
                            source.set(j, nextTourist);
                        }
                    }
                    break;
                }
            }
            if(m.getName().matches(".*Metro.*")){
                ListElement sandTourist = new NecessaryAction("Talk to Tourist", "Sand", true);
                output.add(sandTourist);
            }
        }
        else if(m.getName().matches(".*Regular Cup")){
            String kingdom = m.getName().split(" ")[0];
            for (int i = 0; i < standby.size(); i++) {
                if(standby.get(i).getName().matches(kingdom+" Kingdom.*Master Cup")){
                    for (int j = 0; j < source.size(); j++) {
                        if(source.get(j) == m){
                            source.set(j, standby.remove(i));
                        }
                    }
                    break;
                }
            }
        }
        else if(m.getName().equals("Walking the Desert!")){
            for (int i = 0; i < standby.size(); i++) {
                if(standby.get(i).getName().equals("More Walking in the Desert!")){
                    for (int j = 0; j < source.size(); j++) {
                        if(source.get(j) == m){
                            source.set(j, standby.remove(i));
                        }
                    }
                    break;
                }
            }
        }
        else if(m.getName().equals("Walking on Ice!")){
            for (int i = 0; i < standby.size(); i++) {
                if(standby.get(i).getName().equals("Even More Walking on Ice!")){
                    for (int j = 0; j < source.size(); j++) {
                        if(source.get(j) == m){
                            source.set(j, standby.remove(i));
                        }
                    }
                    break;
                }
            }
        }
        else if(m.getName().equals("Walking on the Moon!")){
            for (int i = 0; i < standby.size(); i++) {
                if(standby.get(i).getName().equals("Walking on the Moon: Again!")){
                    for (int j = 0; j < source.size(); j++) {
                        if(source.get(j) == m){
                            source.set(j, standby.remove(i));
                        }
                    }
                    break;
                }
            }
        }
        else if(m.getName().equals("Atop the Highest Tower")){
            for (int i = 0; i < standby.size(); i++) {
                if(standby.get(i).getName().equals("Moon Shards in the Sand")){
                    for (int j = 0; j < source.size(); j++) {
                        if(source.get(j) == m){
                            source.set(j, standby.remove(i));
                        }
                    }
                    break;
                }
            }
        }
        else if(m.getName().equals("RC Car Pro!")){
            for (int i = 0; i < standby.size(); i++) {
                if(standby.get(i).getName().equals("RC Car Champ!")){
                    for (int j = 0; j < source.size(); j++) {
                        if(source.get(j) == m){
                            source.set(j, standby.remove(i));
                        }
                    }
                    break;
                }
            }
        }
        else if(m.getName().equals("Jump-Rope Hero")) {
            for (int i = 0; i < standby.size(); i++) {
                if (standby.get(i).getName().equals("Jump-Rope Genius")) {
                    for (int j = 0; j < source.size(); j++) {
                        if (source.get(j) == m) {
                            source.set(j, standby.remove(i));
                        }
                    }
                    break;
                }
            }
        }else if(m.getName().equals("Iceburn Circuit Class A")){
            for (int i = 0; i < standby.size(); i++) {
                if(standby.get(i).getName().equals("Iceburn Circuit Class S")){
                    for (int j = 0; j < source.size(); j++) {
                        if(source.get(j) == m){
                            source.set(j, standby.remove(i));
                        }
                    }
                    break;
                }
            }
        }else if(m.getName().equals("Beach Volleyball: Champ")){
            for (int i = 0; i < standby.size(); i++) {
                if(standby.get(i).getName().equals("Beach Volleyball: Hero of the Beach!")){
                    for (int j = 0; j < source.size(); j++) {
                        if(source.get(j) == m){
                            source.set(j, standby.remove(i));
                        }
                    }
                    break;
                }
            }
        }
        else if(m.checkTags("Peach") && achievementProgress[14] == 12){
            for (int i = 0; i < standby.size(); i++) {
                if(standby.get(i).getName().equals("Peach in the Moon Kingdom")){
                    for (int j = 0; j < source.size(); j++) {
                        if(source.get(j) == m){
                            source.set(j, standby.remove(i));
                        }
                    }
                    break;
                }
            }
        }
        else if(m.getName().equals("Peach in the Moon Kingdom")){
            for (int i = 0; i < standby.size(); i++) {
                String name = standby.get(i).getName();
                if(name.equals("Princess Peach, Home Again!") || name.equals("Hat-and-Seek: Mushroom Kingdom")){
                    source.add(standby.remove(i));
                    Collections.shuffle(source, rnd);
                }
            }
        }
        else if(m.checkTags("Yoshi")){
            for (int i = 0; i < standby.size(); i++) {
                if(standby.get(i).checkTags("Yoshi")&& Integer.parseInt(m.getTags()[1]) == Integer.parseInt(standby.get(i).getTags()[1])-1){
                    ListElement nextYoshi = standby.remove(i);
                    for (int j = 0; j < source.size(); j++) {
                        if(source.get(j) == m){
                            source.set(j, nextYoshi);
                        }
                    }
                    break;
                }
            }
        }
        else if(m.getKingdom().equals("Dark Side")){
            if(m.getName().equals("Arrival at Rabbit Ridge!")){
                for (int i = 0; i < standby.size(); i++) {
                    if (standby.get(i).getKingdom().equals("Dark Side") || standby.get(i).getName().matches("Found with Dark Side Art.*")) {
                        switch (standby.get(i).getName().substring(0, 5)) {
                            case "Yoshi":
                                source.add(standby.get(i));
                                source.add(standby.remove(i));
                                i--;
                                break;
                            case "Fruit":
                                break;
                            default:
                                source.add(standby.remove(i));
                                i--;
                        }
                    }
                }
                Collections.shuffle(source, rnd);
            }
            else if(m.getName().startsWith("Yoshi")) {
                for (int i = 0; i < standby.size(); i++) {
                    switch(m.getName()){
                        case "Yoshi Under Siege":
                            if(standby.get(i).getName().equals("Fruit Feast Under Siege")){
                                for (int j = 0; j < source.size(); j++) {
                                    if(source.get(j) == m) {
                                        source.set(j, standby.remove(i));
                                        i--;
                                        break;
                                    }
                                }
                            }
                            break;
                        case "Yoshi on the Sinking Island":
                            if(standby.get(i).getName().equals("Fruit Feast on the Sinking Island")){
                                for (int j = 0; j < source.size(); j++) {
                                    if(source.get(j) == m) {
                                        source.set(j, standby.remove(i));
                                        i--;
                                        break;
                                    }
                                }
                            }
                            break;
                        case "Yoshi's Magma Swamp":
                            if(standby.get(i).getName().equals("Fruit Feast in the Magma Swamp!")){
                                for (int j = 0; j < source.size(); j++) {
                                    if(source.get(j) == m) {
                                        source.set(j, standby.remove(i));
                                        i--;
                                        break;
                                    }
                                }
                            }
                            break;
                        default: break;
                    }
                }
            }
        }
        //</editor-fold>

        if((toadetteAchievements || m.checkTags("Peach")) && m.getTags().length > 0){
            for(String tag : m.getTags()){
                int indexOfTag = Lists.indexOfAchievementTag(tag);

                if(indexOfTag == -1) continue; //Tag not found, just continue with the next tag

                achievementProgress[indexOfTag]++;
                //Lists.ACHIEVEMENT_LEVELS[i] contains the levels for tag[i]

                for(int i=0;i<remainingAchievements.size();i++) {
                    if (remainingAchievements.get(i).checkTags(tag)) {
                        for(int neededForLevel : Lists.ACHIEVEMENT_LEVELS[indexOfTag]) {
                            if (achievementProgress[indexOfTag] == neededForLevel) {
                                output.add(remainingAchievements.remove(i));
                                moonsPulledAdditionally++;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if(m.getName().matches("Found with.*Art.*")){
            String kingdom = m.getName().split(" ")[2];
            ListElement checkArt = new NecessaryAction("@Kops Because I'm Bugged", "Cap", true);
            switch(kingdom){
                case "Cap":
                case "Sand":
                case "Bowser's":
                case "Moon":
                    checkArt = new NecessaryAction("Check Hint Art", kingdom, false);
                    break;
                case "Dark":
                    switch(m.getName().split(" ")[5]){
                        case "1":
                            checkArt = new NecessaryAction("Check Cascade Art", "Dark Side", true);
                            break;
                        case "2":
                            checkArt = new NecessaryAction("Check Metro Art", "Dark Side", true);
                            break;
                        case "3":
                            checkArt = new NecessaryAction("Check Mushroom Art", "Dark Side", true);
                            break;
                        case "4":
                            checkArt = new NecessaryAction("Check Cloud Art", "Dark Side", true);
                            break;
                        case "5":
                            checkArt = new NecessaryAction("Check Snow Art", "Dark Side", true);
                            break;
                        case "6":
                            checkArt = new NecessaryAction("Check Seaside Art", "Dark Side", true);
                            break;
                        case "7":
                            checkArt = new NecessaryAction("Check Lost Art", "Dark Side", true);
                            break;
                        case "8":
                            checkArt = new NecessaryAction("Check Luncheon Art", "Dark Side", true);
                            break;
                        case "9":
                            checkArt = new NecessaryAction("Check Lake Art", "Dark Side", true);
                            break;
                        case "10":
                            checkArt = new NecessaryAction("Check Ruined Art", "Dark Side", true);
                            break;
                        default: break;
                    }
                    break;
                default:
                    checkArt = new NecessaryAction("Check Hint Art", kingdom, true);
            }
            output.add(checkArt);
        }
        return moonsPulledAdditionally;
    }

    private int pullAchievements(ArrayList<ListElement> returns, List<ListElement> sourcePool, List<ListElement> output, List<Moon> remainingAchievements, List<ListElement> standby, boolean[] moonRockMoonPulled, int[] kingdomFirstVisitMoons, int[] achievementProgress, int moonsPulled){
        int moonsPulledAdditionally = 0;
        ArrayList<Moon> achievementSource = new ArrayList<>();
        for(int i = 0; i<remainingAchievements.size(); i++) {
            String name = remainingAchievements.get(i).getName();
            if (name.equals("Rescue Princess Peach") || name.equals("Achieve World Peace") ||
                    name.equals("Power Moon Knight") || name.equals("Power Moon Wizard") ||
                    name.equals("Checkpoint Flagger") || name.equals("World Warper") ||
                    name.equals("Loaded with Coins") || name.equals("Capturing Novice") ||
                    //TODO: count captures dynamically (probably force apprentice but not master)
                    name.equals("Capturing Apprentice") || name.equals("Capturing Master")) {
                returns.add(remainingAchievements.remove(i));
                moonsPulledAdditionally++;
                i--;
            }
        }
        for(int i = 0; i<remainingAchievements.size(); i++){
            if(remainingAchievements.get(i).getLevel() == 1){
                achievementSource.add(remainingAchievements.remove(i));
                i--;
            }
        }
        Collections.shuffle(achievementSource, rnd);
        for(int i = 0; i<15; i++){
            Moon moon = achievementSource.remove(0);
            returns.add(moon);
            if(moon.getName().equals("Art Investigator")){
                for (int j = 0; j < sourcePool.size(); j++) {
                    if(sourcePool.get(j).getName().equals("Arrival at Rabbit Ridge!")){
                        sourcePool.add(0, sourcePool.remove(j));
                        moonsPulledAdditionally += pullMoon(sourcePool, output, remainingAchievements, standby, moonRockMoonPulled, kingdomFirstVisitMoons, achievementProgress, moonsPulled);
                    }
                }
            }
            moonsPulledAdditionally++;
            String tag = moon.getTags()[0];
            int level = moon.getLevel();
            for(int j = 0; j<remainingAchievements.size(); j++){
                Moon test = remainingAchievements.get(j);
                if(test.getLevel() == level+1 && test.getTags()[0].equals(tag)){
                    achievementSource.add(remainingAchievements.remove(j));
                    Collections.shuffle(achievementSource,rnd);
                    break;
                }
            }
        }
        remainingAchievements.addAll(achievementSource);
        return moonsPulledAdditionally;
    }

    private int pullAchievementMoons(List<ListElement> sourcePool, List<ListElement> output, List<Moon> remainingAchievements, List<ListElement> standby, boolean[] moonRockMoonPulled, int[] kingdomFirstVisitMoons, int[] achievementProgress){
        int moonsPulled = 0;
        ArrayList<ListElement> forcedAchievements = new ArrayList<>();
        moonsPulled += pullAchievements(forcedAchievements, sourcePool, output, remainingAchievements, standby, moonRockMoonPulled, kingdomFirstVisitMoons, achievementProgress, moonsPulled);
        for (ListElement achievement : forcedAchievements) {
            moonsPulled += pullTaggedMoons((Moon)achievement, sourcePool, output, remainingAchievements, standby, moonRockMoonPulled, kingdomFirstVisitMoons, achievementProgress, moonsPulled);
        }
        output.addAll(forcedAchievements);
        return moonsPulled;
    }

    private int willMeetAchievement(ListElement toTest, boolean[] moonRockMoonPulled, int[] achievementProgress){
        int achievementsMet = 0;
        for (int i = 0; i < Lists.ACHIEVEMENT_TAGS.length; i++) {
            if (toTest.checkTags(Lists.ACHIEVEMENT_TAGS[i])) {
                for (int j: Lists.ACHIEVEMENT_LEVELS[i]) {
                    if (achievementProgress[i] == j - 1){
                        achievementsMet++;
                        break;
                    }
                }
            }
        }
        if(!moonRockMoonPulled[14] && !toTest.getFirstVisit()){
            boolean moonRock = true;
            for (String moon : Lists.PG_NOT_MOON_ROCK) {
                if (toTest.getName().equals(moon)){
                    moonRock = false;
                    break;
                }
            }
            if(moonRock){
                moonRockMoonPulled[toTest.getKingdomNo()] = true;
            }
            boolean addMRLib = true;
            for (boolean b : moonRockMoonPulled) {
                if(!b){
                    addMRLib = false;
                    break;
                }
            }
            if(addMRLib){
                moonRockMoonPulled[14] = true;
                achievementsMet++;
            }
        }
        return achievementsMet;
    }
}
