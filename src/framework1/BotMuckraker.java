package framework1;

import battlecode.common.*;

public class BotMuckraker extends Bot {
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
        for (RobotInfo robot : rc.senseNearbyRobots(actionRadius, them)) {
            if (robot.type.canBeExposed()) {
                // It's a slanderer... go get them!
                if (rc.canExpose(robot.location)) {
                    rc.expose(robot.location);
                    return;
                }
            }
        }

        Direction dir = Direction.values()[(int)(8*Math.random())];
        if(rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}
