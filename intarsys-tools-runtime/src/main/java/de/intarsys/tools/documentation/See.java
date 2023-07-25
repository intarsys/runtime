package de.intarsys.tools.documentation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A statement that one can find additional information regarding the implementation of the
 * annotated element in the referenced entity (represented by a class).
 * 
 */
@Retention(RetentionPolicy.SOURCE)
@Repeatable(SeeContainer.class)
public @interface See {

	/**
	 * An excerpt from the doc clarifying the context of the reference.
	 * 
	 * @return An excerpt from the doc clarifying the context of the reference.
	 */
	String rule() default "";

	/**
	 * The "documentation" meta element.
	 * 
	 * @return The "documentation" meta element.
	 */
	String doc();

	/**
	 * A textual representation of a dedicated part of the "doc" element.
	 * 
	 * This can be for example a page "p 12", following pages "pp 12", a chapter "ch 3" or whatever makes sense.
	 * 
	 * @return A textual representation of a dedicated part of the "doc" element.
	 */
	String ref() default "";

}
