package framework2;

import battlecode.common.*;

interface NavPolicy {
    boolean isSafeToMoveTo(Direction dir);
}

/**
 * Custom policy to spread out Muckrakers by ensuring they don't move towards each other.
 * TODO: Fix now that all muckraker logic is in muckraker :thinking_face:
 */
class PolicyAvoidFriendlyMuckrakers extends Bot implements NavPolicy {
	RobotInfo[] nearbyAllies;

	public PolicyAvoidFriendlyMuckrakers(RobotInfo[] nearbyAllies) {
		this.nearbyAllies = nearbyAllies;
	}

	public boolean isSafeToMoveTo(Direction dir) {
		// Keep muckrakers spread out
		for (RobotInfo ally : nearbyAllies) {
			if (ally.type == RobotType.MUCKRAKER) {
				// if (here.directionTo(ally.location) == dir) return false;
			}
		}

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
     * Number of right rotations to face different directions
     */
    public static int numRightRotations(Direction start, Direction end) {
        return (end.ordinal() - start.ordinal() + 8) % 8;
    }

    /**
     * Number of left rotations to face different directions
     */
    public static int numLeftRotations(Direction start, Direction end) {
        return (-end.ordinal() + start.ordinal() + 8) % 8;
    }

	/**
	 * Try to directly take the straight line to the destination.
	 * @return - True if you can take the direct line
	 * @throws GameActionException
	 */
	private static boolean tryMoveDirect() throws GameActionException {
		Direction toDest = here.directionTo(dest);

		if (canMove(toDest)) {
			move(toDest);
			return true;
		}

		Direction[] dirs = new Direction[2];
		dirs[0] = toDest.rotateLeft();
		dirs[1] = toDest.rotateRight();
		for (Direction dir : dirs) {
			if (canMove(dir)) {
				move(dir);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Move directly to the destination.
	 * @param theDest - Destination square you want to reach
	 * @param thePolicy - Policy to decide whether you can head in a direction
	 * @throws GameActionException
	 */
	public static void goTo(MapLocation theDest, NavPolicy thePolicy) throws GameActionException {
		dest = theDest;
		policy = thePolicy;

		if (here.equals(dest)) return;

		tryMoveDirect();
	}

    /**
     * Moves a bot in the general direction
     * @param theDir - Direction to move
     * @param thePolicy - Policy to determine whether or not you should move
     * @throws GameActionException
     */
	public static void moveDirection(Direction theDir, NavPolicy thePolicy) throws GameActionException {
		dest = here.add(theDir);

		goTo(dest, thePolicy);
	}
}
