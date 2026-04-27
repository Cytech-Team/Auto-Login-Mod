package cloud.sect.alm.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import cloud.sect.alm.gui.AlmConfigScreen;

public class AlmModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new AlmConfigScreen(parent);
    }
}
