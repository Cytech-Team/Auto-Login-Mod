package cloud.sect.alm.keybinding;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class LoginKeybinding {
    private static KeyMapping loginKeyMapping;

    public static void register() {
        loginKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "alm.key.autocmd.send",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                KeyMapping.Category.MISC
        ));
    }

    public static KeyMapping get() {
        return loginKeyMapping;
    }
}
