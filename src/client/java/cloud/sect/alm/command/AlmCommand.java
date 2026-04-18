package cloud.sect.alm.command;

import cloud.sect.alm.config.ServerConfig;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.network.chat.Component;

public class AlmCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                ClientCommands.literal("alm")
                    // Add new server
                    .then(ClientCommands.literal("add")
                        .then(ClientCommands.argument("ip", StringArgumentType.string())
                            .then(ClientCommands.argument("password", StringArgumentType.greedyString())
                                .executes(context -> {
                                    String ip = StringArgumentType.getString(context, "ip");
                                    String password = StringArgumentType.getString(context, "password");
                                    ServerConfig.setServer(ip, password, false, false);
                                    context.getSource().sendFeedback(
                                        Component.literal("[Auto-Login] Added server: " + ip + " with /login")
                                    );
                                    return 1;
                                })
                            )
                        )
                    )
                    // Set global register mode (use /register for all servers)
                    .then(ClientCommands.literal("register")
                        .then(ClientCommands.argument("password", StringArgumentType.greedyString())
                            .executes(context -> {
                                String password = StringArgumentType.getString(context, "password");
                                ServerConfig.setGlobalPassword(password);
                                ServerConfig.setGlobalRegisterMode(true);
                                context.getSource().sendFeedback(
                                    Component.literal("[Auto-Login] Set global /register mode with password for all servers")
                                );
                                return 1;
                            })
                        )
                    )
                    // List servers and global settings
                    .then(ClientCommands.literal("list")
                        .executes(context -> {
                            // Show global settings
                            String globalMode = ServerConfig.isGlobalRegisterMode() ? "/register" : "/login";
                            String hasGlobal = ServerConfig.hasGlobalPassword() ? "[Set]" : "[Not Set]";
                            String autoType = ServerConfig.isAutoTypeEnabled() ? "[AutoType: ON]" : "[AutoType: OFF]";
                            context.getSource().sendFeedback(Component.literal("[Auto-Login] Global: " + globalMode + " " + hasGlobal + " " + autoType));
                            
                            // Show per-server settings
                            if (!ServerConfig.getAllServers().isEmpty()) {
                                context.getSource().sendFeedback(Component.literal("[Auto-Login] Per-Server Settings:"));
                                ServerConfig.getAllServers().forEach((ip, entry) -> {
                                    String type = entry.isRegisterCommand ? "[Reg]" : "[Login]";
                                    context.getSource().sendFeedback(
                                        Component.literal("  " + type + " " + ip)
                                    );
                                });
                            }
                            return 1;
                        })
                    )
                    // Remove server
                    .then(ClientCommands.literal("remove")
                        .then(ClientCommands.argument("ip", StringArgumentType.greedyString())
                            .executes(context -> {
                                String ip = StringArgumentType.getString(context, "ip");
                                ServerConfig.removeServer(ip);
                                context.getSource().sendFeedback(
                                    Component.literal("[Auto-Login] Removed server: " + ip)
                                );
                                return 1;
                            })
                        )
                    )
                    // Set global login mode (use /login for all servers)
                    .then(ClientCommands.literal("set")
                        .then(ClientCommands.argument("password", StringArgumentType.greedyString())
                            .executes(context -> {
                                String password = StringArgumentType.getString(context, "password");
                                ServerConfig.setGlobalPassword(password);
                                ServerConfig.setGlobalRegisterMode(false);
                                context.getSource().sendFeedback(
                                    Component.literal("[Auto-Login] Set global /login mode with password for all servers")
                                );
                                return 1;
                            })
                        )
                    )
                    // Toggle auto-type (auto login on join)
                    .then(ClientCommands.literal("autotype")
                        .executes(context -> {
                            boolean newState = !ServerConfig.isAutoTypeEnabled();
                            ServerConfig.setAutoTypeEnabled(newState);
                            context.getSource().sendFeedback(
                                Component.literal("[Auto-Login] Auto-type on join: " + (newState ? "ENABLED" : "DISABLED"))
                            );
                            return 1;
                        })
                    )
                    // Help
                    .then(ClientCommands.literal("help")
                        .executes(context -> {
                            context.getSource().sendFeedback(Component.literal("[Auto-Login] Commands:"));
                            context.getSource().sendFeedback(Component.literal("/alm set <password> - Global /login for all servers"));
                            context.getSource().sendFeedback(Component.literal("/alm register <password> - Global /register for all servers"));
                            context.getSource().sendFeedback(Component.literal("/alm add <ip> <password> - Per-server /login setting"));
                            context.getSource().sendFeedback(Component.literal("/alm autotype - Toggle auto-login on join"));
                            context.getSource().sendFeedback(Component.literal("/alm list - Show settings"));
                            context.getSource().sendFeedback(Component.literal("/alm remove <ip> - Remove per-server setting"));
                            context.getSource().sendFeedback(Component.literal("Press F9 (or your keybind) to manual login"));
                            return 1;
                        })
                    )
            );
        });
    }
}
