package cloud.localstack.docker.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to provide parameters to the LocalstackDockerTestRunner
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LocalstackDockerProperties {

    /**
     * Used for determining the host name of the machine running the docker containers
     * so that the containers can be addressed.
     */
    Class<? extends IHostNameResolver> hostNameResolver() default LocalHostNameResolver.class;

    /**
     * Determines if a new image is pulled from the docker repo before the tests are run.
     */
    boolean pullNewImage() default true;
}

