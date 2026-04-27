package cloud.sect.alm.gui;

import cloud.sect.alm.config.ServerConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class LegacyAlmGui extends GuiScreen {
    private GuiTextField passwordField;
    private String status = "";
    private int statusTimer = 0;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (ServerConfig.isLocked() || !ServerConfig.hasMasterPassword()) {
            // Security Mode
            this.passwordField = new GuiTextField(0, this.fontRendererObj, centerX - 100, centerY - 10, 200, 20);
            this.buttonList.add(new GuiButton(1, centerX - 100, centerY + 20, 200, 20, 
                ServerConfig.hasMasterPassword() ? "Unlock Console" : "Set Master Password"));
        } else {
            // Config Mode
            this.passwordField = new GuiTextField(0, this.fontRendererObj, centerX - 100, centerY - 40, 200, 20);
            this.passwordField.setText(ServerConfig.getGlobalPassword());
            
            this.buttonList.add(new GuiButton(2, centerX - 100, centerY - 10, 200, 20, "Save Global Password"));
            this.buttonList.add(new GuiButton(3, centerX - 100, centerY + 20, 200, 20, "Auto-Type: " + (ServerConfig.isAutoTypeEnabled() ? "ON" : "OFF")));
            this.buttonList.add(new GuiButton(4, centerX - 100, centerY + 90, 200, 20, "Close"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) { // Unlock/Set Master
            String pass = this.passwordField.getText();
            if (ServerConfig.hasMasterPassword()) {
                if (ServerConfig.unlock(pass)) { this.initGui(); } else { status = "Invalid!"; statusTimer = 40; }
            } else {
                ServerConfig.setMasterPassword(pass); this.initGui();
            }
        } else if (button.id == 2) { // Save Global
            ServerConfig.setGlobalPassword(this.passwordField.getText());
            status = "Saved!"; statusTimer = 40;
        } else if (button.id == 3) { // Toggle Auto-Type
            ServerConfig.setAutoTypeEnabled(!ServerConfig.isAutoTypeEnabled());
            button.displayString = "Auto-Type: " + (ServerConfig.isAutoTypeEnabled() ? "ON" : "OFF");
        } else if (button.id == 4) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Auto-Login Secure Console", this.width / 2, 20, 0xFFFFFF);
        
        if (this.passwordField != null) {
            this.passwordField.drawTextBox();
            if (statusTimer > 0) {
                this.drawCenteredString(this.fontRendererObj, status, this.width / 2, this.height - 30, 0xFF5555);
                statusTimer--;
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        ServerConfig.updateActivity();
    }

    @Override
    public void onGuiClosed() { Keyboard.enableRepeatEvents(false); }
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.passwordField.textboxKeyTyped(typedChar, keyCode);
    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.passwordField.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
