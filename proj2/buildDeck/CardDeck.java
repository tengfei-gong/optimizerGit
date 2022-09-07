package buildDeck;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

import buildDeck.Card.Tier;
import buildDeck.Card.Position;


public class CardDeck {
    PriorityQueue<Card> deck;
    int size;

    HashMap<Tier, Integer> countTier;


    public CardDeck() {
        Comparator<Card> CardComparator = new CardComparator();
        deck = new PriorityQueue<Card>(10, CardComparator);
        initCountTier();
        size = 0;
    }

    private void initCountTier() {
        countTier = new HashMap<Tier, Integer>();
        countTier.put(Tier.CORE, 0);
        countTier.put(Tier.RARE, 0);
        countTier.put(Tier.ELITE, 0);
        countTier.put(Tier.LEGEND, 0);
        countTier.put(Tier.RM, 0);

    }


    public int getTierCount(int tierNum) {
        Tier tier = tierMap(tierNum);
        return countTier.get(tier);
    }

    public void addCard(Card card) {
        deck.add(card);

        Tier tier = card.getTier();
        int addOne =  countTier.get(tier) + 1;
        countTier.replace(tier, addOne);

        size++;
    }



    public Card removeCard() {
        Card card = deck.poll();

        Tier tier = card.getTier();
        int minusOne =  countTier.get(tier) - 1;
        countTier.replace(tier, minusOne);

        size--;
        return card;
    }

    public int getSize() {
        return size;
    }

    public Card peekCard() {
        Card peek = deck.peek();
        return peek;
    }

    public void readDeck() {
        File name = new File("./name");
        String readName =  Utils.readContentsAsString(name);
        File serial = new File("./serial");
        String readSerial =  Utils.readContentsAsString(serial);
        File tier = new File("./tier");
        String readTier =  Utils.readContentsAsString(tier);
        File pos = new File("./pos");
        String readPos =  Utils.readContentsAsString(pos);
        File FP = new File("./FP");
        String readFP =  Utils.readContentsAsString(FP);

        File team = new File("./team");
        String readTeam =  Utils.readContentsAsString(team);
        File superstar = new File("./superstar");
        String readsuperstar =  Utils.readContentsAsString(superstar);

        // Define REGEX
        String PATTERN = "\\n";
        // Create a pattern using compile method
        Pattern pattern = Pattern.compile(PATTERN);
        String[] nameArr = pattern.split(readName);
        String[] serialArr = pattern.split(readSerial);
        String[] tierArr = pattern.split(readTier);
        String[] posArr = pattern.split(readPos);
        String[] FPArr = pattern.split(readFP);
        String[] teamArr = pattern.split(readTeam);

        String[] superstarArr = pattern.split(readsuperstar);

        int len = nameArr.length;
        //Random random = new Random(42);
        for (int i = 0; i < len; i++) {

            int sn = Integer.parseInt(serialArr[i]);
            Tier tierInput = tierMap(tierArr[i]);
            Position posInput = posMap(posArr[i]);
            double fpInput = Double.parseDouble(FPArr[i]);
            Card card = new Card(nameArr[i], sn, tierInput, posInput, fpInput, posArr[i], teamArr[i]);
            //Check superstar
            if (Arrays.asList(superstarArr).contains(nameArr[i])) {
                card.makeSuperstar();
            }
            //card.randomFP(random);
            addCard(card);
        }
    }


    public Tier tierMap(String tierString) {
        Tier tier = Tier.CORE;
        if (tierString.equals("Core")) {
            tier = Tier.CORE;
        } else if (tierString.equals("Rare")) {
            tier = Tier.RARE;
        } else if (tierString.equals("Elite")) {
            tier = Tier.ELITE;
        } else if (tierString.equals("Legendary")) {
            tier = Tier.LEGEND;
        } else if (tierString.equals("Reignmaker")) {
            tier = Tier.RM;
        }
        return tier;
    }

    public Tier tierMap(int tierNum) {
        Tier tier = Tier.CORE;
        if (tierNum == 4) {
            tier = Tier.CORE;
        } else if (tierNum == 3) {
            tier = Tier.RARE;
        } else if (tierNum == 2) {
            tier = Tier.ELITE;
        } else if (tierNum == 1) {
            tier = Tier.LEGEND;
        } else if (tierNum == 0) {
            tier = Tier.RM;
        }
        return tier;
    }

    public Position posMap(String posString) {
        Position pos = Position.QB;
        if (posString.equals("QB")) {
            pos = Position.QB;
        } else if (posString.equals("RB")) {
            pos = Position.RB;
        } else if (posString.equals("WR")) {
            pos = Position.WR;
        } else if (posString.equals("TE")) { //TODO set TE to WRTE for now. Add second WR later
            pos = Position.WRTE;
        } else {
            pos = Position.FLEX;
        }
        return pos;
    }




    public class CardComparator implements Comparator<Card> {


        @Override
        public int compare(Card x, Card y) {
            if (x.getTier().compareTo(y.getTier()) > 0) {
                return -1;
            }
            if (x.getTier().compareTo(y.getTier()) < 0) {
                return 1;
            }
            if (x.getTier().compareTo(y.getTier()) == 0) {
                if (x.getFP() < y.getFP()) {
                    return 1;
                }
                if (x.getFP() > y.getFP()) {
                    return -1;
                }
                if (x.getFP() == y.getFP()) {
                    if (x.getSerial() > y.getSerial()) {
                        return 1;
                    }
                    if (x.getSerial() < y.getSerial()) {
                        return -1;
                    }
                }
            }
            return 0;
        }
    }


}
