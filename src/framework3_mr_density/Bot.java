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

    static void sendLocation() throws GameActionException{
        MapLocation location = rc.getLocation();
        int x = location.x, y = location.y;
        int encodedLocation = (x % 128) * 128 + (y%128);
        if(rc.canSetFlag(encodedLocation)){
            rc.setFlag(encodedLocation);
        }
    }

    static void sendLocation(int extraInformation) throws GameActionException{
        MapLocation location = rc.getLocation();
        int x = location.x, y = location.y;
        int encodedLocation = (x % 128) * 128 + (y%128) + extraInformation * 128 * 128;
        if(rc.canSetFlag(encodedLocation)) rc.setFlag(encodedLocation);
    }

    /* ENCODED INFO AS SUCH
         2^23     2^14      2^7       2^0
         [         |         |        ]
             10         7        7
             extra      x        y
    */

    static MapLocation getLocationFromFlag(int flag){
        int y = flag % 128;
        int x = (flag / 128) % 128;
        int extraInformation = flag / 128 / 128;

        MapLocation currentLocation = rc.getLocation();
        int offsetX128 = currentLocation.x /128;
        int offsetY128 = currentLocation.y/128;
        MapLocation actualLocation = new MapLocation(offsetX128 * 128 + x, offsetY128 *128 + y);

        MapLocation alternative = actualLocation.translate(-128,0);
        if(rc.getLocation().distanceSquaredTo(alternative) < rc.getLocation().distanceSquaredTo(actualLocation)) actualLocation = alternative;

        alternative = actualLocation.translate(-128,0);
        if(rc.getLocation().distanceSquaredTo(alternative) < rc.getLocation().distanceSquaredTo(actualLocation)) actualLocation = alternative;

        alternative = actualLocation.translate(-128,0);
        if(rc.getLocation().distanceSquaredTo(alternative) < rc.getLocation().distanceSquaredTo(actualLocation)) actualLocation = alternative;

        alternative = actualLocation.translate(-128,0);
        if(rc.getLocation().distanceSquaredTo(alternative) < rc.getLocation().distanceSquaredTo(actualLocation)) actualLocation = alternative;

        return actualLocation;
    }
}
