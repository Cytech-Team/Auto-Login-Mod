package cloud.sect.alm.keybinding;

import cloud.sect.alm.gui.AlmConfigScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import java.lang.reflect.Constructor;

public class LoginKeybinding {
    private static KeyMapping configKeyMapping;

    public static void register() {
        try {
            // We use absolute reflection to find ANY constructor that looks like a KeyMapping
            Constructor<?>[] constructors = KeyMapping.class.getConstructors();
            for (Constructor<?> c : constructors) {
                try {
                    int count = c.getParameterCount();
                    if (count == 4) {
                        // Standard: (String, Type, int, String)
                        configKeyMapping = (KeyMapping) c.newInstance("alm.key.config", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F9, "key.categories.misc");
                        break;
                    } else if (count == 3) {
                        // Variant: (String, int, String)
                        configKeyMapping = (KeyMapping) c.newInstance("alm.key.config", GLFW.GLFW_KEY_F9, "key.categories.misc");
                        break;
                    }
                } catch (Throwable ignored) {}
            }

            if (configKeyMapping != null) {
                KeyBindingHelper.registerKeyBinding(configKeyMapping);
                cloud.sect.alm.AutoLoginModClient.LOGGER.info("F9 Keybinding linked via Reflection.");
            } else {
                // Last ditch effort: Try default constructor if available (unlikely)
                configKeyMapping = new KeyMapping("alm.key.config", GLFW.GLFW_KEY_F9, "key.categories.misc");
                KeyBindingHelper.registerKeyBinding(configKeyMapping);
            }
        } catch (Throwable t) {
            cloud.sect.alm.AutoLoginModClient.LOGGER.error("Keybinding linkage failed!", t);
        }
    }

    public static void checkKeybindings(Minecraft client) {
        try {
            if (configKeyMapping != null && configKeyMapping.consumeClick()) {
                client.setScreen(new AlmConfigScreen(null));
            }
        } catch (Throwable ignored) {}
    }
}
