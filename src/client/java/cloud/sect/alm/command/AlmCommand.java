package cloud.sect.alm.command;

import cloud.sect.alm.config.ServerConfig;
import cloud.sect.alm.gui.AlmConfigScreen;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class AlmCommand {
    private static final String PREFIX = "§6[Auto-Login] §r";

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                ClientCommands.literal("alm")
                    // Open GUI
                    .executes(context -> {
                        Minecraft.getInstance().execute(() -> {
                            Minecraft.getInstance().setScreen(new AlmConfigScreen(null));
                        });
                        return 1;
                    })
                    .then(ClientCommands.literal("gui")
                        .executes(context -> {
                            Minecraft.getInstance().execute(() -> {
                                Minecraft.getInstance().setScreen(new AlmConfigScreen(null));
                            });
                            return 1;
                        })
                    )
                    // Add new server
                    .then(ClientCommands.literal("add")
                        .then(ClientCommands.argument("ip", StringArgumentType.string())
                            .then(ClientCommands.argument("password", StringArgumentType.greedyString())
                                .executes(context -> {
                                    String ip = StringArgumentType.getString(context, "ip");
                                    String password = StringArgumentType.getString(context, "password");
                                    ServerConfig.setServer(ip, password, false, false);
                                    context.getSource().sendFeedback(
                                        Component.literal(PREFIX + "§aAdded server: §e" + ip + " §awith §6/login")
                                    );
                                    return 1;
                                })
                            )
                        )
                    )
                    // List servers and global settings
                    .then(ClientCommands.literal("list")
                        .executes(context -> {
                            String globalMode = ServerConfig.isGlobalRegisterMode() ? "§e/register" : "§e/login";
                            String hasGlobal = ServerConfig.hasGlobalPassword() ? "§a[Set]" : "§c[Not Set]";
                            String autoType = ServerConfig.isAutoTypeEnabled() ? "§a[AutoType: ON]" : "§c[AutoType: OFF]";
                            context.getSource().sendFeedback(Component.literal(PREFIX + "§fGlobal: " + globalMode + " " + hasGlobal + " " + autoType));
                            
                            if (!ServerConfig.getAllServers().isEmpty()) {
                                context.getSource().sendFeedback(Component.literal(PREFIX + "§fPer-Server Settings:"));
                                ServerConfig.getAllServers().forEach((ip, entry) -> {
                                    String type = entry.isRegisterCommand ? "§e[Reg]" : "§e[Login]";
                                    context.getSource().sendFeedback(
                                        Component.literal("  " + type + " §7" + ip)
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
                                    Component.literal(PREFIX + "§cRemoved server: §7" + ip)
                                );
                                return 1;
                            })
                        )
                    )
                    // Set global login mode
                    .then(ClientCommands.literal("set")
                        .then(ClientCommands.argument("password", StringArgumentType.greedyString())
                            .executes(context -> {
                                String password = StringArgumentType.getString(context, "password");
                                ServerConfig.setGlobalPassword(password);
                                ServerConfig.setGlobalRegisterMode(false);
                                context.getSource().sendFeedback(
                                    Component.literal(PREFIX + "§aSet global §6/login §apassword for all servers")
                                );
                                return 1;
                            })
                        )
                    )
                    // Help
                    .then(ClientCommands.literal("help")
                        .executes(context -> {
                            context.getSource().sendFeedback(Component.literal("§6§l=== Auto-Login Mod Help ==="));
                            context.getSource().sendFeedback(Component.literal("§e/alm gui §7- Open configuration screen"));
                            context.getSource().sendFeedback(Component.literal("§e/alm set <pass> §7- Global /login"));
                            context.getSource().sendFeedback(Component.literal("§e/alm add <ip> <pass> §7- Per-server /login"));
                            context.getSource().sendFeedback(Component.literal("§e/alm list §7- Show all settings"));
                            context.getSource().sendFeedback(Component.literal("§e/alm remove <ip> §7- Delete server config"));
                            context.getSource().sendFeedback(Component.literal("§6Hotkeys: §fF9 §7(Manual Login/GUI)"));
                            return 1;
                        })
                    )
            );
        });
    }
}
