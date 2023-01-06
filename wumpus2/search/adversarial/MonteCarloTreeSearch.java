package wumpus2.search.adversarial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import wumpus2.search.framework.GameTree;
import wumpus2.search.framework.Metrics;
import wumpus2.search.framework.Node;
import wumpus2.search.framework.NodeFactory;

/**
 * Artificial Intelligence A Modern Approach (4th Edition): page ???.<br>
 *
 * <pre>
 * <code>
 * function MONTE-CARLO-TREE-SEARCH(state) returns an action
 *   tree &larr; NODE(state)
 *   while TIME-REMAINING() do
 *   	leaf &larr; SELECT(tree)
 *   	child &larr; EXPAND(leaf)
 *   	result &larr; SIMULATE(child)
 *   	BACKPROPAGATE(result, child)
 *   return the move in ACTIONS(state) whose node has highest number of playouts
 * </code>
 * </pre>
 * <p>
 * Figure ?.? The Monte Carlo tree search algorithm. A game tree, tree, is initialized, and
 * then we repeat the cycle of SELECT / EXPAND / SIMULATE/ BACKPROPAGATE until we run  out
 * of time, and return the move that led to the node with the highest number of playouts.
 *
 * @param <S> Type which is used for states in the game.
 * @param <A> Type which is used for actions in the game.
 * @param <P> Type which is used for players in the game.
 * @author Suyash Jain
 */

public class MonteCarloTreeSearch<S, A, P> implements AdversarialSearch<S, A> {
	private final Game<S, A, P> game;
	private final GameTree<S, A> tree;
	private int iterations = 0;

	public MonteCarloTreeSearch(Game<S, A, P> game, int iterations) {
		this.game = game;
		this.iterations = iterations;
		tree = new GameTree<>();
	}

	@Override
	public A makeDecision(S state) {
		// tree <-- NODE(state)
		tree.addRoot(state);
		// while TIME-REMAINING() do
		while(iterations != 0) {
			// leaf <-- SELECT(tree)
			Node<S, A> leaf = select(tree);
			// child <-- EXPAND(leaf)
			Node<S, A> child = expand(leaf);
			// result <-- SIMULATE(child)
			// result = true if player of root node wins
			boolean result = simulate(child);
			// BACKPROPAGATE(result, child)
			backpropagate(result, child);
			// repeat the four steps for set number of iterations
			--iterations;
		}
		// return the move in ACTIONS(state) whose node has highest number of playouts
		return bestAction(tree.getRoot());
	}

	private Node<S, A> select(GameTree<S, A> gameTree) {
		Node<S, A> node = gameTree.getRoot();
		while(!game.isTerminal(node.getState()) && isNodeFullyExpanded(node)) {
			node = gameTree.getChildWithMaxUCT(node);
		}
		return node;
	}

	private Node<S, A> expand(Node<S, A> leaf) {
		if(game.isTerminal(leaf.getState()))
			return leaf;
		else
			return randomlySelectUnvisitedChild(leaf);
	}

	private boolean simulate(Node<S, A> node) {
		while(!game.isTerminal(node.getState())) {
			Random rand = new Random();
			A a = game.getActions(node.getState()).get(rand.nextInt(game.getActions(node.getState()).size()));
			S result = game.getResult(node.getState(), a);
			NodeFactory<S, A> nodeFactory = new NodeFactory<>();
			node = nodeFactory.createNode(result);
		}
		return game.getUtility(node.getState(), game.getPlayer(tree.getRoot().getState())) > 0;
	}

	private void backpropagate(boolean result, Node<S, A> node) {
		tree.updateStats(result, node);
		if(tree.getParent(node) != null) backpropagate(result, tree.getParent(node));
	}

	private A bestAction(Node<S, A> root) {
		Node<S, A> bestChild = tree.getChildWithMaxPlayouts(root);
		for(A a : game.getActions(root.getState())) {
			S result = game.getResult(root.getState(), a);
			if(result.equals(bestChild.getState())) return a;
		}
		return null;
	}

	private boolean isNodeFullyExpanded(Node<S, A> node) {
		List<S> visitedChildren = tree.getVisitedChildren(node);
		for(A a : game.getActions(node.getState())) {
			S result = game.getResult(node.getState(), a);
			if(!visitedChildren.contains(result)) {
				return false;
			}
		}
		return true;
	}


	private Node<S, A> randomlySelectUnvisitedChild(Node<S, A> node) {
		List<S> unvisitedChildren = new ArrayList<>();
		List<S> visitedChildren = tree.getVisitedChildren(node);
		for(A a : game.getActions(node.getState())) {
			S result = game.getResult(node.getState(), a);
			if(!visitedChildren.contains(result)) unvisitedChildren.add(result);
		}
		Random rand = new Random();
		return tree.addChild(node, unvisitedChildren.get(rand.nextInt(unvisitedChildren.size())));
	}

	@Override
	public Metrics getMetrics() {
		return null;
	}
}
