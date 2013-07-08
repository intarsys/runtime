package de.intarsys.claptz.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.claptz.InstrumentRegistryException;
import de.intarsys.claptz.impl.StandardInstrument;
import de.intarsys.claptz.impl.StandardInstrumentRegistry;
import de.intarsys.tools.environment.file.FileEnvironment;
import de.intarsys.tools.expression.ExpressionEvaluator;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.IStringEvaluatorAccess;
import de.intarsys.tools.expression.ScopedResolver;
import de.intarsys.tools.infoset.ElementFactory;
import de.intarsys.tools.infoset.IDocument;
import de.intarsys.tools.stream.StreamTools;

public class ClassLoaderInstrumentStore extends CommonInstrumentStore {

	public static final String META_DEFINITION = "instrument.xml"; //$NON-NLS-1$

	public static final String META_DIRECTORY = "INSTRUMENT-INF"; //$NON-NLS-1$

	public static final String META_PATH = META_DIRECTORY + "/"
			+ META_DEFINITION;

	final private ClassLoader classLoader;

	private static final Logger Log = PACKAGE.Log;

	public ClassLoaderInstrumentStore(ClassLoader classLoader) {
		super();
		this.classLoader = classLoader;
	}

	@Override
	public void flush(StandardInstrument instrument) throws IOException {
		throw new IOException("can't write to classpath");
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public void load(StandardInstrumentRegistry registry)
			throws InstrumentRegistryException {
		try {
			Enumeration<URL> resources = getClassLoader().getResources(
					META_PATH);
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				load(registry, resource);
			}
		} catch (IOException e) {
			throw new InstrumentRegistryException(e.getMessage(), e);
		}
	}

	protected void load(StandardInstrumentRegistry registry, URL resource)
			throws InstrumentRegistryException {
		InputStream is = null;
		try {
			is = resource.openStream();
			StandardInstrument instrument = new StandardInstrument(registry,
					this);
			instrument.setBaseDir(FileEnvironment.get().getBaseDir());
			IDocument doc = ElementFactory.get().parse(is);
			if (doc instanceof IStringEvaluatorAccess) {
				IStringEvaluator resolver = ScopedResolver.create(
						instrument.getExpressionEvaluator(), ExpressionEvaluator.get());
				((IStringEvaluatorAccess) doc).setStringEvaluator(resolver);
			}
			instrument.configure(doc.getRootElement());
			String id = instrument.getId();
			if (registry.lookupInstrument(id) == null) {
				registry.registerInstrument(instrument);
				Log.log(Level.FINE, "instrument '" + id + "' definition at '"
						+ resource + "'");
			} else {
				Log.log(Level.INFO, "instrument '" + id + "' redefinition at '"
						+ resource + "'");
			}
		} catch (Exception e) {
			throw new InstrumentRegistryException(
					"error loading instrument at '" + resource + "'");
		} finally {
			StreamTools.close(is);
		}
	}
}
