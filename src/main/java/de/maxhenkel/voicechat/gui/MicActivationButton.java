package de.maxhenkel.voicechat.gui;

import de.maxhenkel.voicechat.Main;
import de.maxhenkel.voicechat.voice.client.MicrophoneActivationType;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.text.TranslationTextComponent;

public class MicActivationButton extends AbstractButton {

    private MicrophoneActivationType type;
    private VoiceActivationSlider voiceActivationSlider;

    public MicActivationButton(int xIn, int yIn, int widthIn, int heightIn, VoiceActivationSlider voiceActivationSlider) {
        super(xIn, yIn, widthIn, heightIn, null);
        this.voiceActivationSlider = voiceActivationSlider;
        type = Main.CLIENT_CONFIG.microphoneActivationType.get();
        updateText();
    }

    private void updateText() {
        if (type.equals(MicrophoneActivationType.PTT)) {
            setMessage(new TranslationTextComponent("message.activation_type", new TranslationTextComponent("message.activation_type.ptt")));
            voiceActivationSlider.visible = false;
        } else if (type.equals(MicrophoneActivationType.VOICE)) {
            setMessage(new TranslationTextComponent("message.activation_type", new TranslationTextComponent("message.activation_type.voice")));
            voiceActivationSlider.visible = true;
        }
    }

    @Override
    public void onPress() {
        type = MicrophoneActivationType.values()[(type.ordinal() + 1) % MicrophoneActivationType.values().length];
        Main.CLIENT_CONFIG.microphoneActivationType.set(type);
        Main.CLIENT_CONFIG.microphoneActivationType.save();
        updateText();
    }
}
