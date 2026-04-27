package cloud.sect.alm.util;

import cloud.sect.alm.AutoLoginModClient;

public class FeatureManager {
    private static boolean hasModMenu = false;
    private static boolean hasClothConfig = false;
    private static boolean hasArchitectury = false;

    public static void detectFeatures() {
        hasModMenu = isClassPresent("com.terraformersmc.modmenu.api.ModMenuApi");
        hasClothConfig = isClassPresent("me.shedaniel.clothconfig2.api.ConfigBuilder");
        hasArchitectury = isClassPresent("dev.architectury.platform.Platform");

        if (hasModMenu) AutoLoginModClient.LOGGER.info("§6[Auto-Login] §bModMenu detected! Enabling integration.");
        if (hasClothConfig) AutoLoginModClient.LOGGER.info("§6[Auto-Login] §bCloth Config detected! Upgrading GUI experience.");
        if (hasArchitectury) AutoLoginModClient.LOGGER.info("§6[Auto-Login] §bArchitectury detected! Multi-platform sync enabled.");
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasModMenu() { return hasModMenu; }
    public static boolean hasClothConfig() { return hasClothConfig; }
    public static boolean hasArchitectury() { return hasArchitectury; }
}
