package framework2;

import battlecode.common.*;

public class BotMuckraker extends Bot {
	public static Direction dir;
	public static MapLocation target;
	public static int targetDistance = 1000;
	public static int bounceBack = 0;

	/**
	 * Central decision loop for Muckrakers
	 */
    public static void loop(RobotController theRC) throws GameActionException {
        Bot.init(theRC);

        // Get directions from EC
		for (RobotInfo robot : rc.senseNearbyRobots(2, us)) {
			if (robot.type == RobotType.ENLIGHTENMENT_CENTER) {
				dir = directions[rc.getFlag(robot.ID)];
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
    	// Update bot location
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
		
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(sensorRadius, us);
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

		// Chase slanderer if any found.
		if (target != null) {
			Nav.goTo(target, navPolicy);
			return;
		}

		// If you hit a border, choose a new random direction to scout
		if (!rc.onTheMap(here.add(dir))) {
			int times = (int)(Math.random() * 6) + 1;
			for (int i = 0; i < times; ++i) 
				dir = dir.rotateLeft();
			Nav.moveDirection(dir, navPolicy);
			return;
		}
		
		// "bubble" of influence
		boolean blocked[] = new boolean[8];
		boolean bounce = false;
		boolean clogged = true;
		for (RobotInfo ally : nearbyAllies) {
			Direction dirTo = here.directionTo(ally.location);
			if (dirTo == dir) bounce = true;
			blocked[Nav.numRightRotations(Direction.NORTH, dirTo)] = true;
		}

		for (Direction idir : directions) {
			if (!rc.onTheMap(here.add(idir))) {
				blocked[Nav.numRightRotations(Direction.NORTH, idir)] = true;
			}
		}
		
		// TODO: Congregating in corners, how do we want to not do that.
		// Check if we're blocked on all sides
		for (int i = 0; i < 8; ++i) clogged &= blocked[i];
		// TODO: Just because there aren't bots to one direction doesn't mean that you can't be clogged and need to force out, might be on border

		// Bounce the opposite way then
		// Make better bounces like slight bounces instead of full on swings
		Direction newDir = dir;

		if (clogged) {
			// dir = dir.opposite();
			newDir = Direction.values()[(int)(8 * Math.random())];
		} else if (bounce) {
			Direction leftDir = dir.opposite();
			Direction rightDir = dir.opposite();

			if (!blocked[Nav.numRightRotations(Direction.NORTH, leftDir)]) {
				newDir = leftDir;
				return;
			} else if (!blocked[Nav.numRightRotations(Direction.NORTH, rightDir)]) {
				newDir = rightDir;
				return;
			}
			leftDir = leftDir.rotateLeft();
			rightDir = rightDir.rotateRight();
		}
		
		if (newDir == dir.opposite()) {
			++bounceBack;
		}

		// Try not to get too many on the same bounce pathway

		if (bounceBack == 4) {
			if ((int)(2 * Math.random()) == 1) {
				newDir = newDir.rotateRight();
			} else newDir = newDir.rotateLeft();
			bounceBack = 0;
		}

		dir = newDir;
		// TODO: Some way to report back to EC if you find an enemy / neutral EC
		for(RobotInfo robot : rc.senseNearbyRobots(sensorRadius, them)){
			if(robot.type == RobotType.ENLIGHTENMENT_CENTER){
				MapLocation loc = robot.getLocation();
				sendLocation(loc, 2);
				System.out.println("EC AT: " + loc.x + ", " + loc.y);
			}
		}


		// Move in direction if no slanderers near
		// TODO: Fix, they don't move at the very start for unknown reasons and also don't adjust direction. (bug)
		Nav.moveDirection(dir, navPolicy);
    }
}
