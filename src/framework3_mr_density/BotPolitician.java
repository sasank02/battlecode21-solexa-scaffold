package framework3_mr_density;

import battlecode.common.*;

public class BotPolitician extends Bot {
    public static Direction dir = Direction.values()[(int)(8*Math.random())];
    public static int type;
    public static int influence;
    public static MapLocation target;

    public static void loop(RobotController theRC) throws GameActionException {
        Bot.init(theRC);
        
        for (RobotInfo robot : rc.senseNearbyRobots(2, us)) {
            if (robot.type.equals(RobotType.ENLIGHTENMENT_CENTER)) {
                int ecFlag = rc.getFlag(robot.ID);
                if (Comm.getExtraInformationFromFlag(ecFlag) == 0) type = 0;
                else {
                    type = 1;
                    target = Comm.getLocationFromFlag(ecFlag);
                }
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

    /**
     * General politician, just attacks if any enemies in range, random movement not yet integrated into Nav.
     * Very stupid.
     * @throws GameActionException
     */
    public static void turn() throws GameActionException {
        // Update bot location
        here = rc.getLocation();
        influence = rc.getInfluence();
        // TODO: Maybe set flag to the ID of the one you're chasing so you don't all stop chasing someone.

        int actionRadius = rc.getType().actionRadiusSquared;
        int sensorRadius = rc.getType().sensorRadiusSquared;

        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(sensorRadius, us);
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(sensorRadius, them);
        NavPolicy navPolicy = new PolicyAvoidFriendlyMuckrakers(rc.senseNearbyRobots(sensorRadius, us));

        // TODO: Adjust for influence boost.
        if (type == 0) {
            System.out.println("TYPE 0 POLITICANS");
            // Detonate if you see 5 chasers or 2 or muckraker and your ally slanderer
            int chasers = 0, slanderers = 0;
            for (RobotInfo ally : nearbyAllies) {
                // TODO: Determine what we want chasers to be
                if (ally.type.equals(RobotType.POLITICIAN) && rc.getFlag(ally.getID()) == 0) ++chasers;
                if (ally.type.equals(RobotType.SLANDERER)) ++slanderers;
            }
            
            System.out.println("Chasers: " + chasers);
            // Check if 
            int takenOut = 0, bestRS = -1;
            for (int rs = 1; rs <= 9; ++rs) {
                int localTakenOut = 0;
                RobotInfo[] localAllies = rc.senseNearbyRobots(rs, us);
                RobotInfo[] localEnemies = rc.senseNearbyRobots(rs, them);
                if (nearbyAllies.length + localEnemies.length == 0) continue;
                int infSpread = (influence - 10) / (nearbyAllies.length + localEnemies.length);
                for (RobotInfo enemy : localEnemies) {
                    if (enemy.conviction < infSpread) ++localTakenOut;
                }
                if (localTakenOut > takenOut) {
                    takenOut = localTakenOut;
                    bestRS = rs;
                }
            }

            System.out.println("BEST TAKE OUT: " + takenOut);
            System.out.println("SET RADIUS TO:" + bestRS);
            if ((slanderers >= 1 || chasers >= 4) && takenOut >= 1) {
                if (rc.canEmpower(bestRS)) {
                    rc.empower(bestRS);
                }
            } else if (takenOut >= 2) {
                if (rc.canEmpower(bestRS)) {
                    rc.empower(bestRS);
                }
            }

            // "bubble" of influence
            int density[] = new int[8];
            double spreadDensity[] = new double[8];
            if (nearbyEnemies.length > 0) {
                for (RobotInfo enemy : nearbyEnemies) {
                    Direction dirTo = here.directionTo(enemy.location);
                    int distTo = here.distanceSquaredTo(enemy.location);
                    density[Nav.numRightRotations(Direction.NORTH, dirTo)] += (41 - distTo);
                }

                // Never try to walk directly at border
                for (Direction idir : directions) {
                    if (!rc.onTheMap(here.add(idir))) {
                        density[Nav.numRightRotations(Direction.NORTH, idir)] = -100000;
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


                double maxDensity = spreadDensity[Nav.numRightRotations(Direction.NORTH, dir)];
                Direction chosenDir = dir;

                for (Direction idir : directions) {
                    double dirDensity = spreadDensity[Nav.numRightRotations(Direction.NORTH, idir)];
                    //System.out.println(idir + ": " + dirDensity);
                    if (dirDensity > maxDensity) {
                        maxDensity = dirDensity;
                        chosenDir = idir;
                    }
                }
                dir = chosenDir;
            } else {
                for (RobotInfo ally : nearbyAllies) {
                    Direction dirTo = here.directionTo(ally.location);
                    int distTo = here.distanceSquaredTo(ally.location);
                    density[Nav.numRightRotations(Direction.NORTH, dirTo)] += (41 - distTo);
                }
                // Never try to walk directly at border
                for (Direction idir : directions) {
                    if (!rc.onTheMap(here.add(idir))) {
                        density[Nav.numRightRotations(Direction.NORTH, idir)] += 100000;
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
            }
            Nav.moveDirection(dir, navPolicy);
        } else {
            int distanceFromEC = here.distanceSquaredTo(target);
            if (distanceFromEC == 1) {
                RobotInfo[] localNeutral = rc.senseNearbyRobots(1, Team.NEUTRAL);
                RobotInfo[] localEnemies = rc.senseNearbyRobots(1, them);
                if (localNeutral.length + localEnemies.length == 1) {
                    if (rc.canEmpower(1)) {
                        rc.empower(1);
                    }
                }
            }

            Nav.goTo(target, navPolicy);
        }
    }
}
