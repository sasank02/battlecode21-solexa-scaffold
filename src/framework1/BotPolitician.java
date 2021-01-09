package framework1;

import battlecode.common.*;

public class BotPolitician extends Bot {
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
        int actionRadius = rc.getType().actionRadiusSquared;
        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, them);
        if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
            rc.empower(actionRadius);
            return;
        }
        Direction dir = Direction.values()[(int)(8*Math.random())];
        if(rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}
