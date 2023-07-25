/*
 * intarsys GmbH
 * all rights reserved
 *
 */
package de.intarsys.tools.component;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

public class ComponentInfo implements Comparable<ComponentInfo> {

	public static final String IMPLEMENTATION_BUILD = "Implementation-Build"; //$NON-NLS-1$

	public static final String IMPLEMENTATION_TITLE = "Implementation-Title"; //$NON-NLS-1$

	public static final String IMPLEMENTATION_TIMESTAMP = "Implementation-Timestamp"; //$NON-NLS-1$

	public static final String IMPLEMENTATION_VENDOR = "Implementation-Vendor"; //$NON-NLS-1$

	public static final String IMPLEMENTATION_VERSION = "Implementation-Version"; //$NON-NLS-1$

	private static final ILogger Log = LogTools.getLogger(ComponentInfo.class);

	private String name;

	private String displayName;

	private String version;

	private String vendor;

	private String build;

	private String timestamp;

	private URL url;

	private final Map<String, ComponentInfo> children = new HashMap<>();

	public ComponentInfo() {
		//
	}

	public void addChild(ComponentInfo ci) {
		children.put(ci.getName(), ci);
	}

	/**
	 * Order is defined by name.
	 * 
	 * Note: this class has a natural ordering that is inconsistent with equals
	 */
	@Override
	public int compareTo(ComponentInfo o) { // NOSONAR
		return getDisplayName().compareTo(o.getDisplayName());
	}

	private String getAttributeValue(Attributes attributes, String attributeName) {
		return attributes.getValue(attributeName);
	}

	public String getBuild() {
		return build;
	}

	public Collection<ComponentInfo> getChildren() {
		return children.values().stream().sorted().toList();
	}

	public String getDisplayName() {
		if (displayName == null) {
			return getName();
		}
		return displayName;
	}

	public String getName() {
		return name;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public URL getUrl() {
		return url;
	}

	public String getVendor() {
		return vendor;
	}

	public String getVersion() {
		return version;
	}

	public void readFromContainer(URL url) {
		if (url.getFile().endsWith(".jar")) {
			readFromJar(url);
		} else {
			readFromDirectory(url);
		}
	}

	public void readFromDirectory(URL url) {
		InputStream is = null;
		try {
			URL manifestUrl = new URL(url, "META-INF/MANIFEST.MF");
			is = manifestUrl.openStream();
			Manifest manifest = new Manifest(is);
			setUrl(url);
			setName(PathTools.getName(url.toString()));
			readFromManifestAttributes(manifest.getMainAttributes());
		} catch (Exception ex) {
			Log.trace("component info loading manifest from {} failed ({})", url, ex.getMessage());
		} finally {
			StreamTools.close(is);
		}
	}

	protected void readFromJar(URL url) {
		InputStream is = null;
		JarInputStream jis = null;
		try {
			is = url.openStream();
			jis = new JarInputStream(is);
			Manifest manifest = jis.getManifest();
			if (manifest != null) {
				readFromManifestAttributes(manifest.getMainAttributes());
			}
			setUrl(url);
			setName(PathTools.getName(url.toString()));
		} catch (Exception ex) {
			Log.trace("component info loading manifest from {} failed ({})", url, ex.getMessage());
		} finally {
			StreamTools.close(jis);
			StreamTools.close(is);
		}
	}

	public void readFromManifestAttributes(Attributes attributes) {
		String title = getAttributeValue(attributes, IMPLEMENTATION_TITLE);
		if (StringTools.isEmpty(title)) {
			title = getName();
		}
		setDisplayName(title);
		String tmpVersion = getAttributeValue(attributes, IMPLEMENTATION_VERSION);
		if (StringTools.isEmpty(tmpVersion)) {
			tmpVersion = "unknown";
		}
		setVersion(tmpVersion);
		setVendor(getAttributeValue(attributes, IMPLEMENTATION_VENDOR));
		setBuild(getAttributeValue(attributes, IMPLEMENTATION_BUILD));
		setTimestamp(getAttributeValue(attributes, IMPLEMENTATION_TIMESTAMP));
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String toStringShort() {
		StringBuilder sb = new StringBuilder();
		sb.append(getDisplayName());
		if (!getDisplayName().equals(getName())) {
			sb.append(" (");
			sb.append(getName());
			sb.append(")");
		}
		sb.append(", ");
		sb.append(getVersion());
		if (!StringTools.isEmpty(getBuild())) {
			sb.append(", ");
			sb.append(getBuild());
		}
		if (!StringTools.isEmpty(getTimestamp())) {
			sb.append(", ");
			sb.append(getTimestamp());
		}
		return sb.toString();
	}

}
