/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.walkingStrategy;

public class WalkingStrategyFactory {
    private static WalkingStrategyFactory instance;

    private WalkingStrategyFactory() {}

    public static WalkingStrategyFactory getInstance() {
        if (instance == null) {
            instance = new WalkingStrategyFactory();
        }
        return instance;
    }

    public WalkingStrategy getStrategy(String type) {
        if (type.equals("Aggressive")) {
            // If the most important strategy (the first one) doesn't decide the direction, use the latter one
            CompositeWalkingStrategy aggressiveFollowerComposite = new BasicCompositeWalkingStrategy();
            aggressiveFollowerComposite.addStrategy(new AggressiveFollower());
            aggressiveFollowerComposite.addStrategy(new RandomWalk());
            return aggressiveFollowerComposite;
        }
        if (type.equals("PacActor")) {
            return new PacActorStrategy();
        }
        return new RandomWalk();

    }
}
