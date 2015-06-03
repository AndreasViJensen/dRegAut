public FA determinize() {
		NFA f1 = removeLambdas();
		FA m = new FA();
		
	//Alphabet
		m.alphabet = f1.alphabet;
		
	//Initial state:
		Map<Set<State>,State> setToStateMap = new HashMap<Set<State>,State>();
		Set<State> initialSet = new HashSet<State>();
		initialSet.add(f1.initial);
		setToStateMap.put(initialSet, new State());
		m.initial = setToStateMap.get(initialSet);
		
	//Transitions:
		Set<Set<State>> res = new HashSet<Set<State>>(); // contains the states of m that are reachable and have been visited
		Set<Set<State>> pending = new HashSet<Set<State>>(); // contains the states of m that are reachable but have not yet been visited
		pending.add(initialSet);
		//INvariants: (1) All sets in pending are contained in the keyset of setToStateMap.
		// (2) Only unvisited sets are added to pending
		while (!pending.isEmpty()) {
			Set<State> q = pending.iterator().next();
			pending.remove(q);
			res.add(q);
			//In this for-loop a new transition is added to m according to the definition of the transition function.
			//if statements are included to ensure that invariant (1) and (2) are kept true.
			for (char c : f1.alphabet.symbols){
				Set<State> nextSetOfStates = new HashSet<State>();
				for (State p :q) {
					nextSetOfStates.addAll(f1.delta(p, c));
				}
				if (!res.contains(nextSetOfStates)) {
					pending.add(nextSetOfStates);
					if (!setToStateMap.containsKey(nextSetOfStates)) {
						setToStateMap.put(nextSetOfStates, new State());
					}
				}
				m.transitions.put(new StateSymbolPair(setToStateMap.get(q),c),setToStateMap.get(nextSetOfStates));
			}	
		}
		
	//States and accept-states:
		for (Set<State> set: res) {
			//adding reachable states to n:
			m.states.add(setToStateMap.get(set));
			//Checking whether we are dealing with an accept state of m, and adding it to accept-set if so:
			Set<State> testSet = new HashSet<State>();
			testSet.addAll(set);
			testSet.retainAll(f1.accept);
			if(!testSet.isEmpty()) {
				m.accept.add(setToStateMap.get(set));
			}
		}
		return m;
	}
