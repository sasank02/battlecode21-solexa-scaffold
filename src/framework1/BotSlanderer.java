package framework1;

import battlecode.common.*;

public class BotSlanderer extends Bot {
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

    public static void turn() throws GameActionException {
        Direction dir = Direction.values()[(int)(8*Math.random())];
        if(rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}
