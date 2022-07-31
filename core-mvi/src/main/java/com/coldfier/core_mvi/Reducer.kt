package com.coldfier.core_mvi

/**
 *  Reducer needs to change [State] according to the [Action].
 *  It must doesn't contain a heavy calculations,
 *  it's only compares the new [Action] with current [State] and produces new [State]
 */
typealias Reducer<State, Action> = (state: State, action: Action) -> State