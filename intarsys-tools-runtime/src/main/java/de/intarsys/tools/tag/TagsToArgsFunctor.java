package de.intarsys.tools.tag;

import java.io.IOException;
import java.util.List;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;
import de.intarsys.tools.zones.Zone;

/**
 * Make implicit context provided in {@link Tag} instances explicitly available
 * in {@link IArgs}.
 * 
 * <pre>
 * -input
 * -- args 		[optional] The initial args we are working on. If no args are supplied,
 * 				the functor args itself are used. The arguments are manipulated in place.
 * -- tags 		[optional] The collection or array of tags we are acting on
 * -- target 	[optional] The target to which the tags are currently attached
 * -- prefix 	A prefix to detect the tags that should become arguments. The prefix is stripped 
 *    			before further processing.
 * </pre>
 * 
 */
public class TagsToArgsFunctor implements IFunctor {

	private static final ILogger Log = LogTools.getLogger(TagsToArgsFunctor.class);

	@Override
	public Object perform(IFunctorCall call) throws FunctorException {
		IArgs args = call.getArgs();
		IArgs resultArgs = ArgTools.getArgs(args, "args", null);
		if (resultArgs == null) {
			resultArgs = args;
		}
		String prefix = ArgTools.getString(args, "prefix", "args.");
		List tags = ArgTools.getList(args, "tags", null);
		if (tags == null) {
			Object target = ArgTools.getObject(args, "target", null);
			if (target == null) {
				target = Zone.getCurrent();
			}
			tags = TagTools.getTagList(target);
		}
		for (Object tagObject : tags) {
			Tag tag;
			if (tagObject instanceof Tag) {
				tag = (Tag) tagObject;
			} else if (tagObject instanceof String) {
				try {
					tag = TagTools.parseTag((String) tagObject);
				} catch (IOException e) {
					Log.warn("tag cannot be parsed {}", tagObject);
					continue;
				}
			} else {
				continue;
			}
			String key = tag.getKey();
			String value = tag.getValue();
			// check if tag key starts with prefix
			if (key.startsWith(prefix)) {
				ArgTools.putPath(resultArgs, key.substring(prefix.length()), value);
			}
		}
		return resultArgs;
	}
}
