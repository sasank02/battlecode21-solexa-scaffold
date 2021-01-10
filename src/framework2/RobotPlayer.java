package framework2;

import battlecode.common.*;

public class RobotPlayer {
    public static void run(RobotController theRC) throws GameActionException {
        /*
        Determine which ruleset to use based on the bot type.
         */
        switch (theRC.getType()) {
            case ENLIGHTENMENT_CENTER:
                BotEC.loop(theRC);
                break;
            case POLITICIAN:
                BotPolitician.loop(theRC);
                break;
            case SLANDERER:
                BotSlanderer.loop(theRC);
                break;
            case MUCKRAKER:
                BotMuckraker.loop(theRC);
                break;
        }
    }
}

