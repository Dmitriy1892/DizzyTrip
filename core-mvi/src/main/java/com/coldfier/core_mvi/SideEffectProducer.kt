package com.coldfier.core_mvi

/**
 *  SideEffectProducer needs to provide (or not) a [SideEffect]
 *  according to the [State] and [Action].
 *  [SideEffect] is a one-shot event that triggers some one-time used UI elements
 *  like showing a Toasts, Snackbars, or navigating to the new screen etc.
 */
typealias SideEffectProducer<State, Action, SideEffect> =
            (state: State, action: Action) -> SideEffect?