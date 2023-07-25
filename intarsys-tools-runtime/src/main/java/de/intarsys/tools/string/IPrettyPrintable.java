package de.intarsys.tools.string;

/**
 * This interface allows the {@link PrettyPrinter} visitor implementation to
 * create a more detailed output.
 * 
 * Within {@link #toString(PrettyPrinter)} the receiver should call the methods
 * of {@link PrettyPrinter} to serialize its state, especially
 * {@link PrettyPrinter#appendMember(String, Object, String)} for substructured
 * objects and {@link PrettyPrinter#appendValue(Object)} for "primitive"
 * objects.
 * 
 * Example <code>
	public void toString(PrettyPrinter printer) {
		printer.appendValue(getClass().getName() + " on " + getClassLoader());
	}
 * </code>
 *
 * Any object that is forwarded to the {@link PrettyPrinter} is checked for
 * {@link IPrettyPrintable} itself, if it does not implement this interface, it
 * is simply printed as a primitive object @see {@link IPrettyPrinterPrimitive}.
 */
public interface IPrettyPrintable {

	public void toString(PrettyPrinter printer);

}
