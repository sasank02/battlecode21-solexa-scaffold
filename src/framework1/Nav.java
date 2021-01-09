package framework1;

import battlecode.common.*;

interface NavSafetyPolicy {
    public boolean isSafeToMoveTo(MapLocation loc);
}

class SafetyPolicyAvoidAllUnits extends Bot implements NavSafetyPolicy {
    RobotInfo[] nearbyEnemies;
    public SafetyPolicyAvoidAllUnits(MapLocation[] enemyTowers, RobotInfo[] nearbyEnemies) {
        this.nearbyEnemies = nearbyEnemies;
    }

    public boolean isSafeToMoveTo(MapLocation loc) {
        for (RobotInfo enemy: nearbyEnemies) {
            // TODO: If you move into some radius then don't move there.
        }
    }
}

public class Nav {
    public static void bugNav(MapLocation dest) {

    }
}
