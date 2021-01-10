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
        	System.out.println(rc.getRoundNum());
            try {
            	System.out.println("TAKING TURN");
                turn();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Clock.yield();
        }
    }

    public static void turn() throws GameActionException {
    	// Update bot location
		here = rc.getLocation();

		System.out.println("Searching for slanderers to destroy");
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
		System.out.println("No slanderers in range to destroy");

		NavPolicy navPolicy = new PolicyAvoidFriendlyMuckrakers(rc.senseNearbyRobots(sensorRadius, us));


        // Find closest slanderer around to chase
		targetDistance = 1000;
		target = null;
		for (RobotInfo robot : rc.senseNearbyRobots(sensorRadius, them)) {
			if (robot.type.canBeExposed()) {
				int distanceFrom = here.distanceSquaredTo(robot.location);
				if (distanceFrom < targetDistance) {
					target = robot.location;
					targetDistance = distanceFrom;
				}
			}
		}
		System.out.println("Finished finding slanderer");

		// Chase slanderer if any found.
		if (target != null) {
			System.out.println("Chasing slanderer");
			Nav.goTo(target, navPolicy);
			return;
		}
		System.out.println("No chasing");

		// If you hit a border, choose a new random direction to scout
		if (!rc.onTheMap(here.add(dir))) {
			System.out.println("HIT BORDER");
			int times = (int)(Math.random() * 6) + 1;
			System.out.println("ROTATING " + times + " times");
			for (int i = 0; i < times; ++i) 
				dir = dir.rotateLeft();
			System.out.println("NEW DIRECTION: " + dir);
		}

		// TODO: Some way to report back to EC if you find an enemy / neutral EC
		// Move in direction if no slanderers near
		// TODO: Fix, they don't move at the very start for unknown reasons and also don't adjust direction. (bug)
		System.out.println(dir + " " + rc.canMove(dir));
		Nav.moveDirection(dir, navPolicy);
    }
}
