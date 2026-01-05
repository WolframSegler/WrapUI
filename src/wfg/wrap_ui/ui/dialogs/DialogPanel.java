package wfg.wrap_ui.ui.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import static wfg.wrap_ui.util.UIConstants.*;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

import wfg.wrap_ui.internal.ui.dialogs.FoldingPanel;
import wfg.wrap_ui.internal.ui.dialogs.ModalDialog;
import wfg.wrap_ui.ui.panels.Button;
import wfg.wrap_ui.ui.panels.Button.CutStyle;
import wfg.wrap_ui.util.CallbackRunnable;
import wfg.wrap_ui.util.RunnableWithCode;

public class DialogPanel extends ModalDialog implements CallbackRunnable<Button> {
    public boolean noiseOnConfirmDismiss = true;
    public LabelAPI titleLbl;

    private FoldingPanel holo;
    private Map<Button, Integer> optionsMap = new HashMap<>();

    public DialogPanel(UIPanelAPI parent, int width, int height, RunnableWithCode runnable) {
        super(parent, width, height, runnable);
        getPlugin().init(this);

        holo = new FoldingPanel(width, height);

        createPanel();
    }

    public DialogPanel(UIPanelAPI parent, RunnableWithCode runnable, String title, String... btnText) {
        this(500, 200, parent, runnable, title, btnText);
    }

    public DialogPanel(int w, int h, UIPanelAPI parent, RunnableWithCode runnable,
        String title, String... btnText
    ) {
        this(w, h, btnTxtColor, btnBgColorDark, parent, runnable, title, btnText);
    }

    public DialogPanel(int w, int h, Color btnTxtColor, Color btnBgColor, UIPanelAPI parent,
        RunnableWithCode runnable, String title, String... btnTextArr
    ) {
        this(parent, w, h, runnable);

        final UIPanelAPI innerComp = getInnerUIPanel();
        final PositionAPI innerPos = innerComp.getPosition();
        final int btnW = 150;
        final int btnH = 25;

        titleLbl = Global.getSettings().createLabel(title, "graphics/fonts/insignia21LTaa.fnt");
        innerComp.addComponent((UIComponentAPI)titleLbl);
        titleLbl.setColor(btnTxtColor);
        titleLbl.getPosition().setSize(
            innerPos.getWidth() - opad*2,
            innerPos.getHeight() - btnH - opad*2
        ).inTL(opad, opad);
        titleLbl.setAlignment(Alignment.TL);
        final String font = "graphics/fonts/orbitron20aa.fnt";

        Button prevBtn = null;
        for(int i = btnTextArr.length - 1; i >= 0; i--) {
            final String BtnTxt = btnTextArr[i];
            if (BtnTxt == null) continue;

            final Button btn = new Button(innerComp, btnW, btnH, BtnTxt, font, this);
            btn.setAlignment(Alignment.MID);
            btn.setCutStyle(CutStyle.TL_BR);
            btn.quickMode = true;
            optionsMap.put(btn, i);
            innerComp.addComponent(btn.getPanel());
            if (prevBtn == null) {
                btn.getPos().inBR(opad, opad);
            } else {
                btn.getPos().leftOfMid(prevBtn.getPanel(), opad);
            }

            prevBtn = btn;
        }
    }

    public void createPanel() {
        add(holo);
        holo.getPos().inMid();
        holo.forceFoldIn();

        UIComponentAPI innerPanel = holo.getPanel().createCustomPanel(
            0f, 0f, null
        );

        holo.transitionEnabled = false;
        holo.setNext(innerPanel);
    }

    public PositionAPI setSize(float w, float h) {
        holo.setSize(w, h);
        return getPos().setSize(w, h);
    }

    public PositionAPI sizeToInner(float w, float h) {
        return setSize(w + holo.borderThickness * 2f, h + holo.borderThickness * 2f);
    }

    public void setConfirmShortcut() {
        optionsMap.keySet().forEach(b -> {
            if (optionsMap.get(b) == 0) { b.setShortcut(Keyboard.KEY_G);}
        });

    }

    public void dismiss(int var1) {
        float var2 = fader.getDurationOut() * 0.5f;
        holo.foldIn(var2);
        if (noiseOnConfirmDismiss || var1 != 0) {
            holo.flickerNoise(0f, 1.25f);
        }

        super.dismiss(var1);
    }

    public void show(float var1, float var2) {
        super.show(var1, var2);
        float var3 = fader.getDurationIn() * 0.5f;
        holo.foldOut(var3);
        holo.flickerNoise(0.0F, 1.25F);
    }

    protected void outsideClickAbsorbed() {
        holo.flickerNoise(0f, 0.5f);
    }

    public void run(Button btn) {
        final Integer optionValue = optionsMap.get(btn);
        if (optionValue == null) dismiss(1);
        else dismiss(optionValue);
    }

    public Button getButton(int id) {
        for (Map.Entry<Button, Integer> entry : optionsMap.entrySet()) {
            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }
        return null;
    }

    public UIComponentAPI getInnerPanel() { return holo.getCurr();}
    public UIPanelAPI getInnerUIPanel() { return (UIPanelAPI) holo.getCurr();}

    public FoldingPanel getHolo() { return holo;}

    public Map<Button, Integer> getOptionMap() { return optionsMap;}
}