package de.intarsys.tools.codeexit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.string.StringTools;

/**
 * An {@link ICodeExitHandler} that issues a HTTP GET request.
 * 
 * The source is expanded before each use.
 */
public class HttpGetCodeExitHandler extends CommonCodeExitHandler {

	@Override
	public Object perform(CodeExit codeExit, IFunctorCall call)
			throws FunctorInvocationException {
		String source = codeExit.getSourceExpanded(call.getArgs());
		if (StringTools.isEmpty(source)) {
			return null;
		}
		InputStream is = null;
		try {
			URL url = new URL(source);
			URLConnection connection = url.openConnection();
			is = connection.getInputStream();
			return StreamTools.toString(is, connection.getContentEncoding());
		} catch (IOException e) {
			throw new FunctorInvocationException(e);
		} finally {
			StreamTools.close(is);
		}
	}

}
