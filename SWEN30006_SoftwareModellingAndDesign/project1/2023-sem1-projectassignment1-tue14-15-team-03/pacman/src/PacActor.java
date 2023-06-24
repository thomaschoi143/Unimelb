/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

public abstract class  PacActor extends MovingActor {
    private static final int nbSprites = 4;
    private final static String filename = "pacpix.gif";
    private final static boolean isRotatable = true;
    private int idSprite = 0;
    private int nbPillsAndGold = 0;
    private int score = 0;

    public PacActor(Game game, int seed) {
        super(game, isRotatable, filename, nbSprites, seed);  // Rotatable
    }

    public void act() {
        show(idSprite);
        idSprite++;
        if (idSprite == nbSprites)
            idSprite = 0;

        getGame().checkEatItem();
        getGame().getGameCallback().pacManLocationChanged(getLocation(), score, nbPillsAndGold);
    }

    public void addNbPillsAndGold() {
      nbPillsAndGold++;
    }
    public void addScore(int earningScore) {
      score += earningScore;
    }
    public int getNbPillsAndGold() {
        return nbPillsAndGold;
    }
    public int getScore() {
      return score;
    }

}
