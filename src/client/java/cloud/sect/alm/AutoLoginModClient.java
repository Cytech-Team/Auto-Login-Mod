package cloud.sect.alm;

import cloud.sect.alm.command.AlmCommand;
import cloud.sect.alm.config.ServerConfig;
import cloud.sect.alm.event.ClientTickEvents;
import cloud.sect.alm.keybinding.LoginKeybinding;
import net.fabricmc.api.ClientModInitializer;

public class AutoLoginModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LoginKeybinding.register();
        ClientTickEvents.register();
        AlmCommand.register();

        ServerConfig.load();
    }
}