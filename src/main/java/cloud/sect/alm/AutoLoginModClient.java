package cloud.sect.alm;

import cloud.sect.alm.command.AlmCommand;
import cloud.sect.alm.config.ServerConfig;
import cloud.sect.alm.event.ChatMessageHandler;
import cloud.sect.alm.event.ClientTickEvents;
import cloud.sect.alm.event.PlayerJoinHandler;
import cloud.sect.alm.event.ScreenHandler;
import cloud.sect.alm.keybinding.LoginKeybinding;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoLoginModClient implements ClientModInitializer {
    public static final String MOD_ID = "auto-login-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("§6[Auto-Login] §aStarting initialization...");

        try {
            // Load Configuration
            LOGGER.info("§6[Auto-Login] §7Loading configuration...");
            ServerConfig.load();
            
            // Register Core Systems
            LOGGER.info("§6[Auto-Login] §7Registering keybindings...");
            LoginKeybinding.register();
            
            LOGGER.info("§6[Auto-Login] §7Registering tick events...");
            ClientTickEvents.register();
            
            LOGGER.info("§6[Auto-Login] §7Registering commands...");
            AlmCommand.register();
            
            LOGGER.info("§6[Auto-Login] §7Registering network handlers...");
            PlayerJoinHandler.register();
            ChatMessageHandler.register();
            ScreenHandler.register();

            LOGGER.info("§6[Auto-Login] §aReady for secure session management.");
        } catch (Exception e) {
            LOGGER.error("§6[Auto-Login] §cInitialization failed!", e);
        }
    }
}