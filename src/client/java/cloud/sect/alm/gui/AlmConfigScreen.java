package cloud.sect.alm.gui;

import cloud.sect.alm.config.ServerConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AlmConfigScreen extends Screen {
    private final Screen parent;
    private EditBox inputField;
    private String statusMessage = "";
    private int messageTimer = 0;

    public AlmConfigScreen(Screen parent) {
        super(Component.literal("§6§lAuto-Login §r§7- §fSecure Console"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (ServerConfig.isLocked() || !ServerConfig.hasMasterPassword()) {
            initSecurityScreen(centerX, centerY);
        } else {
            initMainScreen(centerX, centerY);
        }
    }

    private void initSecurityScreen(int centerX, int centerY) {
        String title = ServerConfig.hasMasterPassword() ? "§c§lDATABASE LOCKED" : "§6§lSET MASTER PASSWORD";
        String prompt = ServerConfig.hasMasterPassword() ? "Enter Password to Decrypt:" : "Create Master Password (Required):";
        
        this.inputField = new EditBox(this.font, centerX - 100, centerY - 10, 200, 20, Component.literal("Password"));
        this.inputField.setResponder(s -> {});
        this.addRenderableWidget(this.inputField);

        String btnText = ServerConfig.hasMasterPassword() ? "§aUnlock" : "§6Set & Initialize";
        this.addRenderableWidget(Button.builder(Component.literal(btnText), button -> {
            String pass = this.inputField.getValue();
            if (pass.isEmpty()) {
                showStatus("§cPassword cannot be empty!", 60);
                return;
            }
            
            if (ServerConfig.hasMasterPassword()) {
                if (ServerConfig.unlock(pass)) {
                    showStatus("§aUnlocked!", 40);
                    this.init(); // Refresh to main screen
                } else {
                    showStatus("§cInvalid Password!", 60);
                }
            } else {
                if (ServerConfig.setMasterPassword(pass)) {
                    showStatus("§aMaster Password Set!", 60);
                    this.init();
                }
            }
        }).bounds(centerX - 100, centerY + 20, 200, 20).build());
    }

    private void initMainScreen(int centerX, int centerY) {
        // Global Password Edit
        this.inputField = new EditBox(this.font, centerX - 100, centerY - 45, 200, 20, Component.literal("Global Pass"));
        this.inputField.setMaxLength(64);
        this.inputField.setValue(ServerConfig.getGlobalPassword());
        this.addRenderableWidget(this.inputField);

        this.addRenderableWidget(Button.builder(Component.literal("§a✔ §fSave Password"), button -> {
            ServerConfig.setGlobalPassword(this.inputField.getValue());
            showStatus("§aGlobal Password Updated!", 40);
        }).bounds(centerX - 100, centerY - 20, 200, 20).build());

        // Toggles
        this.addRenderableWidget(Button.builder(Component.literal("Auto-Type: " + getStatus(ServerConfig.isAutoTypeEnabled())), button -> {
            ServerConfig.setAutoTypeEnabled(!ServerConfig.isAutoTypeEnabled());
            button.setMessage(Component.literal("Auto-Type: " + getStatus(ServerConfig.isAutoTypeEnabled())));
        }).bounds(centerX - 100, centerY + 10, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Smart-Detection: " + getStatus(ServerConfig.isSmartModeEnabled())), button -> {
            ServerConfig.setSmartModeEnabled(!ServerConfig.isSmartModeEnabled());
            button.setMessage(Component.literal("Smart-Detection: " + getStatus(ServerConfig.isSmartModeEnabled())));
        }).bounds(centerX - 100, centerY + 40, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("§c✖ §fClose"), button -> {
            this.onClose();
        }).bounds(centerX - 100, centerY + 80, 200, 20).build());
    }

    private String getStatus(boolean b) { return b ? "§aON" : "§cOFF"; }

    private void showStatus(String msg, int ticks) {
        this.statusMessage = msg;
        this.messageTimer = ticks;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        ServerConfig.updateActivity(); // Stay unlocked while GUI is open
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        
        context.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (ServerConfig.isLocked() || !ServerConfig.hasMasterPassword()) {
            String prompt = ServerConfig.hasMasterPassword() ? "§7Enter Master Password:" : "§6Set a Master Password to protect your data:";
            context.drawCenteredString(this.font, prompt, centerX, centerY - 30, 0xFFFFFF);
        } else {
            context.drawString(this.font, "§7Global Password:", centerX - 100, centerY - 58, 0xFFFFFF);
            
            String delayInfo = "§8Distribution: Gaussian (Gaussian/Normal)";
            context.drawCenteredString(this.font, delayInfo, centerX, centerY + 65, 0xAAAAAA);
            context.drawCenteredString(this.font, "§8Auto-locks after 5m of inactivity", centerX, centerY + 105, 0x555555);
        }

        if (messageTimer > 0) {
            context.drawCenteredString(this.font, statusMessage, centerX, this.height - 40, 0xFFFFFF);
            messageTimer--;
        }
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xDD000000); // Very dark
        context.fill(this.width / 2 - 120, 10, this.width / 2 + 120, this.height - 10, 0x44FFFFFF); // Border
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.parent);
    }
}
