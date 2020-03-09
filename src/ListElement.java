import java.util.Objects;

abstract class ListElement {
    private String name;
    private String kingdom;
    private boolean firstVisit;

    ListElement(String name, String kingdom, boolean firstVisit) {
        this.name = name;
        this.kingdom = kingdom;
        this.firstVisit = firstVisit;
    }

    private boolean crossedOff;

    String getName() {
        return name;
    }

    String getKingdom() {
        return kingdom;
    }

    boolean getFirstVisit() {
        return firstVisit;
    }

    String[] getTags() {
        return new String[]{};
    }

    boolean checkTags(String tag) {
        return false;
    }

    void toggleCrossedOff() {
        crossedOff = !crossedOff;
    }

    boolean getCrossedOff() {
        return crossedOff;
    }

    static int compareByVisit(ListElement m1, ListElement m2) {
        int visitM1 = getVisit(m1);
        int visitM2 = getVisit(m2);
        if (visitM1 < visitM2)
            return -1;
        if (visitM1 > visitM2)
            return 1;
        return Lists.indexOfElement(m1) < Lists.indexOfElement(m2) ? -1 : 1;
    }

    private static int getVisit(ListElement m){
        String kingdom = m.getKingdom();
        boolean firstVisit = m.getFirstVisit();
        if(firstVisit){
            switch (kingdom){
                case "Cascade":
                    return 0;
                case "Sand":
                    return 1;
                case "Lake":
                    return 2;
                case "Wooded":
                    return 3;
                case "Cloud":
                    return 4;
                case "Lost":
                    return 5;
                case "Metro":
                    return 6;
                case "Snow":
                    return 7;
                case "Seaside":
                    return 8;
                case "Luncheon":
                    return 9;
                case "Ruined":
                    return 10;
                case "Bowser's":
                    return 11;
                case "Moon":
                    return 12;
            }
        }
        else { // Not first visit
            switch (kingdom){
                case "Snow":
                    return 14;
                case "Cascade":
                    return 15;
                case "Bowser's":
                    return 16;
                case "Seaside":
                    return 17;
                case "Lake":
                    return 18;
                case "Sand":
                    return 19;
                case "Metro":
                    return 20;
                case "Wooded":
                    return 21;
                case "Luncheon":
                    return 22;
                case "Cap":
                    return 23;
                case "Cloud":
                    return 24;
                case "Lost":
                    return 25;
                case "Ruined":
                    return 26;
                case "Moon":
                    return 27;
            }
        }

        switch (kingdom) {
            case "Mushroom":
                return 13;
            case "Dark Side":
                return 28;
            case "Achievements":
                return 29;

            default:
                return -1;
        }
    }

    static int compareByKingdom(ListElement m1, ListElement m2) {
        int kingdom1 = getKingdom(m1);
        int kingdom2 = getKingdom(m2);
        if (kingdom1 < kingdom2)
            return -1;
        if (kingdom1 > kingdom2)
            return 1;
        return Lists.indexOfElement(m1) < Lists.indexOfElement(m2) ? -1 : 1;
    }

    static int getKingdom(ListElement m){
        switch (m.getKingdom()) {
            case "Cap":
                return 0;
            case "Cascade":
                return 1;
            case "Sand":
                return 2;
            case "Lake":
                return 3;
            case "Wooded":
                return 4;
            case "Cloud":
                return 5;
            case "Lost":
                return 6;
            case "Metro":
                return 7;
            case "Snow":
                return 8;
            case "Seaside":
                return 9;
            case "Luncheon":
                return 10;
            case "Ruined":
                return 11;
            case "Bowser's":
                return 12;
            case "Moon":
                return 13;
            case "Mushroom":
                return 14;
            case "Dark Side":
                return 15;
            case "Achievements":
                return 16;
        }

        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListElement e = (ListElement) o;
        return Objects.equals(name, e.name);
    }
}
