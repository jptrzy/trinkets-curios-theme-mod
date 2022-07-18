package net.jptrzy.trinkets.curios.theme.debug;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.jptrzy.trinkets.curios.theme.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Objects;

public class ImGuiManager {

    long windowPtr = -1;

    private final ImGuiImplGlfw implGlfw = new ImGuiImplGlfw();

    private final ImGuiImplGl3 implGl3 = new ImGuiImplGl3();

    private final ImGuiIO io;

    public ImGuiManager(){
        windowPtr = MinecraftClient.getInstance().getWindow().getHandle();
        ImGui.createContext();
        io = ImGui.getIO();
        implGlfw.init(windowPtr, false);
        implGl3.init("#version 330");
    }

    public boolean charTyped(char chr, int keyCode) {
        if (io.getWantTextInput()) {
            io.addInputCharacter(chr);
        }
        return true;
    }
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (io.getWantCaptureKeyboard()) {
            if (io.getKeysDown(keyCode)) {
                io.setKeysDown(new boolean[]{ true });
            }
        }
        return true;
    }
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (io.getWantCaptureKeyboard()) {
            if (io.getKeysDown(keyCode)) {
                io.setKeysDown(new boolean[] { false });
            }
        }
        return true;
    }

    public void close() {
        implGl3.dispose();
        implGlfw.dispose();
    }

    ImBoolean always_update = new ImBoolean(ModConfig.always_update);

    ImBoolean scrollbar = new ImBoolean(ModConfig.scrollbar);
    ImBoolean scrolling_outside_boundary = new ImBoolean(ModConfig.scrolling_outside_boundary);

    ImInt max_height = new ImInt(ModConfig.max_height);
    ImInt min_width = new ImInt(ModConfig.min_width);

    ImBoolean scout_auto_resize = new ImBoolean(ModConfig.scout_auto_resize);

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // TODO Auto-generated method stub
        implGlfw.newFrame();
        ImGui.newFrame();

        ImGui.begin("Trinkets Curios Theme Options");

        if (ImGui.inputInt("max_height", max_height)) {
            ModConfig.max_height = max_height.get();
        }
        if (ImGui.inputInt("min_width", min_width)) {
            ModConfig.min_width = min_width.get();
        }

        ImGui.end();

        ImGui.render();

        implGl3.updateFontsTexture();
        implGl3.renderDrawData(Objects.requireNonNull(ImGui.getDrawData()));

    }
}
