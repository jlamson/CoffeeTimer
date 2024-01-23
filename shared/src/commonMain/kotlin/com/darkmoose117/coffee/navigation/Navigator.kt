package com.darkmoose117.coffee.navigation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface INavigator {
    val currentDestFlow: Flow<Nav.Dest>
    val navStackStateFlow: StateFlow<List<Nav.Dest>>

    fun push(dest: Nav.Dest)

    fun pop()
}

class Navigator(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : INavigator {
    private val job = Job()
    private val scope: CoroutineScope = CoroutineScope(job + dispatcher)
    private val mutex = Mutex()

    private val _navStackStateFlow =
        MutableStateFlow<List<Nav.Dest>>(
            listOf(Nav.Dest.RecipeList),
        )
    override val navStackStateFlow: StateFlow<List<Nav.Dest>> = _navStackStateFlow

    override val currentDestFlow: Flow<Nav.Dest> = navStackStateFlow.map { it.last() }

    override fun push(dest: Nav.Dest) = updateStack { stack -> stack + dest }

    override fun pop() =
        updateStack { stack ->
            if (stack.size > 1) {
                stack.dropLast(n = 1)
            } else {
                stack
            }
        }

    private fun updateStack(block: (List<Nav.Dest>) -> List<Nav.Dest>) {
        scope.launch {
            mutex.withLock {
                _navStackStateFlow.update(block)
            }
        }
    }
}
