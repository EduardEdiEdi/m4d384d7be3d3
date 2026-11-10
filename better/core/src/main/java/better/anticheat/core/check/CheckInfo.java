package better.anticheat.core.check;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows checks to be easily marked with all required information without working with any redundant
 * constructors. This allows all important information for loading to be available upon creation and allows for it to be
 * easily seen by other developers.
 * Example Annotation:
 *
 * @CheckInfo( name = "Example",
 * category = "Combat",
 * parent = "newconfig",
 * experimental = true,
 * requirements = { ClientFeatureRequirement.CLIENT_TICK_END }
 * )
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CheckInfo {

    /**
     * This parameter should be the primary name of the command. This is what will be shown in alerts,
     */
    String name();

    String category();

    String config() default "checks";

    boolean experimental() default false;

    /**
     * Feature requirements this check depends on. If a player does not support a required feature,
     * the check will not be loaded for that player.
     */
    ClientFeatureRequirement[] requirements() default {};
}
