# Omnibus
Events, services, and concurrency frameworks.

[![GitHub last commit](https://img.shields.io/github/last-commit/A248/Omnibus.svg)](https://github.com/A248/Omnibus/commits/master)[![Issue Resolution Time](http://isitmaintained.com/badge/resolution/A248/Omnibus.svg)](http://isitmaintained.com/project/A248/Omnibus "Average time to resolve an issue")[![Open Issues](http://isitmaintained.com/badge/open/A248/Omnibus.svg)](http://isitmaintained.com/project/A248/Omnibus "Percentage of issues still open")[![License: LGPL v3](https://img.shields.io/badge/License-LGPLv3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0-standalone.html)

## Usage

Some notable features.

### Events and Services

See the EventBus interface for events. Registry is for the services registry. You can obtain a Registry and EventBus by creating a `DefaultOmnibus` and using it.

### Concurrency

Most prominent is the futures framework centered around `FactoryOfTheFuture`/`CentralisedFuture`/`ReactionStage` which both introduces a concept of an application main thread and centralizes creation of futures. Some basic and partial implementations are provided in `space.arim.omnibus.util.concurrent.impl` A more advanced future factory implementation with anti-deadlock features is located at [ManagedWaitStrategies](https://github.com/A248/ManagedWaitStrategies).

Also particularly useful is `EnhancedExecutor`, whose provided base implementation uses the system-wide scheduler in CompletableFuture.delayedExecutor.

## Dependency Information

Dependency:

`space.arim.omnibus:omnibus:{VERSION}`

Repository:

`https://mvn-repo.arim.space/lesser-gpl3/`

A thanks to Cloudsmith for providing free repositories for FOSS.

### Maven

With maven, this would be applied as follows.

Dependency:

```xml
<dependency>
	<groupId>space.arim.omnibus</groupId>
	<artifactId>omnibus</artifactId>
	<version>{INSERT_VERSION}</version>
</dependency>
```

Repository:

``` xml
<repository>
	<id>arim-mvn-lgpl3</id>
	<url>https://mvn-repo.arim.space/lesser-gpl3/</url>
</repository>
```

## License

See the file LICENSE.txt for the full GNU Lesser General Public License v3. The license applies to this entire repository and is copied into binaries as LICENSE.txt.
