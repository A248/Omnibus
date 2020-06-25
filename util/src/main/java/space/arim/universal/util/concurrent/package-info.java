/**
 * Core interfaces for concurrency-related classes. <br>
 * <br>
 * There are 2 frameworks in this package: <br>
 * <br>
 * Empowered task scheduling, spearheaded by {@link EnhancedExecutor}, which enables more than
 * just fixed rate / fixed delay scheduling by allowing delay calculators to determine delays
 * between task execution dynamically. <br>
 * <br>
 * Enhanced {@link CompletionStage}/{@link CompletableFuture} API in the form of
 * {@link ReactionStage}/{@link CentralisedFuture}, which add <i>Sync</i> variants of
 * {@code CompletionStage} methods to run synchronously with the main thread in accordance
 * with the concept of a main thread as defined in {@link SynchronousExecutor}. Since
 * such execution may encounter deadlocks if not done carefully, {@link FactoryOfTheFuture}
 * is intended to be used to construct implementations of {@code ReactionStage} or subclasses
 * of {@code CentralisedFuture}, which may employ special handling when awaiting completion. <br>
 * <br>
 * Additionally, there are a few miscellanous helper classes, including {@link CompetitiveFuture} to change
 * the default executor of a {@code CompletableFuture} by subclassing, as well as {@link StoppableExecutor}
 * to mirror some of the shutdown-related methods of {@link ExecutorService}.
 * 
 */
package space.arim.universal.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
