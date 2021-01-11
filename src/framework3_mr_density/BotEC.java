package framework3_mr_density;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.HashMap;

public class BotEC extends Bot {

    static ArrayList<Integer> childArr = new ArrayList<Integer>();
    static int trigger = 0;

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
            }
        }

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
        //if (flag == 7) flag = -1;
        if (flag < 7) {
			if (rc.canBuildRobot(RobotType.MUCKRAKER, directions[flag + 1], 1)) {
				rc.buildRobot(RobotType.MUCKRAKER, directions[flag + 1], 1);
                childArr.add(rc.senseRobotAtLocation(rc.adjacentLocation(directions[flag + 1])).getID());
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

        if(flag >= 7 && flag < 42){
            System.out.println("");
            if(flag%4 ==0){
                //poli
                System.out.println("Politician");
                for (Direction dir : directions) {
                    if (rc.canBuildRobot(RobotType.POLITICIAN, dir, 25)) {
                        rc.buildRobot(RobotType.POLITICIAN, dir, 25);
                        ++flag;
                        rc.setFlag(flag);
                        break;
                    }
                }

            }
            else{
                //c
                System.out.println("Sladnerer");

                for (Direction dir : directions) {
                    if (rc.canBuildRobot(RobotType.SLANDERER, dir, 21)) {
                        rc.buildRobot(RobotType.SLANDERER, dir, 21);
                        ++flag;
                        rc.setFlag(flag);
                        break;
                    }
                }
            }
        }

        // Return to just maintaining a ratio between muckrakers / slanderers / polis
        if (flag >= 42){
            if(flag%7 == 0 || flag%7-1 == 0) {
                System.out.println("Slanderer");
                for (Direction dir : directions) {
                    if (rc.canBuildRobot(RobotType.SLANDERER, dir, rc.getInfluence())) {
                        rc.buildRobot(RobotType.SLANDERER, dir, rc.getInfluence());
                        ++flag;
                        rc.setFlag(flag);
                        break;
                    }
                }
            }
            else if(flag%7-2 == 0){
                System.out.println("Muckracker");
                for (Direction dir : directions) {
                    if (rc.canBuildRobot(RobotType.MUCKRAKER, dir, 1)) {
                        rc.buildRobot(RobotType.MUCKRAKER, dir, 1);
                        ++flag;
                        rc.setFlag(flag);
                        break;
                    }
                }
            }
            else{
                //politician
                System.out.println("Politician");
                for (Direction dir : directions) {
                    if (rc.canBuildRobot(RobotType.POLITICIAN, dir, 25)) {
                        rc.buildRobot(RobotType.POLITICIAN, dir, 25);
                        ++flag;
                        rc.setFlag(flag);
                        break;
                    }
                }
            }
        }

        // TODO: General strat after that
        // If something off cooldown, then you want to send to that EC.
    }

}
