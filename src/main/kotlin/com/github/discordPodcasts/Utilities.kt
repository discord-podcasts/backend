package com.github.discordPodcasts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ForkJoinPool

val globalScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher())