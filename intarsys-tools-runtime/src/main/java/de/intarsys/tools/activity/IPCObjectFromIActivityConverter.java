package de.intarsys.tools.activity;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.IConverter;
import de.intarsys.tools.ipc.IIPCScope;
import de.intarsys.tools.ipc.IPCHandle;
import de.intarsys.tools.ipc.IPCObject;
import de.intarsys.tools.ipc.IPCScope;

public class IPCObjectFromIActivityConverter implements IConverter<IActivity<?>, IPCHandle> {

	@Override
	public IPCHandle convert(IActivity<?> source) throws ConversionException {
		IIPCScope ipcScope = IPCScope.get();
		IPCHandle handle = ipcScope.exportObject(source);
		return handle;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class<IActivity> getSourceType() {
		return IActivity.class;
	}

	@Override
	public Class<IPCObject> getTargetType() {
		return IPCObject.class;
	}
}
