package de.intarsys.tools.bean;

/**
 * This is a marker interface to force instantiation of associated beans.
 * 
 * When using non-spring code ("legacy registries") in spring environment, we may want to
 * register instances in spring configurations anyway. Registering plain instances will have no effect
 * as nobody will collect and register.
 * 
 * A solution is to write a dedicated "installer" bean that gets registered in a spring configuration.
 * As normally we can (will) not put dependencies on these installer beans from any (potential) client
 * of the "legacy registry", the installer beans should be marked with {@link IBeanInstallationInstruction}
 * and get @Autowired at a very early stage in the application cycle.
 */
public interface IBeanInstallationInstruction {

}
