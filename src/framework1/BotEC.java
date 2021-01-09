package framework1;

import battlecode.common.*;

public class BotEC extends Bot {


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
        boolean makeSlanderers = false;
        boolean makePoliticians = false;
        boolean makeMuckrakers = false;

        int influence = rc.getInfluence();

        if (rc.getRoundNum() >= 750) makeSlanderers = true;
        if (influence >= 50) makePoliticians = true;
        makeMuckrakers = true;

        int slanderers = 0;
        int politicians = 0;
        int muckrakers = 0;

        for (RobotInfo bot : rc.senseNearbyRobots(rc.getType().actionRadiusSquared, us)) {
            if (bot.getType() == RobotType.MUCKRAKER) ++muckrakers;
            if (bot.getType() == RobotType.SLANDERER) ++slanderers;
            if (bot.getType() == RobotType.POLITICIAN) ++politicians;
        }

        // TODO: Determine healthy num, 10 to even use politician

        // TODO: Determine when to make each type or robot

        boolean slandLast = false;
        if (makeSlanderers && slanderers == 0 && !slandLast) {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.SLANDERER, dir, Math.min(influence, 949))) {
                    rc.buildRobot(RobotType.SLANDERER, dir, Math.min(influence, 949));
                    slandLast = true;
                } else {
                    break;
                }
            }
        } else if (makeMuckrakers && influence >= 1 && muckrakers == 0) {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.MUCKRAKER, dir, 1)) {
                    rc.buildRobot(RobotType.MUCKRAKER, dir, 1);
                } else {
                    break;
                }
            }
            slandLast = false;
        } else if (makePoliticians) {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.POLITICIAN, dir, Math.max(50, (int)(0.6 * influence)))) {
                    rc.buildRobot(RobotType.POLITICIAN, dir, Math.max(50, (int)(0.6 * influence)));
                } else {
                    break;
                }
            }
            slandLast = false;
        }
    }
}
