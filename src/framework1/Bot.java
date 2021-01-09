package framework1;

import battlecode.common.*;

public class Bot {
    public static RobotController rc;
    protected static Team us;
    protected static Team them;

    public static MapLocation here;

    protected static void init(RobotController theRC) throws GameActionException {
        rc = theRC;

        us = rc.getTeam();
        them = us.opponent();

        here = rc.getLocation();
    }
}
