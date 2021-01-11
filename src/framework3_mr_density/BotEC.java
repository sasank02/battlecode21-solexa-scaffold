package framework3_mr_density;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.HashMap;

public class BotEC extends Bot {

    static ArrayList<Integer> childArr = new ArrayList<Integer>();
    static int trigger = 0;
    static int count;
    public static void loop(RobotController theRC) throws GameActionException {
        Bot.init(theRC);
        flag = -1;
        count = -1;
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
        //System.out.println(".");
        
        for(Integer id : childArr){
            if(rc.canGetFlag(id))
            {   int idx = rc.getFlag(id);
                //System.out.println("idx: "+  idx);
                // TODO: Modularize this code, create a Map one with static functions to read and write messages.
                if (Comm.getExtraInformationFromFlag(flag) == 2) {
                    MapLocation ecLoc = Comm.getLocationFromFlag(idx);
                    Comm.sendLocation(1);
                    //System.out.println("Enlightenment Center At: " + ecLoc.x + ", " + ecLoc.y + "YAYAYAYAYAYAYAYAYAYAYAYA");
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
        //if (count == 7) count = -1;
        if (count < 7) {
			if (rc.canBuildRobot(RobotType.MUCKRAKER, directions[count + 1], 1)) {
				rc.buildRobot(RobotType.MUCKRAKER, directions[flag + 1], 1);
                childArr.add(rc.senseRobotAtLocation(rc.adjacentLocation(directions[flag + 1])).getID());
                ++count;
                ++flag;
				rc.setFlag(flag);
				return;
			}

            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.MUCKRAKER, dir, 1)) {
                    rc.buildRobot(RobotType.MUCKRAKER, dir, 1);
                    ++count;
                    ++flag;
                    rc.setFlag(flag);
                    break;
                }
            }
            return;
		}

        if(count >= 7 && count < 42){
            System.out.println("");
            if(count%4 ==0){
                //poli
                System.out.println("Politician");
                for (Direction dir : directions) {
                    if (rc.canBuildRobot(RobotType.POLITICIAN, dir, 40)) {
                        rc.buildRobot(RobotType.POLITICIAN, dir, 40);
                        ++count;
                        rc.setFlag(0);
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
                        ++count;
                        rc.setFlag(flag);
                        break;
                    }
                }
            }
        }

        // Return to just maintaining a ratio between muckrakers / slanderers / polis
        if (count >= 42){
            if(count%7 == 0 || count%7-1 == 0) {
                System.out.println("Slanderer");
                for (Direction dir : directions) {
                    if (rc.canBuildRobot(RobotType.SLANDERER, dir, rc.getInfluence())) {
                        rc.buildRobot(RobotType.SLANDERER, dir, rc.getInfluence());
                        ++count;
                        rc.setFlag(flag);
                        break;
                    }
                }
            }
            else if(count%7-2 == 0){
                System.out.println("Muckracker");
                for (Direction dir : directions) {
                    if (rc.canBuildRobot(RobotType.MUCKRAKER, dir, 1)) {
                        rc.buildRobot(RobotType.MUCKRAKER, dir, 1);
                        ++count;
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
                        ++count;
                        rc.setFlag(0);
                        break;
                    }
                }
            }
        }

        // TODO: General strat after that
        // If something off cooldown, then you want to send to that EC.
    }

}
