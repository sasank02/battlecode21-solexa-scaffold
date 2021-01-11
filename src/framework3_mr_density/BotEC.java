package framework3_mr_density;

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
        if (flag == 7) flag = -1;
        if (flag < 7) {
			if (rc.canBuildRobot(RobotType.MUCKRAKER, directions[flag + 1], 1)) {
				rc.buildRobot(RobotType.MUCKRAKER, directions[flag + 1], 1);
				++flag;
				rc.setFlag(flag);
				return;
			}

            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.MUCKRAKER, dir, 1)) {
                    rc.buildRobot(RobotType.MUCKRAKER, dir, 1);
                    ++flag;
                    rc.setFlag(flag);
                    break;
                }
            }
            return;
		}

        // TODO: General strat after that
    }
}
