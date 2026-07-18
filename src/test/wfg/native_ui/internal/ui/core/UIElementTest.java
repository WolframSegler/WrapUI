package test.wfg.native_ui.internal.ui.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.FaderUtil;

import wfg.native_ui.internal.ui.core.UIElement;
import wfg.native_ui.ui.core.UIElementAPI;
import wfg.native_ui.ui.event.UIEventBus;

public final class UIElementTest {

    private static MockedStatic<com.fs.starfarer.api.Global> globalMock;
    private static MockedStatic<UIEventBus> eventBusMock;
    private static SettingsAPI mockSettings;

    static {
        try {
            // 1. Create mock SettingsAPI
            mockSettings = mock(SettingsAPI.class);
            when(mockSettings.getFloat("uiFadeSpeedMult")).thenReturn(1.0f);

            // 2. Mock Global.getSettings() – Globals will use this when it loads
            globalMock = mockStatic(com.fs.starfarer.api.Global.class);
            globalMock.when(com.fs.starfarer.api.Global::getSettings).thenReturn(mockSettings);

            // 3. Force Globals to load – its static init now sees the mocked Global
            Class.forName("wfg.native_ui.util.Globals");

            // 4. Force UIElement to load – its static init now sees mockSettings via Globals
            Class.forName("wfg.native_ui.internal.ui.core.UIElement");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialise test environment", e);
        }
    }

    @BeforeAll
    static void mockEventBus() {
        // UIEventBus can be mocked after UIElement is safely loaded
        eventBusMock = mockStatic(UIEventBus.class);
    }

    @AfterAll
    static void releaseStatics() {
        if (eventBusMock != null) eventBusMock.close();
        if (globalMock != null) globalMock.close();
    }

    // --- Instance fields (no UIElement here – safe) ---
    private UIElementAPI element;
    private PositionAPI mockPos;
    private UIPanelAPI mockParent;
    private FaderUtil mockFader;
    private InputEventAPI mockEvent;


    @BeforeEach
    void setUp() throws Exception {
        eventBusMock.reset();

        MockitoAnnotations.openMocks(this);

        mockPos = mock(PositionAPI.class);
        mockParent = mock(UIPanelAPI.class);
        mockFader = mock(FaderUtil.class);
        mockEvent = mock(InputEventAPI.class);

        when(mockPos.getX()).thenReturn(10f);
        when(mockPos.getY()).thenReturn(20f);
        when(mockPos.getWidth()).thenReturn(100f);
        when(mockPos.getHeight()).thenReturn(50f);
        when(mockPos.getCenterX()).thenReturn(60f);
        when(mockPos.getCenterY()).thenReturn(45f);
        when(mockParent.getPosition()).thenReturn(mockPos);

        element = new UIElement(mockPos);

        Field faderField = UIElement.class.getDeclaredField("mFader");
        faderField.setAccessible(true);
        faderField.set(element, mockFader);

        when(mockFader.isFadedOut()).thenReturn(false);
        when(mockFader.getBrightness()).thenReturn(1f);
        element.setOpacity(1f);
    }

    @Test
    @Disabled("Requires commented-out constructor code to be active (settings.createPosition())")
    void constructorWithWidthHeightShouldSetSizeOnPosition() {
        final PositionAPI customPos = mock(PositionAPI.class);
        when(customPos.setSize(200f, 100f)).thenReturn(customPos);
        final UIElement el = new UIElement(200f, 100f);

        // TODO Write test after the commented code is active and mPos is properly initialised
    }

    @Test
    void gettersShouldDelegateToPosition() {
        assertEquals(10f, element.getX());
        assertEquals(20f, element.getY());
        assertEquals(60f, element.getCenterX());
        assertEquals(45f, element.getCenterY());
        assertEquals(100f, element.getWidth());
        assertEquals(50f, element.getHeight());
        assertEquals(mockPos, element.pos());
        assertEquals(mockPos, element.getPosition());
    }

    @Test
    void setPosShouldReplacePosition() {
        final PositionAPI newPos = mock(PositionAPI.class);
        element.setPos(newPos);
        assertEquals(newPos, element.getPosition());
    }

    @Test
    void setWidthShouldUpdateWidthOnly() {
        element.setWidth(200f);
        verify(mockPos).setSize(200f, 50f);
    }

    @Test
    void setHeightShouldUpdateHeightOnly() {
        element.setHeight(120f);
        verify(mockPos).setSize(100f, 120f);
    }

    @Test
    void setSizeShouldReturnPositionAndUpdateBoth() {
        when(mockPos.setSize(300f, 400f)).thenReturn(mockPos);
        final PositionAPI result = element.setSize(300f, 400f);
        assertEquals(mockPos, result);
        verify(mockPos).setSize(300f, 400f);
    }

    @Test
    void moveBy_withNoOffset_shouldMoveExactlyByDelta() {
        when(mockPos.getX()).thenReturn(10f, 10f); // before reset, after reset
        when(mockPos.getY()).thenReturn(20f, 20f);

        element.moveBy(5f, -3f);
        verify(mockPos).setXAlignOffset(5f);
        verify(mockPos).setYAlignOffset(-3f);
    }

    @Test
    void moveBy_withPositiveOffset_shouldMoveExactlyByDelta() {
        when(mockPos.getX()).thenReturn(15f, 10f);
        when(mockPos.getY()).thenReturn(25f, 20f);
        element.moveBy(2f, 1f); // should move to 17,26

        // new offsets must be oldOffset+dx = 5+2=7, 5+1=6
        verify(mockPos).setXAlignOffset(7f);
        verify(mockPos).setYAlignOffset(6f);
    }

    @Test
    void moveBy_withNegativeOffset_shouldMoveExactlyByDelta() {
        when(mockPos.getX()).thenReturn(5f, 10f);
        when(mockPos.getY()).thenReturn(15f, 20f);
        element.moveBy(2f, 2f);   // should move to 7,17
        // new offsets: -5+2=-3, -5+2=-3
        verify(mockPos).setXAlignOffset(-3f);
        verify(mockPos).setYAlignOffset(-3f);
    }

    @Test
    void resizeByShouldIncreaseDimensions() {
        element.resizeBy(30f, -10f);
        verify(mockPos).setSize(130f, 40f); // width 100+30, height 50-10
    }

    // TODO activate after update
    // @Test
    // void setParentShouldStoreReferenceAndSetParentOnPosition() {
    //     element.setParent(mockParent);
    //     assertEquals(mockParent, element.getParent());
    //     verify(mockPos).setParent(mockParent.getPosition());
    // }

    @Test
    void getParentShouldReturnNullInitially() {
        assertNull(element.getParent());
    }

    @Test
    void bringToFrontShouldDelegateToParent() {
        element.setParent(mockParent);
        element.bringToFront();
        verify(mockParent).bringComponentToTop(element);
    }

    @Test
    void bringToFrontWithNoParentShouldNotCrash() {
        element.bringToFront();
        verifyNoInteractions(mockParent);
    }

    @Test
    void sendToBackShouldDelegateToParent() {
        element.setParent(mockParent);
        element.sendToBack();
        verify(mockParent).sendToBottom(element);
    }

    @Test
    void detachShouldRemoveFromParentAndFireEvent() {
        element.setParent(mockParent);
        element.detach();
        verify(mockParent).removeComponent(element);
        eventBusMock.verify(() -> UIEventBus.fireDetached(element));
    }

    @Test
    void detachWithoutParentShouldNotFireEvent() {
        element.detach();
        verify(mockParent, never()).removeComponent(any());
        eventBusMock.verify(() -> UIEventBus.fireDetached(any()), never());
    }

    @Test
    void setAndGetOpacity() {
        element.setOpacity(0.5f);
        assertEquals(0.5f, element.getOpacity());
    }

    @Test
    void renderShouldCallRenderImplWithScaledAlpha() {
        element.render(0.8f);
        // alpha * brightness = 0.8 * 1.0 = 0.8
        
        final UIElementAPI spyElement = spy(element);
        spyElement.render(0.8f);
        verify(spyElement).renderImpl(0.8f);
    }

    @Test
    void renderShouldScaleAlphaByFaderBrightness() {
        when(mockFader.getBrightness()).thenReturn(0.5f);
        final UIElementAPI spyElement = spy(element);
        spyElement.render(0.8f);
        verify(spyElement).renderImpl(0.4f); // 0.8 * 0.5
    }

    @Test
    void renderShouldSkipWhenAlphaZeroOrNegative() {
        final UIElementAPI spyElement = spy(element);
        spyElement.render(0f);
        verify(spyElement, never()).renderImpl(anyFloat());

        spyElement.render(-0.1f);
        verify(spyElement, never()).renderImpl(anyFloat());
    }

    @Test
    void processInputShouldDelegateWhenVisible() {
        final UIElementAPI spyElement = spy(element);
        List<InputEventAPI> events = new ArrayList<>();
        spyElement.processInput(events);
        verify(spyElement).processInputImpl(events);
    }

    @Test
    void processInputShouldSkipWhenFadedOut() {
        when(mockFader.isFadedOut()).thenReturn(true);
        final UIElementAPI spyElement = spy(element);
        spyElement.processInput(List.of(mockEvent));
        verify(spyElement, never()).processInputImpl(any());
    }

    @Test
    void processInputShouldSkipWhenOpacityZero() {
        element.setOpacity(0f);
        final UIElementAPI spyElement = spy(element);
        spyElement.processInput(List.of(mockEvent));
        verify(spyElement, never()).processInputImpl(any());
    }

    @Test
    void advanceShouldAdvanceFaderAndCallImpl() {
        final UIElementAPI spyElement = spy(element);
        spyElement.advance(0.1f);
        verify(mockFader).advance(0.1f); // delta * FADE_SPEED_MULT
        verify(spyElement).advanceImpl(0.1f);
    }

    @Test
    void reportAttachedShouldFireEvent() {
        element.reportAttached();
        eventBusMock.verify(() -> UIEventBus.fireAttached(element));
    }

    @Test
    void reportDetachedShouldFireEvent() {
        element.reportDetached();
        eventBusMock.verify(() -> UIEventBus.fireDetached(element));
    }
}