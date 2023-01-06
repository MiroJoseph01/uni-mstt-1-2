package wumpus2.mstt2;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import wumpus2.environment.wumpusworld.AgentPosition;
import wumpus2.environment.wumpusworld.EfficientHybridWumpusAgent;
import wumpus2.environment.wumpusworld.WumpusAction;
import wumpus2.environment.wumpusworld.WumpusPercept;
import wumpus2.mstt2.languageprocessing.NavigatorSpeech;
import wumpus2.mstt2.languageprocessing.SimpleNavigatorSpeech;

public class NavigatorAgent extends Agent {
	EfficientHybridWumpusAgent agent;
	NavigatorSpeech speech;
	private AID speleologistAid;

	@Override
	protected void takeDown() {
		System.out.println("Navigator-agent " + getAID().getName() + " terminating.");
	}

	@Override
	protected void setup() {
		agent = new EfficientHybridWumpusAgent(4, 4, new AgentPosition(1, 1, AgentPosition.Orientation.FACING_NORTH));
		speech = new SimpleNavigatorSpeech();
		registerMe();
		discover();
		System.out.println("Hello! Navigator-agent " + getAID().getName() + " is ready.");
		addBehaviour(new ListenBehavior());
	}

	private void registerMe() {
		var dfd = new DFAgentDescription();
		dfd.setName(getAID());
		var sd = new ServiceDescription();
		sd.setType("navigator");
		sd.setName("wumpus-world");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
	}

	private void discover() {
		var template = new DFAgentDescription();
		var sd = new ServiceDescription();
		sd.setType("speleologist");
		template.addServices(sd);

		try {
			var result = DFService.search(this, template);
			speleologistAid = result[0].getName();
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
	}

	private class ListenBehavior extends CyclicBehaviour {
		@SneakyThrows
		@Override
		public void action() {
			//query - propose
			var mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			var msg = myAgent.receive(mt);
			if(msg != null) {
				var state = msg.getContent();
				var percept = speech.recognize(state);
				System.out.println("Navigator: received feelings. Feelings = " + state);
				addBehaviour(new FindActionBehaviour(percept));
			} else {
				block();
			}
		}
	}

	private class FindActionBehaviour extends OneShotBehaviour {
		WumpusPercept percept;

		FindActionBehaviour(WumpusPercept percept) {
			this.percept = percept;
		}

		@SneakyThrows
		@Override
		public void action() {
			var action = agent.act(percept).orElseThrow();
			var reply = new ACLMessage(ACLMessage.PROPOSE);
			System.out.println("Navigator: decided on action. Action = " + action);
			var actionSentence = speech.tellAction(action);
			reply.setLanguage("English");
			reply.setOntology("WumpusWorld");
			reply.setContent(actionSentence);
			reply.addReplyTo(speleologistAid);
			reply.addReceiver(speleologistAid);
			myAgent.send(reply);
		}
	}
}
