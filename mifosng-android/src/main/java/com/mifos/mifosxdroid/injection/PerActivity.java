package com.mifos.mifosxdroid.injection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * @author Rajan Maurya
 *         A scoping annotation to permit objects whose lifetime should
 *         conform to the life of the Activity to be memorised in the
 *         correct component.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {
}
