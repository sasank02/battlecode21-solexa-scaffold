package framework2;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class BotSlanderer extends Bot {
	// Set home EC and stay within that radius
    public static void loop(RobotController theRC) throws GameActionException {
        Bot.init(theRC);
        while (true) {
            try {
                turn();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Clock.yield();
        }
    }

    /**
     * random movement, stupid.
     * TODO: One idea is to keep them within EC range to protect them, also started work on Nav class to run away from all unites sensored to impl here.
     * @throws GameActionException
     */
    public static void turn() throws GameActionException {
        Direction dir = Direction.values()[(int)(8*Math.random())];
        if(rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}
