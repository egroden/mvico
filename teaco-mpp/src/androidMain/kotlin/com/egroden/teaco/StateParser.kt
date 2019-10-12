package com.egroden.teaco

typealias StateParser<State> = Pair<(State) -> String, (String) -> State>