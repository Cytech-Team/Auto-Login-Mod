package cloud.sect.alm;

import cloud.sect.alm.config.ServerConfig;
import cloud.sect.alm.event.LegacyChatHandler;
import cloud.sect.alm.event.LegacyJoinHandler;
import cloud.sect.alm.keybinding.LegacyKeybinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = "auto_login_mod", name = "Auto Login Mod", version = "1.0.0-LEGACY", clientSideOnly = true)
public class AutoLoginModLegacy {

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ServerConfig.load(); // Load config early
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Register all events to Forge Bus
        MinecraftForge.EVENT_BUS.register(new LegacyChatHandler());
        MinecraftForge.EVENT_BUS.register(new LegacyJoinHandler());
        MinecraftForge.EVENT_BUS.register(this);
        
        LegacyKeybinding.register();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            LegacyKeybinding.checkKeybindings();
        }
    }
}
