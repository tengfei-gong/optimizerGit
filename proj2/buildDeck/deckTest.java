package buildDeck;

import buildDeck.Card.Tier;
import buildDeck.Card.Position;
import org.junit.Test;

import java.util.ArrayList;


public class deckTest {

    @Test
    public void testReadDeck() {
        CardDeck deck = new CardDeck();
        deck.readDeck();
        //System.out.println(deck.getSize());

        Game game = new Game(Tier.LEGEND);


        for (int i = 0; i < 300; i++) {

            game.shuffleTwoDecks();

            //System.out.println(game.getDeck().getTierCount(0));
            //System.out.println(game.getDeck().getTierCount(1));
            //System.out.println(game.getDeck().getTierCount(2));
            //System.out.println(game.getDeck().getTierCount(3));
            //System.out.println(game.getDeck().getTierCount(4));

            //System.out.println(game.getDeck().getTierCount(0));
            //System.out.println(game.getDeck().getTierCount(1));
            //System.out.println(game.getDeck().getTierCount(2));
            //System.out.println(game.getDeck().getTierCount(3));
            //System.out.println(game.getDeck().getTierCount(4));


            game.play();
            ArrayList<Card> hand = game.getHand(i);
            ArrayList<Position> summPos = game.getPosition(i);
            boolean isStacked = game.getStacked(i);
            Tier gameTier = game.getTier();

            double totalFP = hand.get(0).getFP() + hand.get(1).getFP() + hand.get(2).getFP() + hand.get(3).getFP() + hand.get(4).getFP();

            System.out.println("Hand: " + i + ",  contestTier: " + gameTier + ",  TotalFP: " + totalFP + ",  stacked: " + isStacked);

            System.out.println(hand.get(0).getName() + ",  TIER:" + hand.get(0).getTier() + ",  SERIAL:" + hand.get(0).getSerial() + ",  FP:"  + hand.get(0).getFP() + ",  POS:" + summPos.get(0) +  ",  OrigPOS:" + hand.get(0).getOrigPos() +  ",  SS:"  + hand.get(0).getSuperstar() + ",  Team:"  + hand.get(0).getTeam());
            System.out.println(hand.get(1).getName() + ",  TIER:" + hand.get(1).getTier() + ",  SERIAL:" + hand.get(1).getSerial() + ",  FP:"  + hand.get(1).getFP() + ",  POS:" + summPos.get(1) +  ",  OrigPOS:" + hand.get(1).getOrigPos() +  ",  SS:"  + hand.get(1).getSuperstar() + ",  Team:"  + hand.get(1).getTeam());
            System.out.println(hand.get(2).getName() + ",  TIER:" + hand.get(2).getTier() + ",  SERIAL:" + hand.get(2).getSerial() + ",  FP:"  + hand.get(2).getFP() + ",  POS:" + summPos.get(2) +  ",  OrigPOS:" + hand.get(2).getOrigPos() +  ",  SS:"  + hand.get(2).getSuperstar() + ",  Team:"  + hand.get(2).getTeam());
            System.out.println(hand.get(3).getName() + ",  TIER:" + hand.get(3).getTier() + ",  SERIAL:" + hand.get(3).getSerial() + ",  FP:"  + hand.get(3).getFP() + ",  POS:" + summPos.get(3) +  ",  OrigPOS:" + hand.get(3).getOrigPos() +  ",  SS:"  + hand.get(3).getSuperstar() + ",  Team:"  + hand.get(3).getTeam());
            System.out.println(hand.get(4).getName() + ",  TIER:" + hand.get(4).getTier() + ",  SERIAL:" + hand.get(4).getSerial() + ",  FP:"  + hand.get(4).getFP() + ",  POS:" + summPos.get(4) +  ",  OrigPOS:" + hand.get(4).getOrigPos() +  ",  SS:"  + hand.get(4).getSuperstar() + ",  Team:"  + hand.get(4).getTeam());
            System.out.println();

        }
    }

    @Test
    public void testPersistence() {

        CardDeck deck = new CardDeck();
        deck.readDeck();
        //System.out.println(deck.getSize());

        Game game = new Game(Tier.RM);

        game.shuffleTwoDecks();

        while(!game.stop()) {
            game.play();
            game.updateLineUp();

        }
    }

}
