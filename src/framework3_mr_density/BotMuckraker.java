package framework3_mr_density;

import battlecode.common.*;

public class BotMuckraker extends Bot {
	public static Direction dir = Direction.values()[(int)(8*Math.random())];
	public static MapLocation target;
	public static int targetDistance = 1000;
	public static int bounceBack = 0;

	/**
	 * Central decision loop for Muckrakers
	 */
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

    public static void turn() throws GameActionException {
    	// Update bot location
		here = rc.getLocation();
		flag = 0;

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
			int distTo = here.distanceSquaredTo(ally.location);
			density[Nav.numRightRotations(Direction.NORTH, dirTo)] += (41 - distTo);
		}

		// Never try to walk directly at border
		for (Direction idir : directions) {
			if (!rc.onTheMap(here.add(idir))) {
				density[Nav.numRightRotations(Direction.NORTH, idir)] += 1000000;
			}
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

		double minDensity = spreadDensity[Nav.numRightRotations(Direction.NORTH, dir)];
		Direction chosenDir = dir;

		for (Direction idir : directions) {
			double dirDensity = spreadDensity[Nav.numRightRotations(Direction.NORTH, idir)];
			//System.out.println(idir + ": " + dirDensity);
			if (dirDensity < minDensity) {
				minDensity = dirDensity;
				chosenDir = idir;
			}
		}
		dir = chosenDir;
		
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(sensorRadius, them);



		boolean foundEC = false;
		// TODO: Some way to report back to EC if you find an enemy / neutral EC
		for(RobotInfo robot : nearbyEnemies){
			if(robot.type == RobotType.ENLIGHTENMENT_CENTER){
				MapLocation loc = robot.getLocation();
				Comm.sendLocation(loc, 2);
				foundEC = true;
				break;
			}
		}

		if (!foundEC) {
			for(RobotInfo robot : rc.senseNearbyRobots(sensorRadius, Team.NEUTRAL)){
				if(robot.type == RobotType.ENLIGHTENMENT_CENTER){
					MapLocation loc = robot.getLocation();
					Comm.sendLocation(loc, 2);
					foundEC = true;
					break;
				}
			}
		}

		if (!foundEC) {
			for(RobotInfo robot : nearbyAllies){
				int allyFlag = rc.getFlag(robot.ID);
				if (robot.getTeam() == us && Comm.getExtraInformationFromFlag(allyFlag) == 2) {
					flag = allyFlag;
					break;
				}
			}
			rc.setFlag(flag);
		}

		Nav.moveDirection(dir, navPolicy);
    }
}
