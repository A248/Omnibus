/* 
 * Omnibus-default
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-default is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-default is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-default. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.defaultimpl.resourcer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import space.arim.omnibus.defaultimpl.resourcer.DefaultResourceManager;
import space.arim.omnibus.resourcer.ResourceHook;
import space.arim.omnibus.resourcer.ResourceInfo;
import space.arim.omnibus.resourcer.ResourceManager;
import space.arim.omnibus.resourcer.ShutdownHandler;

public class DefaultResourceManagerTest {

	private ResourceManager resourcer;
	
	@BeforeEach
	public void setup() {
		resourcer = new DefaultResourceManager(new space.arim.omnibus.defaultimpl.events.TestableEvents());
	}
	
	@Test
	public void testBasicUsage() {
		CompletableFuture<Boolean> shutdown = new CompletableFuture<>();
		ResourceHook<TestResource> hook = resourcer.hookUsage(TestResource.class,
				() -> new ResourceInfo<>("", new TestResourceMainImpl(), new ShutdownHandler() {
					@Override
					public void preShutdownEvent() {
						//System.out.println("Pre shutdown event");
					}
					@Override
					public void postShutdownEvent() {
						shutdown.complete(true);
					}
				}));
		assertEquals(hook.getResource().getClass(), TestResourceMainImpl.class);

		resourcer.unhookUsage(hook);
		assertTrue(shutdown.isDone());
	}
	
	@Test
	public void testSharedUsage() {
		ResourceHook<TestResource> hookMain = resourcer.hookUsage(TestResource.class,
				() -> new ResourceInfo<>("", new TestResourceMainImpl(), ShutdownHandler.none()));
		ResourceHook<TestResource> hookAlt = resourcer.hookUsage(TestResource.class,
				() -> new ResourceInfo<>("", new TestResourceAltImpl(), ShutdownHandler.none()));
		assertEquals(hookMain.getResource().getClass(), TestResourceMainImpl.class);
		assertEquals(hookAlt.getResource().getClass(), TestResourceMainImpl.class);
		resourcer.unhookUsage(hookMain);
		assertEquals(hookAlt.getResource().getClass(), TestResourceAltImpl.class);
		resourcer.unhookUsage(hookAlt);
	}
	
}
