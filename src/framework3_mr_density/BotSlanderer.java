package framework3_mr_density;

import battlecode.common.*;

public class BotSlanderer extends Bot {
	// Set home EC and stay within that radius
    static Direction dir;
    static MapLocation destination = null;
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

    /**
     * random movement, stupid.
     * TODO: One idea is to keep them within EC range to protect them, also started work on Nav class to run away from all unites sensored to impl here.
     * @throws GameActionException
     */
    public static void turn() throws GameActionException {
         dir = Direction.values()[(int)(8*Math.random())];

        for (Direction idir : directions) {
            if (!rc.onTheMap(here.add(idir))) {
                //WALL FOUND
                System.out.println("Wall Found");
                Comm.sendLocation(4);

            }
        }

        int sensorRadius = rc.getType().sensorRadiusSquared;
        for (RobotInfo robot : rc.senseNearbyRobots(sensorRadius, us)){
            int id = robot.getID();
            if(rc.canGetFlag(id)){
                int flag = rc.getFlag(id);
                if(Comm.getExtraInformationFromFlag(flag) == 4 || Comm.getExtraInformationFromFlag(flag) == 5 ){
                    //found a direction to move
                    Comm.sendLocation(5);
                    destination = Comm.getLocationFromFlag(flag);
                    System.out.println("Destination: " + destination);
                }
            }
        }

        NavPolicy navPolicy = new PolicyAvoidFriendlyMuckrakers(rc.senseNearbyRobots(sensorRadius, us));

        if(destination != null){
            Nav.goTo( destination, navPolicy);
        }
        else{
            Direction dir = Direction.values()[(int)(8*Math.random())];
             if(rc.canMove(dir)) {
              rc.move(dir);
            }
        }

    }
}
