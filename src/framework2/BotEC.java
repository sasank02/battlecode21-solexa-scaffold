package framework2;

import battlecode.common.*;

public class BotEC extends Bot {
    public static void loop(RobotController theRC) throws GameActionException {
        Bot.init(theRC);
        flag = -1;
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
     * Spawning setup
     */
    public static void turn() throws GameActionException {
		// Create slanderer first for eco
		here = rc.getLocation();
        if (rc.getRoundNum() == 1) {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.SLANDERER, dir, rc.getInfluence())) {
                    rc.buildRobot(RobotType.SLANDERER, dir, rc.getInfluence());
                    break;
                }
            }
            return;
        }


        // Send scouts out each direction to scout
        // TODO: Broken, they don't move at the beginning for unknown reasons. (bug)
        if (flag < 7) {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.MUCKRAKER, dir, 1)) {
                    rc.buildRobot(RobotType.MUCKRAKER, dir, 1);
                    ++flag;
                    rc.setFlag(flag);
                    break;
                }
            }

		}

        // TODO: General strat after that
    }
}
