package framework2;

import battlecode.common.*;

interface NavPolicy {
    boolean isSafeToMoveTo(Direction dir);
}

/**
 * Custom policy to spread out Muckrakers by ensuring they don't move towards each other.
 */
class PolicyAvoidFriendlyMuckrakers extends Bot implements NavPolicy {
	RobotInfo[] nearbyAllies;

	public PolicyAvoidFriendlyMuckrakers(RobotInfo[] nearbyAllies) {
		this.nearbyAllies = nearbyAllies;
	}

	public boolean isSafeToMoveTo(Direction dir) {
		// Keep muckrakers spread out
		/*for (RobotInfo ally : nearbyAllies) {
			if (ally.type == RobotType.MUCKRAKER) {
			    System.out.println("DIRECTION TO ALLY: " + here.directionTo(ally.location));
				// if (here.directionTo(ally.location) == dir) return false;
			}
		}*/

		return true;
	}
}

/**
 * Custom policy which should make it so the bot will avoid moving in range of enemy units.
 */
class PolicyAvoidAllUnits extends Bot implements NavPolicy {
    RobotInfo[] nearbyEnemies;

    public PolicyAvoidAllUnits(RobotInfo[] nearbyEnemies) {
        this.nearbyEnemies = nearbyEnemies;
    }

    public boolean isSafeToMoveTo(Direction dir) {
		MapLocation loc = here.add(dir);
        for (RobotInfo enemy: nearbyEnemies) {
			switch (enemy.type) {
				case MUCKRAKER:
					if (loc.distanceSquaredTo(enemy.location) <= 20) return false;
					break;
				case POLITICIAN:
					if (loc.distanceSquaredTo(enemy.location) <= 9) return false;
					break;
			}
		}

        return true;
    }
}

public class Nav extends Bot {
	private static MapLocation dest;
    private static NavPolicy policy;

	private static boolean move(Direction dir) throws GameActionException {
        rc.move(dir);
        return true;
    }

    /**
     * Check if you can move in a direction
     * @param dir - Direction to check
     * @return - True if you can move in that direction and it's safe.
     */
    private static boolean canMove(Direction dir) {
        return rc.canMove(dir) && policy.isSafeToMoveTo(dir);
    }

    /**
     * TODO: Implement this with BugNAV 2
     * @param theDest - MapLocation where you want to go
     * @param thePolicy - Policy to follow for safe movement
     * @throws GameActionException - Catch errors
     */
	public static void goTo(MapLocation theDest, NavPolicy thePolicy) throws GameActionException {
		if (here.equals(dest)) return;
		
		dest = theDest;
		policy = thePolicy;

		// bugMove();
	}

    /**
     * Moves a bot in the general direction
     * @param theDir - Direction to move
     * @param thePolicy - Policy to determine whether or not you should move
     * @throws GameActionException
     */
	public static void moveDirection(Direction theDir, NavPolicy thePolicy) throws GameActionException {
		dest = null;
		policy = thePolicy;

        if (canMove(theDir)) {
            move(theDir);
            return;
        }

        Direction[] dirs = new Direction[2];
        dirs[0] = theDir.rotateLeft();
        dirs[1] = theDir.rotateRight();

        for (Direction dir : dirs) {
            if (canMove(dir)) {
                move(dir);
                return;
            }
        }
	}
}
