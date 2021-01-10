package framework2;

import battlecode.common.*;

public class BotMuckraker extends Bot {
	public static Direction dir;
	public static MapLocation target;
	public static int targetDistance = 1000;

	/**
	 * Central decision loop for Muckrakers
	 */
    public static void loop(RobotController theRC) throws GameActionException {
        Bot.init(theRC);

        // Get directions from EC
		for (RobotInfo robot : rc.senseNearbyRobots(2, us)) {
			if (robot.type == RobotType.ENLIGHTENMENT_CENTER) {
				dir = directions[rc.getFlag(robot.ID)];
				System.out.println("Set DIR TO :" + dir + " from " + rc.getFlag(robot.ID));
				break;
			}
		}

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

    	System.out.println("Hi");
		here = rc.getLocation();
        int actionRadius = rc.getType().actionRadiusSquared;
        int sensorRadius = rc.getType().sensorRadiusSquared;
        // Destroy slanderer if in range.
        for (RobotInfo robot : rc.senseNearbyRobots(actionRadius, them)) {
            if (robot.type.canBeExposed()) {
                if (rc.canExpose(robot.location)) {
                    rc.expose(robot.location);
                   	return;
                }
            }
        }

		NavPolicy navPolicy = new PolicyAvoidFriendlyMuckrakers(rc.senseNearbyRobots(sensorRadius, us));

        // Find closest slanderer around to chase
		for (RobotInfo robot : rc.senseNearbyRobots(sensorRadius, them)) {
			//TODO: What is target distance????
			targetDistance = 1000;
			target = null;
			if (robot.type.canBeExposed()) {
				int distanceFrom = here.distanceSquaredTo(robot.location);
				if (distanceFrom < targetDistance) {
					target = robot.location;
				}
			}
		}

		// Chase slanderer if any found.
		if (target != null) {
			Nav.goTo(target, navPolicy);
			return;
		}

		// If you hit a border, choose a new random direction to scout
		if (!rc.canSenseLocation(here.add(dir))) {
			int times = (int)(Math.random() * 6) + 1;
			for (int i = 0; i < times; ++i) 
				dir.rotateLeft();
		}


		// TODO: Some way to report back to EC if you find an enemy / neutral EC
		for (RobotInfo robot : rc.senseNearbyRobots(sensorRadius, them)) {
			//if enlightenment center encountered
			if(robot.type.canBid()){
				int distanceFrom = here.distanceSquaredTo(robot.location);
				//FIXME: distance from
				if (distanceFrom < 25) {
					sendLocation(2);
				}
			}
		}

		// Move in direction if no slanderers near
		// TODO: Fix, they don't move at the very start for unknown reasons and also don't adjust direction. (bug)
		Nav.moveDirection(dir, navPolicy);
    }

    //public static void registerECTranslation(MapLocation loc){}
}
