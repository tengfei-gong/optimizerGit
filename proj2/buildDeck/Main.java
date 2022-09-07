package buildDeck;

public class Main {

    public static void main(String[] args) {
        //System.out.println(deck.getSize());

        Game game = new Game(Card.Tier.RM);

        game.shuffleTwoDecks();

        while(!game.stop()) {
            game.play();
            game.updateLineUp();
            game.updateCurrDeck();



        }


    }
}

