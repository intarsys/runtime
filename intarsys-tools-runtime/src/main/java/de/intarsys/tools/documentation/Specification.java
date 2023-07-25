package de.intarsys.tools.documentation;

/**
 * Mark this class as a representation of a specification document.
 * 
 */
public @interface Specification {

	String description() default "";

	String family() default "";

	String link() default "";

	String organization() default "";

	String title() default "";

}
