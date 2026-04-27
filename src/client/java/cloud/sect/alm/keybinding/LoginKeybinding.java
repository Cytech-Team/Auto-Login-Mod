package cloud.sect.alm.keybinding;

import cloud.sect.alm.gui.AlmConfigScreen;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class LoginKeybinding {
    private static KeyMapping configKeyMapping;

    public static void register() {
        configKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "alm.key.config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                KeyMapping.Category.MISC
        ));
    }

    public static void checkKeybindings(Minecraft client) {
        while (configKeyMapping.consumeClick()) {
            client.setScreen(new AlmConfigScreen(null));
        }
    }
}
