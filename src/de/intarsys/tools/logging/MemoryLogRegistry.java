package de.intarsys.tools.logging;

import java.util.HashMap;
import java.util.Map;

public class MemoryLogRegistry {

	final private static MemoryLogRegistry ACTIVE = new MemoryLogRegistry();

	public static MemoryLogRegistry get() {
		return ACTIVE;
	}

	final private Map<String, MemoryLogHandler> handlers = new HashMap<String, MemoryLogHandler>();

	public MemoryLogHandler[] getHandlers() {
		return handlers.values().toArray(new MemoryLogHandler[handlers.size()]);
	}

	public MemoryLogHandler lookup(String id) {
		return handlers.get(id);
	}

	public void register(MemoryLogHandler handler) {
		handlers.put(handler.getId(), handler);
	}

	public void unregister(MemoryLogHandler handler) {
		handlers.remove(handler.getId());
	}
}
