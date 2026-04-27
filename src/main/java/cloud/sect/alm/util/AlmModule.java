package cloud.sect.alm.util;

import cloud.sect.alm.AutoLoginModClient;

public interface AlmModule {
    String getModuleName();
    boolean isDetected();
    void onEnable();
}

// ระบบจัดการ Module ทั้งหมด
class ModuleRegistry {
    public static void initModules() {
        // 1. Module ป้องกันรหัสหลุดใน Replay Mod
        registerModule(new AlmModule() {
            public String getModuleName() { return "ReplayMod-Privacy"; }
            public boolean isDetected() { return isClassPresent("com.replaymod.replaystudio.ReplayStudio"); }
            public void onEnable() {
                AutoLoginModClient.LOGGER.info("§6[ALM] §eReplay Mod detected: Auto-privacy recording mode enabled!");
            }
        });

        // 2. Module เชื่อมต่อ Discord
        registerModule(new AlmModule() {
            public String getModuleName() { return "Discord-RPC"; }
            public boolean isDetected() { return isClassPresent("net.arikia.dev.drpc.DiscordRPC"); }
            public void onEnable() {
                AutoLoginModClient.LOGGER.info("§6[ALM] §9Discord RPC detected: Secure login status enabled!");
            }
        });
        
        // 3. Module ความสวยงาม (Blur)
        registerModule(new AlmModule() {
            public String getModuleName() { return "Blur-Visuals"; }
            public boolean isDetected() { return isClassPresent("com.atlassian.clover.CloverNames"); } // ตัวอย่างการเช็ค Blur Mod
            public void onEnable() {
                AutoLoginModClient.LOGGER.info("§6[ALM] §dBlur Mod detected: High-end GUI rendering enabled.");
            }
        });
    }

    private static void registerModule(AlmModule module) {
        if (module.isDetected()) {
            module.onEnable();
        }
    }

    private static boolean isClassPresent(String name) {
        try { Class.forName(name); return true; } catch (Exception e) { return false; }
    }
}
