package de.maxhenkel.voicechat.voice.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.maxhenkel.voicechat.Main;
import de.maxhenkel.voicechat.gui.VoiceChatScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientVoiceEvents {

    // private static final ResourceLocation MICROPHONE_ICON = new ResourceLocation(Main.MODID, "textures/gui/microphone.png");
    // private static final ResourceLocation SPEAKER_ICON = new ResourceLocation(Main.MODID, "textures/gui/speaker.png");

    private Client client;
    private Minecraft minecraft;

    public ClientVoiceEvents() {
        minecraft = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void joinEvent(EntityJoinWorldEvent event) {
        if (event.getEntity() != minecraft.player) {
            return;
        }
        if (client != null) {
            return;
        }
        ServerData serverData = minecraft.getCurrentServer();
        if (serverData != null) {
            try {
                Main.LOGGER.info("Connecting to server: '" + serverData.ip + ":" + Main.SERVER_CONFIG.voiceChatPort.get() + "'");
                client = new Client(serverData.ip, Main.SERVER_CONFIG.voiceChatPort.get());
                client.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @SubscribeEvent
    public void joinEvent(WorldEvent.Unload event) {
        // Not just changing the world - Disconnecting
        if (minecraft.getConnection() == null) {
            if (client != null) {
                client.close();
                client = null;
            }
        }
    }

    public Client getClient() {
        return client;
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Pre event) {
        if (!event.getType().equals(RenderGameOverlayEvent.ElementType.HOTBAR)) {
            return;
        }

        if (client == null || !client.isConnected() || client.getMicThread() == null) {
            return;
        }

        if (!client.getMicThread().isTalking()) {
            return;
        }

        return;
        /*
        RenderSystem.pushMatrix();

        minecraft.getTextureManager().bindTexture(MICROPHONE_ICON);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        //double width = minecraft.getMainWindow().getScaledWidth();
        double height = minecraft.getMainWindow().getScaledHeight();

        buffer.pos(16D, height - 32D, 0D).tex(0F, 0F).endVertex();
        buffer.pos(16D, height - 16D, 0D).tex(0F, 1F).endVertex();
        buffer.pos(32D, height - 16D, 0D).tex(1F, 1F).endVertex();
        buffer.pos(32D, height - 32D, 0D).tex(1F, 0F).endVertex();

        tessellator.draw();

        RenderSystem.popMatrix();
        */
    }

    @SubscribeEvent
    public void onInput(InputEvent.KeyInputEvent event) {
        if (Main.KEY_VOICE_CHAT_SETTINGS.isDown()) {
            if (Main.CLIENT_VOICE_EVENTS.getClient() == null) {
                minecraft.player.displayClientMessage(new TranslationTextComponent("message.voice_chat_unavailable"), true);
            } else {
                minecraft.setScreen(new VoiceChatScreen());
            }
        }

        if (Main.KEY_PTT.isDown()) {
            if (Main.CLIENT_VOICE_EVENTS.getClient() == null) {
                minecraft.player.displayClientMessage(new TranslationTextComponent("message.voice_chat_unavailable"), true);
            }
        }

        // if (Main.KEY_PTT.getKey().getKeyCode() == GLFW.GLFW_KEY_CAPS_LOCK) {} //TODO caps lock disabler
    }

    @SubscribeEvent
    public void renderOverlay(RenderNameplateEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity playerEntity = (PlayerEntity) event.getEntity();
        if (client != null && client.getTalkCache().isTalking(playerEntity)) {
            renderSpeaker(playerEntity, event.getContent().getString(), event.getMatrixStack(), event.getRenderTypeBuffer(), event.getPackedLight());
        }
    }

    protected void renderSpeaker(PlayerEntity player, String displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        return;

        /*
        matrixStackIn.push();
        matrixStackIn.translate(0D, player.getHeight() + 0.5D, 0D);
        matrixStackIn.rotate(minecraft.getRenderManager().getCameraOrientation());
        matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
        matrixStackIn.translate(0D, -1D, 0D);

        float offset = (float) (minecraft.fontRenderer.getStringWidth(displayNameIn) / 2 + 2);


        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getText(SPEAKER_ICON));
        int alpha = 32;

        if (player.isDiscrete()) {
            vertex(builder, matrixStackIn, offset, 10F, 0F, 0F, 1F, alpha, packedLightIn);
            vertex(builder, matrixStackIn, offset + 10F, 10F, 0F, 1F, 1F, alpha, packedLightIn);
            vertex(builder, matrixStackIn, offset + 10F, 0F, 0F, 1F, 0F, alpha, packedLightIn);
            vertex(builder, matrixStackIn, offset, 0F, 0F, 0F, 0F, alpha, packedLightIn);
        } else {
            vertex(builder, matrixStackIn, offset, 10F, 0F, 0F, 1F, packedLightIn);
            vertex(builder, matrixStackIn, offset + 10F, 10F, 0F, 1F, 1F, packedLightIn);
            vertex(builder, matrixStackIn, offset + 10F, 0F, 0F, 1F, 0F, packedLightIn);
            vertex(builder, matrixStackIn, offset, 0F, 0F, 0F, 0F, packedLightIn);

            IVertexBuilder builderSeeThrough = bufferIn.getBuffer(RenderType.getTextSeeThrough(SPEAKER_ICON));
            vertex(builderSeeThrough, matrixStackIn, offset, 10F, 0F, 0F, 1F, alpha, packedLightIn);
            vertex(builderSeeThrough, matrixStackIn, offset + 10F, 10F, 0F, 1F, 1F, alpha, packedLightIn);
            vertex(builderSeeThrough, matrixStackIn, offset + 10F, 0F, 0F, 1F, 0F, alpha, packedLightIn);
            vertex(builderSeeThrough, matrixStackIn, offset, 0F, 0F, 0F, 0F, alpha, packedLightIn);
        }

        matrixStackIn.pop();
        */
    }

    private static void vertex(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, float u, float v, int light) {
        vertex(builder, matrixStack, x, y, z, u, v, 255, light);
    }

    private static void vertex(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, float u, float v, int alpha, int light) {
        MatrixStack.Entry entry = matrixStack.last();
        builder.vertex(entry.pose(), x, y, z)
                .color(255, 255, 255, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(entry.normal(), 0F, 0F, -1F)
                .endVertex();
    }

}
