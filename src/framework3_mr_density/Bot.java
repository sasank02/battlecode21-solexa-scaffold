package framework3_mr_density;

import battlecode.common.*;

/**
 * Generic Bot class with universal items for all robot types.
 */
public class Bot {
    public static RobotController rc;
    protected static Team us;
    protected static Team them;
	public static int flag;

    public static MapLocation here;

    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    protected static void init(RobotController theRC) throws GameActionException {
        rc = theRC;

        us = rc.getTeam();
        them = us.opponent();

        // Note that each bot has to continually update here each turn.
        here = rc.getLocation();
    }
}
