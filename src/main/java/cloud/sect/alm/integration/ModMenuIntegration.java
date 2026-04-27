package cloud.sect.alm.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import cloud.sect.alm.gui.AlmConfigScreen;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // ใช้ Lambda เพื่อไม่ให้โหลด AlmConfigScreen ทันทีตอนเริ่มเกม
        return parent -> new AlmConfigScreen(parent);
    }
}
