package de.intarsys.claptz.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.SAXParser;

import de.intarsys.claptz.InstrumentRegistryException;
import de.intarsys.claptz.impl.StandardInstrument;
import de.intarsys.claptz.impl.StandardInstrumentRegistry;
import de.intarsys.tools.environment.file.FileEnvironment;
import de.intarsys.tools.expression.ExpressionEvaluator;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.IStringEvaluatorAccess;
import de.intarsys.tools.expression.ScopedResolver;
import de.intarsys.tools.file.IPathFilter;
import de.intarsys.tools.file.Loader;
import de.intarsys.tools.infoset.ElementFactory;
import de.intarsys.tools.infoset.IDocument;
import de.intarsys.tools.message.MessageBundle;
import de.intarsys.tools.sax.SAXTools;
import de.intarsys.tools.serialize.xml.XMLSerializationContext;
import de.intarsys.tools.serialize.xml.XMLSerializer;
import de.intarsys.tools.stream.StreamTools;

/**
 * Handle file based instrument io.
 * 
 */
public class DirectoryInstrumentStore extends CommonInstrumentStore {

	public static IInstrumentStore getLocal() {
		return new DirectoryInstrumentStore(new File(FileEnvironment.get()
				.getProfileDir(), "instruments"));
	}

	private final File file;

	public static final String META_DEFINITION = "instrument.xml"; //$NON-NLS-1$

	//
	public static final String META_DIRECTORY = "INSTRUMENT-INF"; //$NON-NLS-1$

	static private final MessageBundle Msg = PACKAGE.Messages;

	private static SAXParser parser;

	protected static SAXParser getParser() {
		return parser;
	}

	public DirectoryInstrumentStore(File file) {
		super();
		assert file != null;

		this.file = file;
		try {
			parser = SAXTools.getParserFactory().newSAXParser();
		} catch (Exception e) {
			throw new IllegalArgumentException("error creating SAXParser", e);
		}
	}

	public void flush(StandardInstrument instrument) throws IOException {
		// select more appropriate destination?
		File tempBaseDir = instrument.getBaseDir();
		if (tempBaseDir == null) {
			tempBaseDir = new File(getFile(), instrument.getId());
		}
		save(instrument, tempBaseDir);
	}

	public File getFile() {
		return file;
	}

	protected boolean isInstrumentHome(File home) {
		File metaDir = new File(home, META_DIRECTORY);
		File configFile = new File(metaDir, META_DEFINITION);
		return configFile.exists();
	}

	@Override
	public void load(StandardInstrumentRegistry registry)
			throws InstrumentRegistryException {
		loadAllInstruments(registry, file, Loader.ANY_FILTER);
	}

	/**
	 * Load all the instruments in the directory <code>base</code>.
	 * 
	 * @param base
	 * 
	 * @throws InstrumentRegistryException
	 */
	protected void loadAllInstruments(StandardInstrumentRegistry registry,
			File base, IPathFilter filter) throws InstrumentRegistryException {
		if (isInstrumentHome(base)) {
			loadInstrument(registry, base);
		} else {
			if (!base.isDirectory()) {
				return;
			}
			String[] instrumentDirs = base.list();
			if ((instrumentDirs == null) || (instrumentDirs.length == 0)) {
				return;
			}
			for (int i = 0; i < instrumentDirs.length; i++) {
				String path = instrumentDirs[i];
				if (filter.accept(path)) {
					File file = new File(base, path);
					loadInstrument(registry, file);
				}
			}
		}
	}

	/**
	 * If <code>home</code> is a instrument directory the instrument is loaded.
	 * 
	 * @param name
	 * @param home
	 * 
	 * @throws InstrumentRegistryException
	 */
	public void loadInstrument(StandardInstrumentRegistry registry, File home)
			throws InstrumentRegistryException {
		// if (!home.isDirectory()) {
		// return;
		// }
		File metaDir = new File(home, META_DIRECTORY);
		File configFile = new File(metaDir, META_DEFINITION);
		String name = home.getName();
		loadInstrument(registry, name, home, configFile);
	}

	/**
	 * create and start an instrument in the base directory "base" with the
	 * configuration in "file"
	 * 
	 * @param name
	 * @param base
	 * @param file
	 * 
	 * @throws InstrumentRegistryException
	 */
	protected void loadInstrument(StandardInstrumentRegistry registry,
			String name, File base, File file)
			throws InstrumentRegistryException {
		InputStream is = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			is = new BufferedInputStream(fis, 10000);
			StandardInstrument instrument = new StandardInstrument(registry,
					this);
			instrument.setBaseDir(base);
			IDocument doc = ElementFactory.get().parse(is);
			if (doc instanceof IStringEvaluatorAccess) {
				IStringEvaluator resolver = ScopedResolver.create(
						instrument.getExpressionEvaluator(), ExpressionEvaluator.get());
				((IStringEvaluatorAccess) doc).setStringEvaluator(resolver);
			}
			instrument.configure(doc.getRootElement());
			registry.registerInstrument(instrument);
		} catch (FileNotFoundException e) {
			// everything is fine - just not an instrument directory
		} catch (IOException e) {
			throw new InstrumentRegistryException(
					Msg.getString(
							"DirectoryInstrumentStore.ExLoadingDefinition", base.getAbsolutePath(), "line " + 0 + ", column " + 0 + ": " + e.getMessage()), e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
		} catch (Exception e) {
			throw new InstrumentRegistryException(
					Msg.getString(
							"DirectoryInstrumentStore.ExLoadingDefinition", base.getAbsolutePath(), e.getMessage()), e); //$NON-NLS-1$ 
		} finally {
			StreamTools.close(is);
		}
	}

	protected void save(StandardInstrument instrument, File dir)
			throws IOException {
		instrument.setBaseDir(dir);
		File tempInstrumentDir = new File(dir, "INSTRUMENT-INF"); //$NON-NLS-1$
		File tempInstrumentFile = new File(tempInstrumentDir, "instrument.xml"); //$NON-NLS-1$
		if (!tempInstrumentDir.exists()) {
			tempInstrumentDir.mkdirs();
		}
		// todo encoding
		OutputStream os = null;
		try {
			os = new FileOutputStream(tempInstrumentFile);
			XMLSerializationContext context = new XMLSerializationContext(os,
					true);
			XMLSerializer serializer = new InstrumentSerializer(context);
			serializer.serialize(instrument);
		} finally {
			StreamTools.close(os);
		}
	}

}
