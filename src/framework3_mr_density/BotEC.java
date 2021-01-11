package framework3_mr_density;

import battlecode.common.*;

import java.util.*;

public class BotEC extends Bot {

    static ArrayList<Integer> childArr = new ArrayList<Integer>();
    static Map<MapLocation, Integer> uniqueEnemyEC = new HashMap<MapLocation, Integer>();
    static Integer[] childArray = new Integer[10000];
    static int numChildren = 0;

    static MapLocation[] uniqueEnemyECLoc = new MapLocation[3];
    static Integer[] enemyECCooldowns = new Integer[3];
    static int numEC = 0;

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

        //MARK: -HEAVY BYTECODE
        /*for(Integer id : childArr){
            if(rc.canGetFlag(id))
            {   int idx = rc.getFlag(id);
                if (idx / 128 / 128 == 2) {
                    MapLocation ecLoc = getLocationFromFlag(idx);
                    System.out.println("Enlightenment Center At: " + ecLoc.x + ", " + ecLoc.y);
                    if(!uniqueEnemyEC.containsKey(ecLoc)) uniqueEnemyEC.put(ecLoc, 0);

                }
            }
        }*/

        //MARK: -LOW BYTECODE
        for(int i = 0; i < numChildren; i++){
            int id = childArray[i];
            if(rc.canGetFlag(id))
            {   int idx = rc.getFlag(id);
                if (idx / 128 / 128 == 2) {
                    MapLocation ecLoc = getLocationFromFlag(idx);
                    System.out.println("Enlightenment Center At: " + ecLoc.x + ", " + ecLoc.y);
                    if(!uniqueEnemyEC.containsKey(ecLoc)) uniqueEnemyEC.put(ecLoc, 0);
                    if((indexOf(uniqueEnemyECLoc, ecLoc) == -1)){
                        uniqueEnemyECLoc[numEC] = ecLoc;
                        enemyECCooldowns[numEC] = 0;
                        numEC++;
                    }
                }
            }
        }

        //HEAVY BYTECODE
        /*for(Map.Entry<MapLocation, Integer> entry : uniqueEnemyEC.entrySet()){
            if(entry.getValue() % 20 == 0){
                if (rc.canBuildRobot(RobotType.POLITICIAN, Direction.NORTH, 20)){
                    rc.buildRobot(RobotType.POLITICIAN, Direction.NORTH, 20);
                    //TODO: ADD POLITICAN IN SETS NOT INDIVIDUALS
                    System.out.println("politician added");
                }
            } uniqueEnemyEC.put(entry.getKey(), entry.getValue() + 1);
        }*/

        for(int i = 0; i < 3; i++){
            if(enemyECCooldowns[i] % 20 == 0){
                if(rc.canBuildRobot(RobotType.POLITICIAN, Direction.NORTH, 20)){
                    rc.buildRobot(RobotType.POLITICIAN, Direction.NORTH, 20);
                    System.out.println("poliAdded");
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
        // TODO: Broken, they don't move at the beginning for unknown reasons. (bug)
        if (flag == 7) flag = -1;
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
                    childArr.add(rc.senseRobotAtLocation(rc.adjacentLocation(dir)).getID());
                    ++flag;
                    rc.setFlag(flag);
                    break;
                }
            }
            return;
		}



        // TODO: General strat after that
    }

    static int indexOf(MapLocation[] someArray, MapLocation myElement){
        int index = -1;
        for(int i = 0; i < someArray.length; i++){
            if(someArray[i] == myElement) index = i;
        }
        return index;
    }

}
