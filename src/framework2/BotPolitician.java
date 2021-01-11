package framework2;

import battlecode.common.*;

public class BotPolitician extends Bot {
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
     * General politician, just attacks if any enemies in range, random movement not yet integrated into Nav.
     * Very stupid.
     * @throws GameActionException
     */
    public static void turn() throws GameActionException {
        here = rc.getLocation();
        int actionRadius = rc.getType().actionRadiusSquared;
        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, them);
        int total = rc.senseNearbyRobots(actionRadius).length;
        int friendlies = rc.senseNearbyRobots(actionRadius, us).length;
        if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
			System.out.println(total + " " + friendlies + " " + attackable.length);
            rc.empower(actionRadius);
            return;
        }
        
        if(themECLocs.size() > 0){
            MapLocation  destinationEC = findClosestEnemyEC();
            Nav.goTo(destinationEC,null);
            System.out.println("MOVING");
        }

        Direction dir = Direction.values()[(int)(8*Math.random())];
        if(rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    private static MapLocation findClosestEnemyEC(){
        MapLocation shortest = null;
        int shortDist = Integer.MAX_VALUE;
        for(Integer loc : themECLocs){
             MapLocation location = getLocationFromFlag(loc);
             int dist = location.distanceSquaredTo(location);
             if(dist < shortDist){
                 shortDist = dist;
                 shortest = location;
             }
        }
        return shortest;
    }
}
