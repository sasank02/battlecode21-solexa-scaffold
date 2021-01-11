package framework3_mr_density;

import battlecode.common.*;

public class BotEC extends Bot {
    static int trigger = 0;
    static MapLocation[] ECs = new MapLocation[12];
    static int[] lastSent = new int[12];
    static int nextSpace = 0;

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
    	// Update information
        here = rc.getLocation();
        flag = 0;

        int sensorRadius = rc.getType().sensorRadiusSquared;

        // Check for new rally locations
        for (RobotInfo ally : rc.senseNearbyRobots(sensorRadius, us)) {
            int allyFlag = rc.getFlag(ally.ID);
            if (Comm.getExtraInformationFromFlag(allyFlag) == 2) {
                MapLocation ECLocation = Comm.getLocationFromFlag(allyFlag);
                boolean alreadyFound = false;
                for (int i = 0; i < nextSpace; ++i) {
                    if (ECs[i].equals(ECLocation)) {
                        alreadyFound = true;
                        break;
                    }
                }

                if (!alreadyFound) {
                    ECs[nextSpace] = ECLocation;
                    ++nextSpace;
                }
                // TODO: If new EC, add and spawn, else check if you want to send big boy
            }
        }

        for (int i = 0; i < nextSpace; ++i) {
            System.out.println(ECs[i]);
        }
        
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

        // TODO: Set flag after to rally location too.
        // Return to just maintaining a ratio between muckrakers / slanderers / polis


        // TODO: General strat after that
        // If something off cooldown, then you want to send to that EC.
    }

}
