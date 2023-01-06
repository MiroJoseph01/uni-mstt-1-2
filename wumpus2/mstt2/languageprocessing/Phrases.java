package wumpus2.mstt2.languageprocessing;

import java.util.List;

public final class Phrases {
	public static final class PerceptKeyWords {
		public static List<String> breeze = List.of("breez", "wind");
		public static List<String> stench = List.of("stench", "smell", "stinky");
		public static List<String> glitter = List.of("shiny", "glitter");
		public static List<String> bump = List.of("bump", "hit", "wall");
		public static List<String> scream = List.of("scream", "hear");

	}

	public static final class SpeleologistPhrases {
		public static List<String> pitNear = List.of("It is wind here", "There is a breeze", "It is breezy here");
		public static List<String> wumpusNear = List.of("I smell something", "Something stinky is here", "There is such a stench");
		public static List<String> goldNear = List.of("I see something shiny", "It is something shiny here", "It is a glitter");
		public static List<String> wallNear = List.of("I faced the wall", "It is bumping here", "I hit the wall");
		public static List<String> wumpusKilledNear = List.of("I heard something", "It is screaming here", "There is a terrible scream");
		public static List<String> nothing = List.of("All is clear now", "I see nothing now", "There is nothing left");
	}

	public static final class ActionKeyWords {
		public static List<String> turnLeft = List.of("left");
		public static List<String> turnRight = List.of("right");
		public static List<String> goForward = List.of("forward", "ahead", "straight");
		public static List<String> grab = List.of("grab");
		public static List<String> shoot = List.of("shoot");
		public static List<String> climb = List.of("climb");
	}

	public static final class NavigatorPhrases {
		public static List<String> goForward = List.of("Go forward", "Go straight", "Go ahead");
		public static List<String> turnLeft = List.of("Turn left", "Turn to the left");
		public static List<String> turnRight = List.of("Turn right", "Turn to the right");
		public static List<String> shoot = List.of("Shoot", "Shoot the wumpus");
		public static List<String> grab = List.of("Grab", "Grab the gold");
		public static List<String> climb = List.of("Climb", "Climb the ladder");
	}
}
