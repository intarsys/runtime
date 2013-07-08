package de.intarsys.tools.logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.hex.HexTools;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.valueholder.IValueHolder;

public class CommonDumpObject implements IDumpObject {

	public List<String> dump(String prefix, Object object, IDumpObject details) {
		List<String> result = new ArrayList<String>();
		if (object instanceof byte[]) {
			String hex;

			byte[] array = (byte[]) object;
			if (array.length > 50) { // arbitrary
				hex = HexTools.bytesToHexString(array, 0, 49) + "..."; //$NON-NLS-1$
			} else {
				hex = HexTools.bytesToHexString(array);
			}
			result.add(prefix + "byte[" + array.length + "] " + hex); //$NON-NLS-1$ //$NON-NLS-2$
		} else if (object instanceof char[]) {
			result.add(prefix + "a character array"); //$NON-NLS-1$
		} else if (object instanceof IValueHolder<?>) {
			IValueHolder<?> ref = (IValueHolder<?>) object;
			List<String> attrValue = details.dump("", ref.get(), details); //$NON-NLS-1$
			Iterator<String> it = attrValue.iterator();
			result.add(prefix + "a reference to " + it.next()); //$NON-NLS-1$
			while (it.hasNext()) {
				result.add(prefix + it.next());
			}
		} else if (object instanceof Object[]) {
			Object[] array = (Object[]) object;
			result.add(prefix + "["); //$NON-NLS-1$
			for (Object element : array) {
				result.addAll(details.dump(prefix + LogTools.INDENT, element,
						details));
			}
			result.add(prefix + "]"); //$NON-NLS-1$
		} else if (object instanceof int[]) {
			int[] array = (int[]) object;
			result.add(prefix + Arrays.toString(array));
		} else if (object instanceof Integer) {
			int i = (Integer) object;
			result.add(prefix + i + " | 0x" + Integer.toHexString(i)); //$NON-NLS-1$
		} else if (object instanceof Byte) {
			byte i = (Byte) object;
			result.add(prefix + i + " | 0x" + Integer.toHexString(i)); //$NON-NLS-1$
		} else {
			result.add(prefix + StringTools.safeString(object));
		}
		return result;
	}

}
