package de.intarsys.tools.documentation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Make {@link See} repeatable
 */
@Retention(RetentionPolicy.SOURCE)
public @interface SeeContainer {

	See[] value();
}
