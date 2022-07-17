package com.coldfier.core_mvi

typealias Reducer<State, Action> = (state: State, action: Action) -> State

