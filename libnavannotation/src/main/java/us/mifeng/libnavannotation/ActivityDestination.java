package us.mifeng.libnavannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface ActivityDestination {
    String pageUrl();
    boolean needLogin() ;
    boolean asStarter() ;
}
