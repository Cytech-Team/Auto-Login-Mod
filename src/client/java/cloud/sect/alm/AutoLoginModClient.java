package cloud.sect.alm;

import cloud.sect.alm.command.AlmCommand;
import cloud.sect.alm.config.ServerConfig;
import cloud.sect.alm.event.ChatMessageHandler;
import cloud.sect.alm.event.ClientTickEvents;
import cloud.sect.alm.event.PlayerJoinHandler;
import cloud.sect.alm.keybinding.LoginKeybinding;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoLoginModClient implements ClientModInitializer {
    public static final String MOD_ID = "auto-login-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("§6[Auto-Login] §aInitializing Secure Production-Grade Utility...");

        // Load Configuration
        ServerConfig.load();

        // Register Core Systems
        LoginKeybinding.register();
        ClientTickEvents.register();
        AlmCommand.register();
        PlayerJoinHandler.register();
        ChatMessageHandler.register();

        LOGGER.info("§6[Auto-Login] §aReady for secure session management.");
    }
}