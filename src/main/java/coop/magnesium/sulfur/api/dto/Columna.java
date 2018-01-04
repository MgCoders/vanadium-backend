package coop.magnesium.sulfur.api.dto;


import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by rsperoni on 05/05/17.
 */
@NameBinding
@Retention(RUNTIME)
@Target({FIELD})
public @interface Columna {
    String titulo();
}
