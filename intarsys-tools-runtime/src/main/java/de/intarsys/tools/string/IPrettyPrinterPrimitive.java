package de.intarsys.tools.string;

/**
 * An object printed by the {@link PrettyPrinter} can implement this feature to
 * add support for more "primitive" types than known by the
 * {@link PrettyPrinter} from start.
 * 
 * All {@link IPrettyPrinterPrimitive} instances on the print stack of the
 * {@link PrettyPrinter} are called in reverse order for printing a value.
 *
 */
public interface IPrettyPrinterPrimitive {

	public boolean toString(PrettyPrinter printer, Object value);

}
