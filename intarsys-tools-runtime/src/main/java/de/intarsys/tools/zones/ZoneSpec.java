package de.intarsys.tools.zones;

import java.util.function.Consumer;

import de.intarsys.tools.attribute.IAttributeSupport;

public class ZoneSpec {

	public static ZoneSpec create() {
		ZoneSpec spec = new ZoneSpec();
		return spec;
	}

	public static ZoneSpec with(String name) {
		ZoneSpec spec = new ZoneSpec();
		spec.setName(name);
		return spec;
	}

	public static ZoneSpec with(String name, IAttributeSupport as) {
		ZoneSpec spec = new ZoneSpec();
		spec.setName(name);
		spec.setAttributeSupport(as);
		return spec;
	}

	private String name;

	private IAttributeSupport attributeSupport;

	private Consumer<IZone> onEnter;

	private Consumer<IZone> onLeave;

	public IAttributeSupport getAttributeSupport() {
		return attributeSupport;
	}

	public String getName() {
		return name;
	}

	public Consumer<IZone> getOnLeave() {
		return onLeave;
	}

	public Consumer<IZone> getOnEnter() {
		return onEnter;
	}

	public void setAttributeSupport(IAttributeSupport attributeSupport) {
		this.attributeSupport = attributeSupport;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOnLeave(Consumer<IZone> onAfter) {
		this.onLeave = onAfter;
	}

	public void setOnEnter(Consumer<IZone> onBefore) {
		this.onEnter = onBefore;
	}

}
