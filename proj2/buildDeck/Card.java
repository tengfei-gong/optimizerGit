package buildDeck;

import java.util.Random;

public class Card implements Comparable<Card>{
    public enum Tier {CORE, RARE, ELITE, LEGEND, RM};
    public enum Position {QB, RB, WR, WRTE, FLEX};
    private String name;

    private String team;
    private Tier tier;
    private Position pos;
    int serial;
    private double FP;

    private boolean superstar;

    private String origPos;


    public Card() {
        name = null;
        tier = Tier.CORE;
        pos = Position.QB;
        serial = 0;
        FP = 0;
    }

    public Card(String nameInput, int sn, Tier tierInput, Position Pos, double FantasyPoint, String originalPosition,
                String teamInput) {
        name = nameInput;
        FP = FantasyPoint;
        serial = sn;
        tier = tierInput;
        pos = Pos;
        origPos = originalPosition;
        superstar = false;
        team = teamInput;
    }

    public Card copyCard() {
        Card card = new Card(name, serial, tier, pos, FP, origPos, team);
        return card;
    }

    public void makeSuperstar() {
        superstar = true;
    }


    private void setFP(double FantasyPoint) {
        FP = FantasyPoint;
    }


    public void randomFP(Random random) {

        double rdFP = random.nextDouble(-2, 30);
        setFP(rdFP);
    }

    public String getName() {
        return name;
    }

    public int getSerial() {
        return serial;
    }

    public Position getPos() {
        return pos;
    }
    public String getOrigPos() {
        return origPos;
    }

    public Tier getTier() {
        return tier;
    }

    public double getFP() {
        return FP;
    }

    public String getTeam() {
        return team;
    }

    public boolean getSuperstar() {
        return superstar;
    }

    public void setStackFP() {
        FP = FP * 1.1;
    }


    @Override
    public int compareTo(Card o) {
        //TODO Complete comparison rules

        double diff = this.FP - o.FP;
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            return 0;
        }
    }

}
