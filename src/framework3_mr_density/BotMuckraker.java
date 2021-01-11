package framework3_mr_density;

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

		// "bubble" of influence
		int density[] = new int[8];
		double spreadDensity[] = new double[8];
		for (RobotInfo ally : nearbyAllies) {
			Direction dirTo = here.directionTo(ally.location);
			++density[Nav.numRightRotations(Direction.NORTH, dir)];
		}

		for (int i = 0; i < 8; ++i) {
			for (int j = 0; j < 8; ++j) {
				int naiveDiff = Math.abs(j - i);
				int diff = Math.min(naiveDiff, 8 - naiveDiff);
				switch (diff) {
					case 0:
						spreadDensity[j] += (1.0d * density[i]);
						break;
					case 1:
						spreadDensity[j] += (0.8d * density[i]);
						break;
					case 2:
						spreadDensity[j] += (0.6d * density[i]);
						break;
					case 3:
						spreadDensity[j] += (0.4d * density[i]);
						break;
					case 4:						
						spreadDensity[j] += (0.2d * density[i]);
						break;
				}
			}
		}

		// Never try to walk directly at border
		for (Direction dir : directions) {
			if (!rc.onTheMap(here.add(dir))) {
				spreadDensity[Nav.numRightRotations(Direction.NORTH, dir)] += 100000000;
			}
		}

		double minDensity = spreadDensity[Nav.numRightRotations(Direction.NORTH, dir)];
		Direction chosenDir = dir;

		for (Direction idir : directions) {
			double dirDensity = spreadDensity[Nav.numRightRotations(Direction.NORTH, idir)];
			System.out.println(idir + ": " + dirDensity);
			if (dirDensity < minDensity) {
				minDensity = dirDensity;
				chosenDir = idir;
			}
		}
		dir = chosenDir;
		
		// TODO: Some way to report back to EC if you find an enemy / neutral EC
		// Move in direction if no slanderers near
		// TODO: Fix, they don't move at the very start for unknown reasons and also don't adjust direction. (bug)
		Nav.moveDirection(dir, navPolicy);
    }
}
