package wumpus2.mstt2.languageprocessing;

import wumpus2.environment.wumpusworld.WumpusAction;
import wumpus2.environment.wumpusworld.WumpusPercept;

public interface NavigatorSpeech {
	String tellAction(WumpusAction action);

	WumpusPercept recognize(String speech);
}
