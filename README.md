# Omnibus

Events, services, and concurrency frameworks. Currently used by [LibertyBans](https://github.com/A248/LibertyBans), but practically usable by anyone.

## Usage

### Events and Services

See the EventBus interface for events. Registry is for the services registry. You can obtain a Registry and EventBus by creating a `DefaultOmnibus` and using it, or `OmnibusProvider.getOmnibus()` for the global instance.

### Concurrency

Most prominent is the futures framework centered around `FactoryOfTheFuture`/`CentralisedFuture`/`ReactionStage`. This framework introduces a concept of an application main thread, and it centralizes creation of futures. Some basic implementations are provided in `space.arim.omnibus.util.concurrent.impl`. A more advanced future factory implementation with anti-deadlock features is located at [ManagedWaitStrategies](https://github.com/A248/ManagedWaitStrategies).

Also particularly useful is `EnhancedExecutor`, whose provided base implementation uses the system-wide scheduler in CompletableFuture.delayedExecutor.

## Dependency Information

The dependency is `org.libertybans:omnibus:{VERSION}`. Since version 1.1.0, it is available from Maven Central.

With Maven, this would be applied as follows.

```xml
<dependency>
	<groupId>org.libertybans</groupId>
	<artifactId>omnibus</artifactId>
	<version>1.1.0</version>
</dependency>
```

## License

See the file LICENSE.txt for the full GNU Lesser General Public License v3. The license applies to this entire repository and is copied into binaries as LICENSE.txt.
