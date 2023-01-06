package wumpus2.mstt2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import wumpus2.environment.wumpusworld.WumpusPercept;

@AllArgsConstructor
@ToString
@Getter
@NoArgsConstructor
public class State {
	WumpusPercept percept;
	int tick;
}
