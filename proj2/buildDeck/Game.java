package buildDeck;

import buildDeck.Card.Tier;
import buildDeck.Card.Position;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Game {
    //private enum GameTier {CORE, RARE, ELITE, LEGEND, RM};

    //private Tier gameTier;

    private ArrayList<ArrayList<Card>> hand;

    private ArrayList<ArrayList<Position>> summaryPosition;

    private ArrayList<Integer> handSize;
    private ArrayList<HashMap<Position, Boolean>> handPosition;

    private ArrayList<Boolean> stacked;

    private ArrayList<HashSet<String>> namesVisited;

    private ArrayList<Boolean> superstarDeck;
    private CardDeck deck;
    private CardDeck tmpDeck;

    private int currTier;

    //QB to stack
    private ArrayList<String> qbStackArr;


    public Game(Tier tier) {
        deck = new CardDeck();
        tmpDeck = new CardDeck();
        deck.readDeck();
        //gameTier = tier;
        handSize = new ArrayList<Integer>();
        namesVisited = new ArrayList<HashSet<String>>();
        //for (int i = 0; i < 5; i++) {
        //    namesVisited.add(new HashSet<String>());
        //}
        handPosition = new ArrayList<>();
        hand = new ArrayList<ArrayList<Card>>();
        currTier = 0; //Initialize game at RM
        superstarDeck = new ArrayList<Boolean>();
        summaryPosition = new ArrayList<ArrayList<Position>>();
        stacked = new ArrayList<Boolean>();
        qbStackArr = new ArrayList<String>();
        writeLineUp();
        loadQbStack();
        currTier = 0;



        //for (int i = 0; i < 5; i++) {
        //    hand[i] = new ArrayList<>();
        //}
        //hand[0] = new ArrayList<Card>();
        //hand[1] = new ArrayList<Card>();
        //hand[2] = new ArrayList<Card>();

        //initPositions();
    }

    public ArrayList<Card> getHand(int ind) {
        return hand.get(ind);
    }

    public ArrayList<Position> getPosition(int ind) {
        return summaryPosition.get(ind);
    }

    public boolean getStacked(int ind) {
        return stacked.get(ind);
    }


    private void initPositions() {
        HashMap positions = new HashMap<Position, Boolean>();
        positions.put(Position.QB, false);
        positions.put(Position.RB, false);
        positions.put(Position.WR, false);
        positions.put(Position.WRTE, false);
        positions.put(Position.FLEX, false);
        handPosition.add(positions);
    }

    public void shuffleTwoDecks() {
        int secondSize = tmpDeck.getSize();
        for (int i = 0; i < secondSize; i++) {
            Card card = tmpDeck.removeCard();
            deck.addCard(card);
        }
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

    //TODO play
    public void play() {

        if (currTier == 5) {
            return;
        }
        hand.add(new ArrayList<Card>());
        initPositions();
        handSize.add(0);
        namesVisited.add(new HashSet<String>());
        superstarDeck.add(false);
        summaryPosition.add(new ArrayList<Position>());

        //Start from tier0 RM
        if (!playable(currTier)) {
            //System.out.println("Change Tier");
            currTier ++;
        }




        pickFirstPart(currTier);
        pickSecondPart(currTier);


        //Post process check current hand
        if (!checkMinTierPositions(currTier)) {
            //System.out.println("Change Tier");
            currTier ++;
        }


        stacked.add(false);

        if (ifStack()) {
            stackEffectNew();
        }


    }

    public boolean stop(){
        if (!playable(currTier)
                && currTier == 4) {
            return true;
        }
        return false;
    }

    public int getTierCount(int tierNum) {
        return deck.getTierCount(tierNum);
    }



    //TODO check if game can be played at the current tier.
    //If not playable, tier++;
    public boolean playable(int tier) {

        if (tier == 4 && deck.getTierCount(4) >=5) {
            return true;
        }

        shuffleTwoDecks();

        boolean atLeastAbove = false; // At least two above or same tier.
        int sumAbove = 0;
        for (int i = tier; i >= 0; i--) {
            sumAbove += deck.getTierCount(i);
        }
        //System.out.println(sumAbove);

        //Count each tier

        if (tier == 0 || tier == 1) {
            if (sumAbove >= 2) {
                atLeastAbove = true;
            }
        } else if(tier == 2 || tier == 3) {
            if (sumAbove >= 4) {
                atLeastAbove = true;
            }
        } else {
            if (sumAbove >= 5) {
                atLeastAbove = true;
            }
        }

        boolean atLeastBelow = false;
        int sumBelow = 0;
        for (int i = tier+1; i <= 4; i++) {
            sumBelow += deck.getTierCount(i);
        }
        //System.out.println(sumBelow);
        if (tier == 0 || tier == 1) {
            if (sumBelow >= 3) {
                atLeastBelow = true;
            }

        } else if(tier == 2 || tier == 3) {
            if (sumBelow >= 1) {
                atLeastBelow = true;
            }
        } else {
            atLeastBelow = true;
            }

        return (atLeastAbove && atLeastBelow);
    }



    public boolean checkMinTierPositions(int tier) {
        shuffleTwoDecks();
        boolean validDeck = true;
        Tier enumTier = tierMap(tier);
        int numCardsAbove = 0;
        for (int i = 0; i < 5; i++ ) {
            //Position of current hand
            if (hand.get(hand.size()-1).get(i).getTier().compareTo(enumTier) >= 0) {
                numCardsAbove++;
            }
        }

        if (tier == 0 || tier == 1) {
            if (numCardsAbove < 2) {
                validDeck = false;
            }
        } else if(tier == 2 || tier == 3) {
            if (numCardsAbove < 4) {
                validDeck = false;
            }
        } else {
            if (numCardsAbove < 5) {
                validDeck = false;
            }
        }


        shuffleTwoDecks();
        return validDeck;
    }





    public Tier getTier() {
        return tierMap(currTier);
    }

    public CardDeck getDeck() {
        return deck;
    }


    public void addCardToHand(Card card, Position pos, String name) {
        hand.get(hand.size() - 1).add(card);
        summaryPosition.get(summaryPosition.size()-1).add(pos);
        handPosition.get(handPosition.size() - 1).replace(pos, true);

        int updateHandSize = handSize.get(handSize.size() - 1) + 1;
        handSize.set(handSize.size() - 1, updateHandSize); //Set new hand size



        namesVisited.get(namesVisited.size() - 1).add(name);
    }

    public void pickFirstPart(int tier) {
        shuffleTwoDecks();
        //Pick only from the same tier or above
        int size = deck.getSize();
        Tier gameTier = tierMap(tier); // play this tier
        for (int i = 0; i < size; i++) {
            Card card = deck.removeCard(); // highest priority card

            Position pos = card.getPos();
            String name = card.getName();
            if (card.getTier().compareTo(gameTier) >= 0
                && !namesVisited.get(namesVisited.size() - 1).contains(name)) {// if same tier or above, and name not already contained

                //TODO change superstar check position (and functionalize)
                if (card.getSuperstar()) { // Check for superstar. if true and no superstar in hand yet, make superstar hand.
                    if (!superstarDeck.get(superstarDeck.size()-1)) {
                        superstarDeck.set(superstarDeck.size()-1, true);
                    } else {
                        tmpDeck.addCard(card);
                        continue;
                    }
                }

                if (!handPosition.get(handPosition.size() - 1).get(pos)) { //if position is open, add card. This includes adding TE players to WRTE slot.
                    addCardToHand(card, pos, name);
                }
                // else if
                else if (pos.compareTo(Position.WR) == 0
                        && !handPosition.get(handPosition.size() - 1).get(Position.WRTE)){ //else if card is second WR and WR is already taken but WRTE is not taken.
                    addCardToHand(card, Position.WRTE, name);
                } else if (!handPosition.get(handPosition.size() - 1).get(Position.FLEX)
                && pos.compareTo(Position.QB) != 0 ) { //if position is filled and card position is not QB, check flex.
                    addCardToHand(card, Position.FLEX, name); //add to hand and set FLEX to true
                } else {
                    tmpDeck.addCard(card);
                }
            } else {
                tmpDeck.addCard(card);
            }

            if (gameTier.equals(Tier.RM) || gameTier.equals(Tier.LEGEND)) {
                if (handSize.get(handSize.size() - 1) == 2) {
                    return;
                }
            } else if (gameTier.equals(Tier.ELITE) || gameTier.equals(Tier.RARE)) {
                if (handSize.get(handSize.size() - 1) == 4) {
                    return;
                }
            } else {
                if (handSize.get(handSize.size() - 1) == 5) {
                    return;
                }
            }
        }
    }


    //TODO lower tiers only.
    public void pickSecondPart(int tier) {
        shuffleTwoDecks();
        Tier gameTier = tierMap(tier);
        int size = deck.getSize();
        for (int i = 0; i < size; i++) {
            Card card = deck.removeCard(); // highest priority card
            Position pos = card.getPos();
            String name = card.getName();
            //TODO check for superstar, cannot play here

            if (card.getTier().compareTo(gameTier) < 0  // if card is of lower tier
                    && !namesVisited.get(namesVisited.size() - 1).contains(name)) { //if name not already contained

                if (card.getSuperstar()) { // Just skip and move to next card.
                        tmpDeck.addCard(card);
                        continue;
                    }

                if (!handPosition.get(handPosition.size() - 1).get(pos)) { //if position not already taken. For example WRTE is not already taken for a TE card.
                    addCardToHand(card, pos, name);
                } else if (pos.compareTo(Position.WR) == 0
                            && !handPosition.get(handPosition.size() - 1).get(Position.WRTE)){ //else if card is second WR and WRTE is not already taken.
                    addCardToHand(card, Position.WRTE, name);
                } else if (!handPosition.get(handPosition.size() - 1).get(Position.FLEX)
                && pos.compareTo(Position.QB) != 0) { //if position is closed and card position is not QB, check flex.
                    addCardToHand(card, Position.FLEX, name); //add to hand and set FLEX to true
                } else {
                    tmpDeck.addCard(card);
                }
            } else {
                tmpDeck.addCard(card);
            }
            //System.out.println(handSize.get(handSize.size() - 1));
            if (handSize.get(handSize.size() - 1) == 5) {
                return;
            }
        }
    }




    // Assume first card in remaining deck is QB or RB.
    private void stackEffect() {
        shuffleTwoDecks();

        int qbInd = 0;
        int wrInd = 0;
        int wrteInd = 0;
        int flexInd = 0;

        double fp0 = 0;
        double fp1 = 0;
        double fp2 = 0;
        double fp3 = 0;
        //CardDeck tmpDeckStack = new CardDeck();

        Position wr_or_te = Position.WR; //If using WR from same team as QB, which position to replace in hand, WR or TE?

        ArrayList<Card> currentHand = copyCurrentHand();
        ArrayList<Position> currentPositions = summaryPosition.get(summaryPosition.size()-1);

        //System.out.println(currentHand.get(0).getName());
        //System.out.println(currentHand.get(1).getName());
        //System.out.println(currentHand.get(2).getName());
        //System.out.println(currentHand.get(3).getName());
        //System.out.println(currentHand.get(4).getName() + currentHand.get(4).getFP());



        for (int i = 0; i < 5; i++) { //Get indices for RB and QB in deck
            if (currentPositions.get(i).equals(Position.QB)) {
                qbInd = i;
            }
            if (currentPositions.get(i).equals(Position.WR)) {
                wrInd = i;
            }
            if (currentPositions.get(i).equals(Position.WRTE)) {
                wrteInd = i;
            }

            if (currentPositions.get(i).equals(Position.FLEX)) {
                flexInd = i;
            }

        }

        //System.out.println("wrInd " + wrInd);
        //System.out.println("wrteInd " + wrteInd);

        //Look at the lower-FP player between wr and wrte
        if (currentHand.get(wrInd).getFP() >= currentHand.get(wrteInd).getFP()) {
            wr_or_te = currentHand.get(wrInd).getPos();
        } else {
            wr_or_te = currentHand.get(wrteInd).getPos();
            wrInd = wrteInd; // use wrte index as wr index in following
        }



        //check if FLEX is WR and already teammate
        if (currentHand.get(qbInd).getTeam().equals(currentHand.get(flexInd).getTeam())
        && currentHand.get(flexInd).getOrigPos().equals("WR")) {
            stacked.set(stacked.size()-1, true);
            hand.get(hand.size() - 1).get(flexInd).setStackFP();
        }

        //Check if wr already teammates
        if (currentHand.get(qbInd).getTeam().equals(currentHand.get(wrInd).getTeam())) {
            stacked.set(stacked.size()-1, true);
            hand.get(hand.size() - 1).get(wrInd).setStackFP();
            return;
        }

        fp0 = getTotalFP(hand.get(hand.size() - 1));
        //System.out.println(getTotalFP(currentHand));

        //Replace QB
        currentHand = copyCurrentHand();
        //System.out.println(getTotalFP(currentHand));
        fp1 = fpIfReplacing(currentHand, Position.QB, qbInd, wrInd, false);

        //Replace RB
        currentHand = copyCurrentHand();
        //System.out.println(getTotalFP(currentHand));
        fp2 = fpIfReplacing(currentHand, Position.WR, wrInd, qbInd, false);


        //System.out.println(fp0);
        //System.out.println(fp1);
        //System.out.println(fp2);

        double fp = 0;
        currentHand = copyCurrentHand();
        if (fp0 > fp1 && fp0 > fp2) {
            stacked.set(stacked.size()-1, false);
            return; //Do nothing if original still the largest
        } else if (fp1 > fp0 && fp1 > fp2) {
            //System.out.println("this happened");
            stacked.set(stacked.size()-1, true);
            fp = fpIfReplacing(currentHand, Position.QB, qbInd, wrInd, true);
        } else if (fp2 > fp0 && fp2 > fp1) {

            stacked.set(stacked.size()-1, true);
            fp = fpIfReplacing(currentHand, Position.WR, wrInd, qbInd, true);
        }

        double fpDummy = fp + 1;
    }





        //ArrayList<Card> firstPair = new ArrayList<>(); // first QB and first RB with highest FP
        //ArrayList<Card> secondPair = new ArrayList<>(); // First QB with highest FP and his teammate
        //ArrayList<Card> thirdPair = new ArrayList<>(); // First RB with highest FP and his teammate
        //CardDeck tmpDeckStack = new CardDeck();


       // Card card = deck.removeCard();

    private double fpIfReplacing(ArrayList<Card> currHand, Position pos, int playerInd, int teammateInd, boolean update) {
        double res = 0;
        boolean replaced = false;
        for (int i = 0; i < deck.getSize(); i++) {
            Card newCard = deck.removeCard();
            if (newCard.getPos().equals(pos)
                    && newCard.getTier().equals(currHand.get(playerInd).getTier())
                    && newCard.getTeam().equals(currHand.get(teammateInd).getTeam())) {

                if (newCard.getSuperstar()) { // Check for superstar. if true and no superstar in hand yet, make superstar hand.
                    if (newCard.getTier().compareTo(tierMap(currTier)) < 0) {
                        tmpDeck.addCard(newCard);
                        continue;
                    }

                    if (!superstarDeck.get(superstarDeck.size()-1)) {
                        if (update) {
                            superstarDeck.set(superstarDeck.size() - 1, true);
                        }
                    } else {
                        tmpDeck.addCard(newCard);
                        continue;
                    }
                }


                replaced = true;



                if (!update) {
                    currHand.set(playerInd, newCard);
                    tmpDeck.addCard(newCard); //Add a copy to tmp deck -- zero impact

                } else {
                    Card replacedCard = hand.get(hand.size() - 1).get(playerInd); //save replaced card
                    ArrayList<Card> newHand = hand.get(hand.size() - 1);
                    newHand.set(playerInd, newCard);
                    hand.set(hand.size() - 1, newHand);
                    tmpDeck.addCard(replacedCard); //add replaced card to tmp deck.
                    shuffleTwoDecks();
                    break;
                }
            } else {
                tmpDeck.addCard(newCard);

            }
        }
        shuffleTwoDecks();

        if (!replaced) {
            return 0;
        }

        //calculate FP with stacking effect
        if (pos.equals(Position.QB)) {
            if (!update) {

                for (int i = 0; i < 5; i++) {
                    //System.out.println(currHand.get(i).getName()+ " " + currHand.get(i).getFP());
                    if (i == teammateInd) {
                        res += currHand.get(i).getFP() * 1.1;
                    } else {
                        res += currHand.get(i).getFP();
                    }
                }

                currHand.get(teammateInd).setStackFP();
            } else {
                //System.out.println("this happened");
                //System.out.println(teammateInd);
                hand.get(hand.size()-1).get(teammateInd).setStackFP();

            }
        } else if (pos.equals(Position.WR)) {

            if (!update) {


                for (int i = 0; i < 5; i++) {

                    if (i == playerInd) {

                        res += currHand.get(i).getFP() * 1.1;
                    } else {
                        res += currHand.get(i).getFP();
                    }
                }

            } else {

                hand.get(hand.size()-1).get(playerInd).setStackFP();

            }
        }

        res = getTotalFP(currHand);
        return res;
    }



    private void fpIfReplacingUpdate(ArrayList<Card> currHand, Position pos, int playerInd, int teammateInd, boolean update) {
        double res = 0;
        boolean replaced = false;
        for (int i = 0; i < deck.getSize(); i++) {
            Card newCard = deck.removeCard();
            if (newCard.getPos().equals(pos)
                    && newCard.getTier().equals(currHand.get(playerInd).getTier())
                    && newCard.getTeam().equals(currHand.get(teammateInd).getTeam())) {

                if (newCard.getSuperstar()) { // Check for superstar. if true and no superstar in hand yet, make superstar hand.
                    if (newCard.getTier().compareTo(tierMap(currTier)) < 0) {
                        tmpDeck.addCard(newCard);
                        continue;
                    }

                    if (!superstarDeck.get(superstarDeck.size()-1)) {
                        if (update) {
                            superstarDeck.set(superstarDeck.size() - 1, true);
                        }
                    } else {
                        tmpDeck.addCard(newCard);
                        continue;
                    }
                }


                replaced = true;

                if (!update) {
                    currHand.set(playerInd, newCard);
                    tmpDeck.addCard(newCard); //Add a copy to tmp deck -- zero impact

                } else {

                    Card replacedCard = hand.get(hand.size() - 1).get(playerInd); //save replaced card
                    ArrayList<Card> newHand = hand.get(hand.size() - 1);
                    newHand.set(playerInd, newCard);
                    hand.set(hand.size() - 1, newHand);
                    tmpDeck.addCard(replacedCard); //add replaced card to tmp deck.
                    shuffleTwoDecks();
                    break;
                }
            } else {
                tmpDeck.addCard(newCard);

            }
        }
        shuffleTwoDecks();

        if (!replaced) {
            return;
        }

        //calculate FP with stacking effect
        if (pos.equals(Position.QB)) {
            if (!update) {

                for (int i = 0; i < 5; i++) {
                    if (i == teammateInd) {
                        res += currHand.get(i).getFP() * 1.1;
                    } else {
                        res += currHand.get(i).getFP();
                    }
                }

                currHand.get(teammateInd).setStackFP();
            } else {

                for (int i = 0; i < 5; i++) {
                    if (i == teammateInd) {
                        res += hand.get(hand.size()-1).get(teammateInd).getFP() * 1.1;
                    } else {
                        res += hand.get(hand.size()-1).get(teammateInd).getFP();
                    }
                }

            }
        } else if (pos.equals(Position.RB)) {
            if (!update) {

                for (int i = 0; i < 5; i++) {
                    if (i == playerInd) {
                        res += currHand.get(i).getFP() * 1.1;
                    } else {
                        res += currHand.get(i).getFP();
                    }
                }

            } else {

                for (int i = 0; i < 5; i++) {
                    if (i == playerInd) {
                        res += hand.get(hand.size()-1).get(i).getFP() * 1.1;
                    } else {
                        res += hand.get(hand.size()-1).get(i).getFP();
                    }
                }


            }
        }

        res = getTotalFP(currHand);
    }




    private double getTotalFP(ArrayList<Card> currHand) {
        return currHand.get(0).getFP() + currHand.get(1).getFP() + currHand.get(2).getFP()
                + currHand.get(3).getFP() + currHand.get(4).getFP();
    }



    private boolean isCaseOne() {
        //Check first two cards of the latest hand
        if ((hand.get(hand.size() - 1).get(0).getOrigPos().equals("QB")
                && hand.get(hand.size() - 1).get(1).getOrigPos().equals("RB"))
                || (hand.get(hand.size() - 1).get(0).getOrigPos().equals("RB")
                && hand.get(hand.size() - 1).get(1).getOrigPos().equals("QB"))) {
            return true;
        }
        return false;
    }


/**
    private void caseOneDeck() {

        //if (hand.get(hand.size() - 1).get(0).getTeam().equals(hand.get(hand.size() - 1).get(1).getTeam())) {
        //    return; // Do nothing if already same team
        //}

        //private void findStack(1) {}
        shuffleTwoDecks();

        double originTotalFP = hand.get(hand.size() - 1).getTotalFP();

        hand1 = findStack1();
        double totalFP1 = hand1.getTotalFP();
        hand2 = findStack2();
        double totalFP2 = hand2.getTotalFP();
        hand3 = findStack3();
        double totalFP3 = hand3.getTotalFP();
        hand4 = findStack4();
        double totalFP4 = hand4.getTotalFP();
        hand5 = findStack5();
        double totalFP5 = hand5.getTotalFP();

        find maxTotalFP

        if (maxTotalFP = originTotalFP) {
            change nothing
        } else if (maxTotalFP == totalFP1) {
            update = findStack1(update = true); //actual update
        } else if(maxTotalFP == totalFP2) {
            update = findStack2(update = true);
        } else if(maxTotalFP == totalFP3) {
            update = findStack3(update = true);
        } else if (maxTotalFP == totalFP4) {
            update = findStack4(update = true);
        } else if (maxTotalFP == totalFP5) {
            update = findStack5(update = true);
        }

        //findStack1
        //find next teammate RB from lower-tier deck
        // => compare its FP with smallest FP among the three cards from the lower tier
        // => if RB with team stacking effect has larger FP than the smallest FP card, replace the card with the RB.
        // => based on position of the replaced card, find its sub from the higher tier deck.
        // => make a hypothetical deck based on this.
        // => get its total FP.
        // => All done with zero impact on the original deck. Make copies if needed.
        // => shuffle

        //findStack2
        //find next teammate QB from lower-tier deck
        // => compare its FP with smallest FP among the three cards from the lower tier
        // => if the QB with team stacking effect has larger FP than the smallest FP card, replace the card with the QB.
        // => based on position of the replaced card, find its sub from the higher tier deck.
        // => make a hypothetical deck based on this.
        // => get its total FP.
        // => All done with zero impact on the original deck. Make copies if needed.
        // => shuffle

        //findStack3
        //find next QB-RB pair from lower-tier deck
        // => compare their FP sum with the lowest FP pair in the lower-tier deck.
        // => if the lower-tier pair with team stacking effect has larger FP than the current top-tier sum, replace the cards with the lowest-sum pair in the lower tier.
        // => based on position of the replaced cards, find their best subs from the higher tier deck.
        // => make a hypothetical deck based on this.
        // => get its total FP.
        // => All done with zero impact on the original deck. Make copies if needed.
        // => shuffle


        //findStack4
        //find next teammate RB from upper-tier deck
        // => compare its stacked FP with current RB.
        // => if new RB with stacking effect has larger FP than original RB, replace.
        // => make a hypothetical deck based on this.
        // => get its total FP.
        // => All done with zero impact on the original deck. Make copies if needed.
        // => shuffle

        //findStack5
        //find next teammate QB from upper-tier deck
        // => compare its stacked FP with current QB.
        // => if new RB with stacking effect has larger FP than original QB, replace.
        // => make a hypothetical deck based on this.
        // => get its total FP.
        // => All done with zero impact on the original deck. Make copies if needed.
        // => shuffle


    }

    */

    private boolean isCaseTwo() {
        //Check first two cards of the latest hand
        if ((hand.get(hand.size()-1).get(0).getOrigPos() == "QB"
                && hand.get(hand.size()-1).get(1).getOrigPos() != "RB")
                || (hand.get(hand.size()-1).get(0).getOrigPos() != "RB"
                && hand.get(hand.size()-1).get(1).getOrigPos() == "QB")) {
            return true;
        } else {
            return false;
        }
    }

    private void caseTwoDeck() {}

    private boolean isCaseThree() {
        //Check first two cards of the latest hand
        if ((hand.get(hand.size()-1).get(0).getOrigPos() == "RB"
                && hand.get(hand.size()-1).get(1).getOrigPos() != "QB")
                || (hand.get(hand.size()-1).get(0).getOrigPos() != "QB"
                && hand.get(hand.size()-1).get(1).getOrigPos() == "RB")) {
            return true;
        } else {
            return false;
        }
    }

    private void caseThreeDeck() {}

    private boolean isCaseFour() {
        //Check first two cards of the latest hand
        if (hand.get(hand.size()-1).get(0).getOrigPos() != "RB"
                && hand.get(hand.size()-1).get(0).getOrigPos() != "QB"
                && hand.get(hand.size()-1).get(1).getOrigPos() != "RB"
                && hand.get(hand.size()-1).get(1).getOrigPos() != "QB") {
            return true;
        } else {
            return false;
        }
    }

    private void caseFourDeck() {}



    private ArrayList<Card> copyCurrentHand() {
        ArrayList<Card> copy = new ArrayList<Card>();
        for (int i = 0; i < 5; i++) {
            Card card = hand.get(hand.size()-1).get(i).copyCard();
            copy.add(card);
        }
        return copy;
    }



    private ArrayList<Card> findStack1(boolean update) {
        CardDeck tmpDeckStack = new CardDeck();
        ArrayList<Card> copyHand = copyCurrentHand();
        String team = null; //QB team
        int ind = 0; // RB index in current hand
        double minFP = 10000; //minimum FP in hand
        int indMinFP = 0; //player index with min FP in hand
        Position posMinFP = Position.QB; //replace this position among the 3 lower-tier cards. QB is dummy here.

        for (int i = 0; i < 5; i++) { //find QB's team
            Card player = hand.get(hand.size() - 1).get(i);
            if (player.getPos().equals(Position.QB)) {
                team = player.getTeam();
            }
            if (player.getPos().equals(Position.RB)) {
                ind = i;
            }
            if (player.getFP() < minFP) {
                minFP = player.getFP();
                indMinFP = i;
                posMinFP = player.getPos();
            }
        }

        Card replacedCardLower = new Card();
        Card replacedCardHigher = new Card();
        Card card = deck.removeCard(); // remove card from deck and check
        if (card.getPos().equals(Position.RB) //Position is RB
                && card.getTeam().equals(team) //same team as QB
                && card.getTier().compareTo(tierMap(currTier)) < 0) { //lower-tier card
            if (update) {
                replacedCardLower = hand.get(hand.size() - 1).get(indMinFP); // save replaced card, send it to tmp deck.
                tmpDeckStack.addCard(replacedCardLower); //add replaced card to tmp stack deck
                hand.get(hand.size()-1).set(indMinFP, card);
            } else {
                copyHand.set(indMinFP, card); // replace min FP card with the RB card in the copy hand.
                tmpDeckStack.addCard(card);} // also add card to tmp stack deck (zero impact to original deck)
        } else if (card.getPos().equals(posMinFP) //is replaced position
                && card.getTier().compareTo(tierMap(currTier)) >= 0){ //top-tier
            if (update) {
                replacedCardHigher = hand.get(hand.size() - 1).get(ind); //save the RB card to be replaced.
                tmpDeckStack.addCard(replacedCardHigher); //add replaced card to tmp stack deck
                hand.get(hand.size()-1).set(ind, card);
            } else {
                copyHand.set(ind, card); // replace original RB card with the new card.
                tmpDeckStack.addCard(card); // also add card to tmp stack deck (zero impact to original deck)
            }
        } else {
            tmpDeckStack.addCard(card);
        }

        //shuffle tmp stack deck
        int tmpSize = tmpDeckStack.getSize();
        for (int i = 0; i < tmpSize; i++) {
            Card tmpCard = tmpDeckStack.removeCard();
            deck.addCard(tmpCard);
        }
        return copyHand;
    }


    private ArrayList<Card> findStack2(boolean update) {
        CardDeck tmpDeckStack = new CardDeck();
        ArrayList<Card> copyHand = copyCurrentHand();
        String team = null; //QB team
        int ind = 0; // RB index in current hand
        double minFP = 10000; //minimum FP in hand
        int indMinFP = 0; //player index with min FP in hand
        Position posMinFP = Position.QB; //QB is dummy here. replace this position among the 3 lower-tier cards

        for (int i = 0; i < 5; i++) { //find QB's team
            Card player = hand.get(hand.size() - 1).get(i);
            if (player.getPos().equals(Position.RB)) {
                team = player.getTeam();
            }
            if (player.getPos().equals(Position.QB)) { // find QB index
                ind = i;
            }
            if (player.getFP() < minFP) {
                minFP = player.getFP();
                indMinFP = i;
                posMinFP = player.getPos();
            }
        }

        Card replacedCardLower = new Card();
        Card replacedCardHigher = new Card();
        Card card = deck.removeCard(); // remove card from deck and check
        if (card.getPos().equals(Position.QB) //Position is QB
                && card.getTeam().equals(team) //same team as RB
                && card.getTier().compareTo(tierMap(currTier)) < 0) { //lower-tier card
            if (update) {
                replacedCardLower = hand.get(hand.size() - 1).get(indMinFP); // save replaced card, send it to tmp deck.
                tmpDeckStack.addCard(replacedCardLower); //add replaced card to tmp stack deck
                hand.get(hand.size()-1).set(indMinFP, card);
            } else {
                copyHand.set(indMinFP, card); // replace min FP card with the QB card in the copy hand.
                tmpDeckStack.addCard(card);} // also add card to tmp stack deck (zero impact to original deck)
        } else if (card.getPos().equals(posMinFP) //is replaced position
                && card.getTier().compareTo(tierMap(currTier)) >= 0){ //top-tier
            if (update) {
                replacedCardHigher = hand.get(hand.size() - 1).get(ind); //save the QB card to be replaced in the top-tier.
                tmpDeckStack.addCard(replacedCardHigher); //add replaced card to tmp stack deck
                hand.get(hand.size()-1).set(ind, card);
            } else {
                copyHand.set(ind, card); // replace original QB card with the new card.
                tmpDeckStack.addCard(card); // also add card to tmp stack deck (zero impact to original deck)
            }
        } else {
            tmpDeckStack.addCard(card);
        }

        //shuffle tmp stack deck
        int tmpSize = tmpDeckStack.getSize();
        for (int i = 0; i < tmpSize; i++) {
            Card tmpCard = tmpDeckStack.removeCard();
            deck.addCard(tmpCard);
        }
        return copyHand;
    }


    private void findStack3(boolean update) {
    }


    private ArrayList<Card> findStack4(boolean update) {
        CardDeck tmpDeckStack = new CardDeck();
        ArrayList<Card> copyHand = copyCurrentHand();
        String team = null; //QB team
        int ind = 0; // RB index in current hand
        double minFP = 10000; //minimum FP in hand
        int indMinFP = 0; //player index with min FP in hand
        Position posMinFP = Position.QB; //QB is dummy here. replace this position among the 3 lower-tier cards

        for (int i = 0; i < 5; i++) { //find QB's team
            Card player = hand.get(hand.size() - 1).get(i);
            if (player.getPos().equals(Position.RB)) {
                team = player.getTeam();
            }
            if (player.getPos().equals(Position.QB)) { // find QB index
                ind = i;
            }
            if (player.getFP() < minFP) {
                minFP = player.getFP();
                indMinFP = i;
                posMinFP = player.getPos();
            }
        }

        Card replacedCardLower = new Card();
        Card replacedCardHigher = new Card();
        Card card = deck.removeCard(); // remove card from deck and check
        if (card.getPos().equals(Position.QB) //Position is QB
                && card.getTeam().equals(team) //same team as RB
                && card.getTier().compareTo(tierMap(currTier)) < 0) { //lower-tier card
            if (update) {
                replacedCardLower = hand.get(hand.size() - 1).get(indMinFP); // save replaced card, send it to tmp deck.
                tmpDeckStack.addCard(replacedCardLower); //add replaced card to tmp stack deck
                hand.get(hand.size()-1).set(indMinFP, card);
            } else {
                copyHand.set(indMinFP, card); // replace min FP card with the QB card in the copy hand.
                tmpDeckStack.addCard(card);} // also add card to tmp stack deck (zero impact to original deck)
        } else if (card.getPos().equals(posMinFP) //is replaced position
                && card.getTier().compareTo(tierMap(currTier)) >= 0){ //top-tier
            if (update) {
                replacedCardHigher = hand.get(hand.size() - 1).get(ind); //save the QB card to be replaced in the top-tier.
                tmpDeckStack.addCard(replacedCardHigher); //add replaced card to tmp stack deck
                hand.get(hand.size()-1).set(ind, card);
            } else {
                copyHand.set(ind, card); // replace original QB card with the new card.
                tmpDeckStack.addCard(card); // also add card to tmp stack deck (zero impact to original deck)
            }
        } else {
            tmpDeckStack.addCard(card);
        }

        //shuffle tmp stack deck
        int tmpSize = tmpDeckStack.getSize();
        for (int i = 0; i < tmpSize; i++) {
            Card tmpCard = tmpDeckStack.removeCard();
            deck.addCard(tmpCard);
        }
        return copyHand;
    }



    private void loadQbStack() {
        File qbStack = new File("./qbStack");
        String readQbStack =  Utils.readContentsAsString(qbStack);
        String PATTERN = "\\n";
        Pattern pattern = Pattern.compile(PATTERN);
        String[] qbArr = pattern.split(readQbStack);
        for (int i = 0; i < qbArr.length; i++) {
            qbStackArr.add(qbArr[i]);
        }
    }


    private boolean ifStack() {
        String qbName = "QB";
        for (int i = 0; i < 5; i++) {
            if(hand.get(hand.size()-1).get(i).getPos().equals(Position.QB)) {
                qbName = hand.get(hand.size()-1).get(i).getName();
            }
        }
        if (qbStackArr.contains(qbName)) {
            return true;
        }
        return false;
    }

    private void stackEffectNew() {
        shuffleTwoDecks();

        int wrInd = 0;
        int qbInd = 0;
        int wrteInd = 0;
        ArrayList<Position> positionArr = getPosition(hand.size() - 1); //Current deck positions
        for (int i = 0; i < 5; i++) {
            if (positionArr.get(i).equals(Position.WR)) {
                wrInd = i;
            }
            if (positionArr.get(i).equals(Position.QB)) {
                qbInd = i;
            }
            if (positionArr.get(i).equals(Position.WRTE)) {
                wrteInd = i;
            }

        }

        //Check if current hand is already stacked
        if (hand.get(hand.size()-1).get(wrInd).getTeam().equals(hand.get(hand.size()-1).get(qbInd).getTeam())
        || hand.get(hand.size()-1).get(wrteInd).getTeam().equals(hand.get(hand.size()-1).get(qbInd).getTeam())) {
            stacked.set(stacked.size()-1, true);
            //System.out.println(hand.size()-1);
            //System.out.println(hand.get(hand.size()-1).get(wrInd).getName());
            return;
        }



        Tier wrTier = hand.get(hand.size()-1).get(wrInd).getTier();
        Tier wrteTier = hand.get(hand.size()-1).get(wrteInd).getTier();
        String qbTeam = hand.get(hand.size()-1).get(qbInd).getTeam();

        for (int i = 0; i < deck.getSize(); i++) {
            Card card = deck.removeCard();

            if (card.getPos().equals(Position.WR)
                    && (card.getTeam().equals(qbTeam))
                    && (card.getTier().compareTo(wrTier)== 0)) {// Has to be same tier or above

                if (card.getSuperstar()) {
                    if (!superstarDeck.get(superstarDeck.size() - 1)
                    && card.getTier().compareTo(tierMap(currTier))>=0) {
                        superstarDeck.set(superstarDeck.size() - 1, true);
                    } else {
                        tmpDeck.addCard(card);
                        continue;
                    }
                }


                Card oldWR = hand.get(hand.size() - 1).get(wrInd); //Save original WR
                tmpDeck.addCard(oldWR);
                hand.get(hand.size() - 1).set(wrInd, card);
                stacked.set(stacked.size()-1, true);
                break;
            } else if (card.getPos().equals(Position.WRTE)
                    && (card.getTeam().equals(qbTeam))
                    && (card.getTier().compareTo(wrteTier)== 0)) {// Has to be same tier or above

                if (card.getSuperstar()) {
                    if (!superstarDeck.get(superstarDeck.size() - 1)
                            && card.getTier().compareTo(tierMap(currTier))>=0) {
                        superstarDeck.set(superstarDeck.size() - 1, true);
                    } else {
                        tmpDeck.addCard(card);
                        continue;
                    }
                }

                Card oldWRTE = hand.get(hand.size() - 1).get(wrteInd); //Save original WR
                tmpDeck.addCard(oldWRTE);
                hand.get(hand.size() - 1).set(wrteInd, card);
                stacked.set(stacked.size()-1, true);
                break;
            } else {
                tmpDeck.addCard(card);
            }
        }
        shuffleTwoDecks();

    }

    public static void writeLineUp() {

        File lineup = new File("./Lineup/lineup");
        Utils.writeContents(lineup, "");
    }



    public void updateLineUp() {
        File lineup = new File("./Lineup/lineup");

        //if (!lineup.exists()){
            //storyFile.createNewFile();
        //    Utils.writeContents(lineup, "");
        //}
        //new line

        int ind = hand.size() - 1;

        ArrayList<Card> hand = getHand(ind);
        ArrayList<Position> summPos = getPosition(ind);
        boolean isStacked = getStacked(ind);
        Tier gameTier = getTier();

        double totalFP = hand.get(0).getFP() + hand.get(1).getFP() + hand.get(2).getFP() + hand.get(3).getFP() + hand.get(4).getFP();



        String readLineup = Utils.readContentsAsString(lineup);

        String add = "Hand: " + ind + ",  contestTier: " + gameTier + ",  TotalFP: " + totalFP + ",  stacked: " + isStacked;
        readLineup = readLineup + add + "\n";

        add = hand.get(0).getName() + ",  TIER:" + hand.get(0).getTier() + ",  SERIAL:" + hand.get(0).getSerial() + ",  FP:"  + hand.get(0).getFP() + ",  POS:" + summPos.get(0) +  ",  OrigPOS:" + hand.get(0).getOrigPos() +  ",  SS:"  + hand.get(0).getSuperstar() + ",  Team:"  + hand.get(0).getTeam();
        readLineup = readLineup + add + "\n";

        add = hand.get(1).getName() + ",  TIER:" + hand.get(1).getTier() + ",  SERIAL:" + hand.get(1).getSerial() + ",  FP:"  + hand.get(1).getFP() + ",  POS:" + summPos.get(1) +  ",  OrigPOS:" + hand.get(1).getOrigPos() +  ",  SS:"  + hand.get(1).getSuperstar() + ",  Team:"  + hand.get(1).getTeam();
        readLineup = readLineup + add + "\n";

        add = hand.get(2).getName() + ",  TIER:" + hand.get(2).getTier() + ",  SERIAL:" + hand.get(2).getSerial() + ",  FP:"  + hand.get(2).getFP() + ",  POS:" + summPos.get(2) +  ",  OrigPOS:" + hand.get(2).getOrigPos() +  ",  SS:"  + hand.get(2).getSuperstar() + ",  Team:"  + hand.get(2).getTeam();
        readLineup = readLineup + add + "\n";

        add = hand.get(3).getName() + ",  TIER:" + hand.get(3).getTier() + ",  SERIAL:" + hand.get(3).getSerial() + ",  FP:"  + hand.get(3).getFP() + ",  POS:" + summPos.get(3) +  ",  OrigPOS:" + hand.get(3).getOrigPos() +  ",  SS:"  + hand.get(3).getSuperstar() + ",  Team:"  + hand.get(3).getTeam();
        readLineup = readLineup + add + "\n";

        add = hand.get(4).getName() + ",  TIER:" + hand.get(4).getTier() + ",  SERIAL:" + hand.get(4).getSerial() + ",  FP:"  + hand.get(4).getFP() + ",  POS:" + summPos.get(4) +  ",  OrigPOS:" + hand.get(4).getOrigPos() +  ",  SS:"  + hand.get(4).getSuperstar() + ",  Team:"  + hand.get(4).getTeam();
        readLineup = readLineup + add + "\n" + "\n";

        Utils.writeContents(lineup, readLineup);
    }


    public void writeCurrDeck() {

        shuffleTwoDecks();
        File remainingDeck = new File("./Lineup/deck");
        if (!remainingDeck.exists()){
            //remainingDeck.createNewFile();
            Utils.writeContents(remainingDeck, "");
        }


        String writeDeck = "RM:" + getTierCount(0) + "\n" + "Legendary:" + getTierCount(1) + "\n"
                + "Elite:" + getTierCount(2) + "\n" + "RARE:" + getTierCount(3) + "\n"
                + "CORE:" + getTierCount(4) + "\n";

        for (int ind = 0; ind < deck.getSize(); ind++) {

            Card card = deck.removeCard();
            tmpDeck.addCard(card);

            String add = card.getName() + ",  TIER:" + card.getTier() + ",  SERIAL:" + card.getSerial() + ",  FP:" + card.getFP() + ",  OrigPOS:" + card.getOrigPos() + ",  SS:" + ",  Team:" + card.getTeam();
            writeDeck = writeDeck + add + "\n";
        }
        shuffleTwoDecks();

        Utils.writeContents(remainingDeck, writeDeck);


    }

}

