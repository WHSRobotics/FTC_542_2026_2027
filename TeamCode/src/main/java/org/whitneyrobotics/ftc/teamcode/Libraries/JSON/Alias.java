package org.whitneyrobotics.ftc.teamcode.Libraries.JSON;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Alias {
    String value();
}
