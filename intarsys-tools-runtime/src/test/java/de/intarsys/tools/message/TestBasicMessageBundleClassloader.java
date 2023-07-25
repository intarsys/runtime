package de.intarsys.tools.message;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.intarsys.tools.nls.INlsContext;
import de.intarsys.tools.nls.StandardNlsContext;
import de.intarsys.tools.servicelocator.ServiceLocator;

public class TestBasicMessageBundleClassloader {

	@ClassRule
	public static TemporaryFolder tempFolder = new TemporaryFolder();;

	@BeforeClass
	public static void setUpClass() throws IOException {
		File targetFolder = tempFolder
				.newFolder(TestBasicMessageBundleClassloader.class.getPackageName().replace('.', '/'));
		for (String resourceName : Set.of("test_de.properties", "test_en.properties", "test.properties")) {
			try (InputStream stream = TestBasicMessageBundleClassloader.class
					.getResourceAsStream(String.format("ext/%s", resourceName))) {
				Files.copy(stream, targetFolder.toPath().resolve(resourceName));
			}
		}
	}

	private Locale oldLocale;

	@Test
	public void checkDe() throws Exception {
		Locale locale = new Locale("de");
		Locale.setDefault(locale);
		Tools.checkPropertyValue(getClass(), "prop3", "ext_value3");
		Tools.checkPropertyValue(getClass(), "prop4", "ext_value4_de");
	}

	@Test
	public void checkEn() throws Exception {
		Locale locale = new Locale("en");
		Locale.setDefault(locale);
		Tools.checkPropertyValue(getClass(), "prop3", "ext_value3");
		Tools.checkPropertyValue(getClass(), "prop4", "ext_value4_en");
	}

	@Test
	public void checkKl() throws Exception {
		Locale locale = new Locale("kl");
		Locale.setDefault(locale);
		Tools.checkPropertyValue(getClass(), "prop3", "ext_value3");
		Tools.checkPropertyValue(getClass(), "prop4", "ext_value4");
	}

	@Before
	public void setup() {
		oldLocale = Locale.getDefault();
		BasicMessageBundleFactory messageBundleFactory = new BasicMessageBundleFactory();
		DirectoryMessageClassLoaderProvider provider = new DirectoryMessageClassLoaderProvider();
		provider.setPath(tempFolder.getRoot().getAbsolutePath());
		messageBundleFactory.setClassloader(provider.getClassLoader());
		ServiceLocator.get().put(IMessageBundleFactory.class, messageBundleFactory);
		ServiceLocator.get().put(INlsContext.class, new StandardNlsContext());
	}

	@After
	public void teardown() {
		if (oldLocale != null) {
			Locale.setDefault(oldLocale);
		}
		ServiceLocator.get().remove(IMessageBundleFactory.class);
		ServiceLocator.get().remove(INlsContext.class);
	}

}
