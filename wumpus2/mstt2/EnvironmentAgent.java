package wumpus2.mstt2;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import wumpus2.environment.wumpusworld.HybridWumpusAgent;
import wumpus2.environment.wumpusworld.WumpusAction;
import wumpus2.environment.wumpusworld.WumpusCave;
import wumpus2.environment.wumpusworld.WumpusEnvironment;
import wumpus2.environment.wumpusworld.WumpusPercept;

public class EnvironmentAgent extends Agent {
	private WumpusEnvironment wumpusEnvironment;
	private AID speleologistAID;
	private HybridWumpusAgent speleologist;
	private WumpusPercept percept;
	private int tick = 0;

	@Override
	protected void takeDown() {
		System.out.println("Environment-agent " + getAID().getName() + " terminating.");
	}

	@Override
	protected void setup() {
		registerMe();
		wumpusEnvironment = new WumpusEnvironment(new WumpusCave(4, 4, ""
				+ ". . W P "
				+ ". G P . "
				+ ". . . . "
				+ "S . P . "));
		speleologist = new EfficientHybridWumpusAgent(4, 4, new AgentPosition(1, 1, AgentPosition.Orientation.FACING_NORTH));
		percept = new WumpusPercept();
		wumpusEnvironment.addAgent(speleologist);


		var template = new DFAgentDescription();
		var sd = new ServiceDescription();
		sd.setType("speleologist");
		template.addServices(sd);
		try {
			var result = DFService.search(this, template);
			speleologistAID = result[0].getName();
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new ListenBehavior());
		System.out.println("Hello! Environment-agent " + getAID().getName() + " is ready.");
	}

	private void registerMe() {
		var dfd = new DFAgentDescription();
		dfd.setName(getAID());
		var sd = new ServiceDescription();
		sd.setType("environment");
		sd.setName("wumpus-world");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
	}

	private class ListenBehavior extends CyclicBehaviour {
		public void action() {
			//query - propose
			var mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchPerformative(ACLMessage.CFP));
			var msg = myAgent.receive(mt);
			if(msg != null) {
				if(ACLMessage.REQUEST == msg.getPerformative()) {
					addBehaviour(new QueryBehaviour());
				} else if(ACLMessage.CFP == msg.getPerformative()) {
					var move = msg.getContent();
					wumpusEnvironment.execute(speleologist, WumpusAction.fromString(move));
					addBehaviour(new AcceptBehaviour());
				} else {
					block();
				}
			} else {
				block();
			}
		}
	}

	private class QueryBehaviour extends OneShotBehaviour {
		var objectMapper = new ObjectMapper();

		@SneakyThrows
		@Override
		public void action() {
			var agentPosition = wumpusEnvironment.getAgentPosition(speleologist);
			percept = wumpusEnvironment.getPerceptSeenBy(speleologist);
			var report = new ACLMessage(ACLMessage.INFORM);
			report.setContent(objectMapper.writeValueAsString(new State(percept, tick++)));
			report.addReceiver(speleologistAID);
			report.addReplyTo(speleologistAID);
			myAgent.send(report);
			System.out.println("Environment: percept sent to speleologist");
			System.out.println("Environment: current position: " + agentPosition);
		}
	}

	private class AcceptBehaviour extends OneShotBehaviour {
		@Override
		public void action() {
			var report = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			report.setContent("OK");
			report.addReceiver(speleologistAID);
			report.addReplyTo(speleologistAID);
			System.out.println("Environment: step performed.");
			myAgent.send(report);
		}
	}
}
