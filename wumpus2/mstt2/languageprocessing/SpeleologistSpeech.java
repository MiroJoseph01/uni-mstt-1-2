package wumpus2.mstt2.languageprocessing;

import wumpus2.environment.wumpusworld.WumpusAction;
import wumpus2.environment.wumpusworld.WumpusPercept;

public interface SpeleologistSpeech {
	WumpusAction recognize(String speech);

	String tellPercept(WumpusPercept percept);
}
