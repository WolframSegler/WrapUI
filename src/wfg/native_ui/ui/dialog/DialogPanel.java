package wfg.native_ui.ui.dialog;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import static wfg.native_ui.util.Globals.settings;
import static wfg.native_ui.util.UIConstants.*;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.PositionAPI;

import wfg.native_ui.internal.ui.dialog.FoldingPanel;
import wfg.native_ui.internal.ui.dialog.ModalDialog;
import wfg.native_ui.ui.core.UIBuildableAPI;
import wfg.native_ui.ui.functional.Button;
import wfg.native_ui.ui.functional.Button.CutStyle;
import wfg.native_ui.util.CallbackRunnable;
import wfg.native_ui.util.RunnableWithCode;

/**
<p>
A modal, fold-animated dialog panel with a built-in <em>holo</em> ({@link FoldingPanel}) frame.

<p><strong>Important implementation notes</strong></p>
<ul>
<li><strong>Ownership:</strong> {@link DialogPanel#m_panel} is owned and positioned by {@link DialogPanel#holo}.
Do <em>not</em> assign {@link DialogPanel#m_panel} to any other parent. Use {@link FoldingPanel#setNext()} instead.</li>
<li><strong>Buttons:</strong> Buttons map to integer options stored in {@link DialogPanel#buttons}.</li>
</ul>

<p><strong>Typical usage</strong></p>
<pre><code>
final DialogPanel dlg = new DialogPanel(
    (code) -&gt; {...},
    "Confirm action", // default text
    "Confirm", "Cancel" // button texts (0 = Confirm, 1 = Cancel)
);

// optionally set keyboard shortcut for the "confirm" button (index 0)
dlg.setConfirmShortcut();

// show with fade in / out durations (seconds)
dlg.show(0.5f, 0.5f);
</code></pre>

</ul>

<p><strong>Subclass example</strong></p>
<pre><code>public class MyConfirmDialog extends DialogPanel {
    public MyConfirmDialog(RunnableWithCode done) {
        super(420, 220, done);
    }

    &#64;Override
    public void buildUI() {
        final LabelAPI text2 = Global.getSettings().createLabel(
            "Extra info", Fonts.INSIGNIA_LARGE
        );
        add(text2).inTL(opad, 50);
    }

    &#64;Override
    public void outsideClickAbsorbed(InputEventAPI e) {
        getHolo().flickerNoise(0f, 0.5f);
    }
}
</code></pre>
*/
public class DialogPanel extends ModalDialog implements UIBuildableAPI, CallbackRunnable<Button> {
    public boolean noiseOnConfirmDismiss = true;
    public final FoldingPanel holo;
    protected final ArrayList<Button> buttons = new ArrayList<>(4);

    public DialogPanel(int w, int h, RunnableWithCode onDismissed) {
        super(w, h + BUTTON_H + pad + opad, onDismissed);

        holo = new FoldingPanel(w, h + BUTTON_H + pad + opad,
            UI_BORDER_1, 7
        );

        holo.getPos().inMid();
        holo.forceFoldIn();

        holo.transitionEnabled = false;
        holo.setNext(m_panel);
    }

    public DialogPanel(RunnableWithCode onDismissed, String txt, String... btnText) {
        this(500, 200, onDismissed, txt, btnText);
    }

    public DialogPanel(int w, int h, RunnableWithCode onDismissed,
        String txt, String... btnText
    ) { this(w, h, text_color, btnBgColorDark, onDismissed, txt, btnText); }

    public DialogPanel(int w, int h, Color btnTxtColor, Color btnBgColor,
        RunnableWithCode onDismissed, String txt, String... btnTextArr
    ) {
        this(w, h, onDismissed);

        if (txt != null && !txt.equals("")) {
            final LabelAPI txtLbl = settings.createLabel(
                txt, Fonts.INSIGNIA_LARGE
            );
            add(txtLbl);
            txtLbl.setColor(btnTxtColor);
            txtLbl.getPosition().setSize(
                pos.getWidth(), pos.getHeight() - BUTTON_H
            ).inTL(0f, 0f);
            txtLbl.setAlignment(Alignment.TL);
        }

        if (btnTextArr != null && btnTextArr.length > 0) {
            for(int i = 0; i < btnTextArr.length; i++) {
                final String BtnTxt = btnTextArr[i];
                if (BtnTxt == null) continue;
    
                final Button btn = new Button(m_panel, BUTTON_W, BUTTON_H, BtnTxt,
                    Fonts.ORBITRON_20AA, this
                );
                btn.setAlignment(Alignment.MID);
                btn.cutStyle = CutStyle.TL_BR;
                btn.setQuickMode(true);
                btn.customData = Integer.valueOf(i);
                buttons.add(btn);
                add(btn);
            }

            for (int i = buttons.size() - 1; i >= 0; i--) {
                final Button btn = buttons.get(i);

                if (i == buttons.size() - 1) btn.getPos().inBR(pad, pad);
                else btn.getPos().leftOfMid(buttons.get(i + 1).getPanel(), pad*2);
            }
        }
    }

    /** Override */
    public void buildUI() {}

    public PositionAPI setSize(float w, float h) {
        holo.setSize(w, h);
        return pos.setSize(w, h);
    }

    public PositionAPI sizeToInner(float w, float h) {
        return setSize(w + holo.borderThickness * 3, h + holo.borderThickness * 3);
    }

    public void setConfirmShortcut() {
        buttons.get(0).setShortcutAndAppendToText(Keyboard.KEY_G);
    }

    public void run(Button btn) {
        dismiss((Integer) btn.customData);
    }

    @Override
    public void dismiss(int option) {
        holo.foldIn(fader.getDurationOut() * 0.5f);
        if (noiseOnConfirmDismiss || option != 0) {
            holo.flickerNoise(0f, 1f);
        }

        super.dismiss(option);
    }

    public void show(float durIn, float durOut) {
        super.show(durIn, durOut);

        holo.getParent().bringComponentToTop(holo.getPanel());
        holo.foldOut(fader.getDurationIn() * 0.5f);
        holo.flickerNoise(0f, 1f);
    }

    @Override
    public void outsideClickAbsorbed(InputEventAPI event) {
        holo.flickerNoise(0f, 0.5f);
    }

    public final Button getButton(int id) {
        return buttons.get(id);
    }
}