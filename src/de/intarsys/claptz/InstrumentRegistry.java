package de.intarsys.claptz;

/**
 * A singleton to access the VM {@link IInstrumentRegistry}.
 * 
 */
public class InstrumentRegistry {

	private static IInstrumentRegistry Active;

	public static IInstrumentRegistry get() {
		return Active;
	}

	public static void set(IInstrumentRegistry registry) {
		Active = registry;
	}

}
