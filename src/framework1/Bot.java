package framework1;

import battlecode.common.*;

public class Bot {
    public static RobotController rc;
    protected static Team us;
    protected static Team them;

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

        here = rc.getLocation();
    }
}
