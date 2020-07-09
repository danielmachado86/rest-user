package io.dmcapps.dshopping.user;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
class UserApplicationLifeCycle {

    private static final Logger LOGGER = Logger.getLogger(UserApplicationLifeCycle.class);

    void onStart(@Observes StartupEvent ev) {


        LOGGER.info(".##.....##..######..########.########........###....########..####");
        LOGGER.info(".##.....##.##....##.##.......##.....##......##.##...##.....##..##.");
        LOGGER.info(".##.....##.##.......##.......##.....##.....##...##..##.....##..##.");
        LOGGER.info(".##.....##..######..######...########.....##.....##.########...##.");
        LOGGER.info(".##.....##.......##.##.......##...##......#########.##.........##.");
        LOGGER.info(".##.....##.##....##.##.......##....##.....##.....##.##.........##.");
        LOGGER.info("..#######...######..########.##.....##....##.....##.##........####");
        LOGGER.info("                                                Powered by Quarkus");
        LOGGER.infof("The application USER is starting with profile `%s`", ProfileManager.getActiveProfile());

    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application USER is stopping...");
    }
}