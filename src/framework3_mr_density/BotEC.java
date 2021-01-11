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

        for(Integer id : childArr){
            if(rc.canGetFlag(id))
            {   int idx = rc.getFlag(id);
                //System.out.println("idx: "+  idx);
                // TODO: Modularize this code, create a Map one with static functions to read and write messages.
                if (Comm.getExtraInformationFromFlag(flag) == 2) {
                    MapLocation ecLoc = Comm.getLocationFromFlag(idx);
                    //System.out.println("Enlightenment Center At: " + ecLoc.x + ", " + ecLoc.y + "YAYAYAYAYAYAYAYAYAYAYAYA");
                    // TODO: what do we do with robots already used
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

        if(rc.getRoundNum() < 50){
            //spawn 1 of each (slanderer, muckracker, politician)

            //
        }

        // Send scouts out each direction to scout
        if (flag < 7) {
			if (rc.canBuildRobot(RobotType.MUCKRAKER, directions[flag + 1], 1)) {
				rc.buildRobot(RobotType.MUCKRAKER, directions[flag + 1], 1);
                //childArr.add(rc.senseRobotAtLocation(rc.adjacentLocation(directions[flag + 1])).getID());
                ++flag;
				rc.setFlag(flag);
				return;
			}

            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.MUCKRAKER, dir, 1)) {
                    rc.buildRobot(RobotType.MUCKRAKER, dir, 1);
                    childArr.add(rc.senseRobotAtLocation(rc.adjacentLocation(dir)).getID());
                    ++flag;
                    rc.setFlag(flag);
                    break;
                }
            }
            return;
		}


        if(flag >= 7 && flag < 42){
            if(flag%4 == 0){
                //Build Politician
                if(rc.canBuildRobot(RobotType.POLITICIAN, directions[flag%7+1], 25)){
                    rc.buildRobot(RobotType.POLITICIAN, directions[flag%7+1], 25);
                    ++flag;
                    rc.setFlag(flag);
                    return;
                }
            }
            else{
                //Build Slanderer
                if(rc.canBuildRobot(RobotType.SLANDERER, directions[flag%7+1], 21)){
                    rc.buildRobot(RobotType.SLANDERER, directions[flag%7+1], 21);
                    ++flag;
                    rc.setFlag(flag);
                    return;
                }
            }
        }

        // Return to just maintaining a ratio between muckrakers / slanderers / polis


        // TODO: General strat after that
        // If something off cooldown, then you want to send to that EC.
    }

}
