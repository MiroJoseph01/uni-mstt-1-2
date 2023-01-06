package wumpus2.mstt2.languageprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import wumpus2.environment.wumpusworld.WumpusAction;
import wumpus2.environment.wumpusworld.WumpusPercept;

public class BaseSpeleologistSpeech implements SpeleologistSpeech {
	private final Random randomGenerator;
	private final Map<List<String>, WumpusAction> actionKeyWords;

	public BaseSpeleologistSpeech() {
		randomGenerator = new Random();

		actionKeyWords = new HashMap<>();
		actionKeyWords.put(Phrases.ActionKeyWords.turnLeft, WumpusAction.TURN_LEFT);
		actionKeyWords.put(Phrases.ActionKeyWords.turnRight, WumpusAction.TURN_RIGHT);
		actionKeyWords.put(Phrases.ActionKeyWords.goForward, WumpusAction.FORWARD);
		actionKeyWords.put(Phrases.ActionKeyWords.shoot, WumpusAction.SHOOT);
		actionKeyWords.put(Phrases.ActionKeyWords.grab, WumpusAction.GRAB);
		actionKeyWords.put(Phrases.ActionKeyWords.climb, WumpusAction.CLIMB);
	}

	@Override
	public WumpusAction recognize(String speech) {
		String finalSpeech = speech.toLowerCase();
		return actionKeyWords.keySet().stream()
				.filter(keyWords -> keyWords.stream().anyMatch(finalSpeech::contains))
				.findFirst()
				.map(actionKeyWords::get)
				.orElseThrow();
	}

	@Override
	public String tellPercept(WumpusPercept percept) {
		List<String> feelings = new ArrayList<>();

		if(percept.isBreeze()) {
			feelings.add(getSentence(Phrases.SpeleologistPhrases.pitNear));
		}
		if(percept.isStench()) {
			feelings.add(getSentence(Phrases.SpeleologistPhrases.wumpusNear));
		}
		if(percept.isGlitter()) {
			feelings.add(getSentence(Phrases.SpeleologistPhrases.goldNear));
		}
		if(percept.isBump()) {
			feelings.add(getSentence(Phrases.SpeleologistPhrases.wallNear));
		}
		if(percept.isScream()) {
			feelings.add(getSentence(Phrases.SpeleologistPhrases.wumpusKilledNear));
		}

		if(feelings.isEmpty()) {
			feelings.add(getSentence(Phrases.SpeleologistPhrases.nothing));
		}

		return String.join(". ", feelings);
	}

	private String getSentence(List<String> sentences) {
		int index = randomGenerator.nextInt(sentences.size());
		return sentences.get(index);
	}
}

