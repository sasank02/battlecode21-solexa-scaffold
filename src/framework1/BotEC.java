package framework1;

import battlecode.common.*;

public class BotEC extends Bot {
	public static int tslS = 0;
	public static int tslM = 0;
	
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
        boolean makeSlanderers = false;
        boolean makePoliticians = false;
        boolean makeMuckrakers = false;

        int influence = rc.getInfluence();

        if (rc.getRoundNum() >= 250) makeSlanderers = true;
        if (influence >= 20) makePoliticians = true;
        makeMuckrakers = true;

        int slanderers = 0;
        int politicians = 0;
        int muckrakers = 0;

        for (RobotInfo bot : rc.senseNearbyRobots(rc.getType().actionRadiusSquared, us)) {
            if (bot.getType() == RobotType.MUCKRAKER) ++muckrakers;
            if (bot.getType() == RobotType.SLANDERER) ++slanderers;
            if (bot.getType() == RobotType.POLITICIAN) ++politicians;
        }

        // TODO: Determine healthy num, 10 to even use politician

        // TODO: Determine when to make each type or robot
		
		if (rc.getRoundNum() >= 2000) {
			for (Direction dir : directions) {
				int infMin = 20, infMax = (int)(0.75d * influence);
				int infToUse = (int)(Math.random() * (infMax - infMin)) + infMin;
                if (rc.canBuildRobot(RobotType.POLITICIAN, dir, infToUse)) {
                    rc.buildRobot(RobotType.POLITICIAN, dir, infToUse);
                    break;
                }
            }
		} else if (makeSlanderers && tslS >= 14) {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.SLANDERER, dir, Math.min(949, influence))) {
                    rc.buildRobot(RobotType.SLANDERER, dir, Math.min(949, influence));
					tslS = 0;
					break;
                }
            }
            tslM++;
            // TODO: Make influence req for muckrackers increase with turn rounds
        } else if (makeMuckrakers && tslM >= 4 && influence < 200) {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.MUCKRAKER, dir, 1)) {
                    rc.buildRobot(RobotType.MUCKRAKER, dir, 1);
                    tslM = 0;
                    break;
                }
            }
            ++tslS;
        } else if (makePoliticians) {
            for (Direction dir : directions) {
				int infMin = 20, infMax = (int)(0.75d * influence);
				int infToUse = (int)(Math.random() * (infMax - infMin)) + infMin;
                if (rc.canBuildRobot(RobotType.POLITICIAN, dir, infToUse)) {
                    rc.buildRobot(RobotType.POLITICIAN, dir, infToUse);
                    break;
                }
            }
            ++tslS;
            ++tslM;
        }
    }
}
