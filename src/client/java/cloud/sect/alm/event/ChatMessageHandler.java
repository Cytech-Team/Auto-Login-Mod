package cloud.sect.alm.event;

import cloud.sect.alm.config.ServerConfig;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ChatMessageHandler {
    
    // Simple detection - just check for /register or /login in message
    private static final String REGISTER_CMD = "/register";
    private static final String LOGIN_CMD = "/login";
    
    public static void register() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            Minecraft client = Minecraft.getInstance();
            if (client.player == null) return;
            
            String text = message.getString().toLowerCase();
            
            // Check for register request - just look for /register
            if (text.contains(REGISTER_CMD)) {
                handleAutoResponse(client, true);
                return;
            }
            
            // Check for login request - just look for /login
            if (text.contains(LOGIN_CMD)) {
                handleAutoResponse(client, false);
                return;
            }
        });
    }
    
    private static void handleAutoResponse(Minecraft client, boolean forceRegister) {
        String serverIP = getCurrentServerIP(client);
        ServerConfig.ServerEntry entry = ServerConfig.getServer(serverIP);
        
        String password;
        boolean isRegisterCommand;
        
        if (entry != null) {
            password = ServerConfig.getDecryptedCommand(entry);
            isRegisterCommand = entry.isRegisterCommand;
        } else if (ServerConfig.hasGlobalPassword()) {
            password = ServerConfig.getGlobalPassword();
            isRegisterCommand = ServerConfig.isGlobalRegisterMode();
        } else {
            client.player.sendSystemMessage(
                Component.literal("[Auto-Login] Server asked for password but no config found! Use /alm set <password>")
            );
            return;
        }
        
        if (password.isEmpty()) return;
        
        // Override with detected type if server specifically asks
        if (forceRegister) {
            isRegisterCommand = true;
        }
        
        final boolean finalIsRegister = isRegisterCommand;
        final String finalPassword = password;
        
        // Small delay to avoid spam
        new Thread(() -> {
            try {
                Thread.sleep(500);
                client.execute(() -> {
                    if (client.player != null) {
                        String cmd = finalIsRegister ? "register " + finalPassword : "login " + finalPassword;
                        client.player.connection.sendCommand(cmd);
                        client.player.sendSystemMessage(
                            Component.literal("[Auto-Login] Smart-detected and sent /" + (finalIsRegister ? "register" : "login"))
                        );
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private static String getCurrentServerIP(Minecraft client) {
        if (client.getCurrentServer() != null) {
            return client.getCurrentServer().ip;
        }
        return "singleplayer";
    }
}
