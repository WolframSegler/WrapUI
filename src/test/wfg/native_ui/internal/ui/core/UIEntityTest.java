package test.wfg.native_ui.internal.ui.core;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.mockito.*;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;

import wfg.native_ui.internal.ui.core.UIElement;
import wfg.native_ui.internal.ui.core.UIEntity;
import wfg.native_ui.ui.core.UIElementFlags;
import wfg.native_ui.ui.component.UIComponentContainer;
import wfg.native_ui.ui.system.*;
import wfg.native_ui.ui.event.UIEventBus;

public class UIEntityTest {

    private static MockedStatic<Global> globalMock;
    private static MockedStatic<UIEventBus> eventBusMock;

    // System factory mocks
    private static MockedStatic<BackgroundSystem> bgMock;
    private static MockedStatic<HoverGlowSystem> hoverMock;
    private static MockedStatic<DebugBgSystem> debugMock;
    private static MockedStatic<OutlineSystem> outlineMock;
    private static MockedStatic<AudioFeedbackSystem> audioMock;
    private static MockedStatic<RawInputSystem> rawInputMock;
    private static MockedStatic<InteractionSystem> interactionMock;

    // The mocked system instances returned by get()
    private static BackgroundSystem mockBackground;
    private static HoverGlowSystem mockHoverGlow;
    private static DebugBgSystem mockDebugBg;
    private static OutlineSystem mockOutline;
    private static AudioFeedbackSystem mockAudioFeedback;
    private static RawInputSystem mockRawInput;
    private static InteractionSystem mockInteraction;

    private static SettingsAPI mockSettings;

    static {
        try {
            mockSettings = mock(SettingsAPI.class);
            when(mockSettings.getFloat("uiFadeSpeedMult")).thenReturn(1.0f);
            when(mockSettings.getScreenWidth()).thenReturn(1920f);
            when(mockSettings.getScreenHeight()).thenReturn(1080f);
            when(mockSettings.getScreenScaleMult()).thenReturn(1.0f);

            globalMock = mockStatic(Global.class);
            globalMock.when(Global::getSettings).thenReturn(mockSettings);

            mockBackground = mock(BackgroundSystem.class);
            mockHoverGlow = mock(HoverGlowSystem.class);
            mockDebugBg = mock(DebugBgSystem.class);
            mockOutline = mock(OutlineSystem.class);
            mockAudioFeedback = mock(AudioFeedbackSystem.class);
            mockRawInput = mock(RawInputSystem.class);
            mockInteraction = mock(InteractionSystem.class);

            bgMock = mockStatic(BackgroundSystem.class);
            bgMock.when(BackgroundSystem::get).thenReturn(mockBackground);

            hoverMock = mockStatic(HoverGlowSystem.class);
            hoverMock.when(HoverGlowSystem::get).thenReturn(mockHoverGlow);

            debugMock = mockStatic(DebugBgSystem.class);
            debugMock.when(DebugBgSystem::get).thenReturn(mockDebugBg);

            outlineMock = mockStatic(OutlineSystem.class);
            outlineMock.when(OutlineSystem::get).thenReturn(mockOutline);

            audioMock = mockStatic(AudioFeedbackSystem.class);
            audioMock.when(AudioFeedbackSystem::get).thenReturn(mockAudioFeedback);

            rawInputMock = mockStatic(RawInputSystem.class);
            rawInputMock.when(RawInputSystem::get).thenReturn(mockRawInput);

            interactionMock = mockStatic(InteractionSystem.class);
            interactionMock.when(InteractionSystem::get).thenReturn(mockInteraction);

            Class.forName("wfg.native_ui.util.Globals");
            Class.forName("wfg.native_ui.internal.ui.core.UIElement");
            Class.forName("wfg.native_ui.internal.ui.core.UIEntity");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialise test environment", e);
        }
    }

    @BeforeAll
    static void mockEventBus() {
        eventBusMock = mockStatic(UIEventBus.class);
    }

    @AfterAll
    static void releaseStatics() {
        eventBusMock.close();
        globalMock.close();
        bgMock.close();
        hoverMock.close();
        debugMock.close();
        outlineMock.close();
        audioMock.close();
        rawInputMock.close();
        interactionMock.close();
    }

    @BeforeEach
    void resetStatics() {
        eventBusMock.reset();
    }

    @Test
    void compAndSystemShouldBeLazyAndReturnSameInstance() {
        UIEntity entity = new UIEntity();
        UIComponentContainer comp1 = entity.comp();
        UIComponentContainer comp2 = entity.getUIComponentContainer();
        assertNotNull(comp1);
        assertSame(comp1, comp2);

        UISystemContainer sys1 = entity.system();
        UISystemContainer sys2 = entity.getUISystemContainer();
        assertNotNull(sys1);
        assertSame(sys1, sys2);

        assertSame(comp1, entity.comp());
        assertSame(sys1, entity.system());
    }

    static class EntityWithBg extends UIEntity implements UIElementFlags.HasBackground {}
    static class EntityWithHover extends UIEntity implements UIElementFlags.HasHoverGlow {}
    static class EntityWithDebug extends UIEntity implements UIElementFlags.HasDebugBg {}
    static class EntityWithOutline extends UIEntity implements UIElementFlags.HasOutline {}
    static class EntityWithAudio extends UIEntity implements UIElementFlags.HasAudioFeedback {}
    static class EntityWithInput extends UIEntity implements UIElementFlags.HasInputSnapshot {}
    static class EntityWithInteraction extends UIEntity implements UIElementFlags.HasInteraction {}
    static class EntityWithBgHoverOutline extends UIEntity
            implements UIElementFlags.HasBackground, UIElementFlags.HasHoverGlow, UIElementFlags.HasOutline {}

    @Test
    void initSystemsShouldRegisterBackgroundSystem() {
        UIEntity entity = new EntityWithBg();
        assertSame(mockBackground, entity.system().get(NativeSystems.BACKGROUND));
    }

    @Test
    void initSystemsShouldRegisterHoverGlowSystem() {
        UIEntity entity = new EntityWithHover();
        assertSame(mockHoverGlow, entity.system().get(NativeSystems.HOVER_GLOW));
    }

    @Test
    void initSystemsShouldRegisterDebugBgSystem() {
        UIEntity entity = new EntityWithDebug();
        assertSame(mockDebugBg, entity.system().get(NativeSystems.DEBUG_BG));
    }

    @Test
    void initSystemsShouldRegisterOutlineSystem() {
        UIEntity entity = new EntityWithOutline();
        assertSame(mockOutline, entity.system().get(NativeSystems.OUTLINE));
    }

    @Test
    void initSystemsShouldRegisterAudioFeedbackSystem() {
        UIEntity entity = new EntityWithAudio();
        assertSame(mockAudioFeedback, entity.system().get(NativeSystems.AUDIO_FEEDBACK));
    }

    @Test
    void initSystemsShouldRegisterRawInputSystem() {
        UIEntity entity = new EntityWithInput();
        assertSame(mockRawInput, entity.system().get(NativeSystems.INPUT_SNAPSHOT));
    }

    @Test
    void initSystemsShouldRegisterInteractionSystem() {
        UIEntity entity = new EntityWithInteraction();
        assertSame(mockInteraction, entity.system().get(NativeSystems.INTERACTION));
    }

    @Test
    void initSystemsShouldRegisterMultipleFlags() {
        UIEntity entity = new EntityWithBgHoverOutline();
        assertSame(mockBackground, entity.system().get(NativeSystems.BACKGROUND));
        assertSame(mockHoverGlow, entity.system().get(NativeSystems.HOVER_GLOW));
        assertSame(mockOutline, entity.system().get(NativeSystems.OUTLINE));
        assertNull(entity.system().get(NativeSystems.DEBUG_BG));
        assertNull(entity.system().get(NativeSystems.INPUT_SNAPSHOT));
    }

    @Test
    @Disabled("TooltipSystem triggers loading of incompatible game class")
    void initSystemsShouldRegisterTooltipSystem() {}

    private UIEntity createEntityWithMockSystems(java.util.List<BaseSystem> systems) throws Exception {
        UIEntity entity = new UIEntity();
        UISystemContainer mockContainer = mock(UISystemContainer.class);
        when(mockContainer.getAll()).thenReturn(systems);
        java.lang.reflect.Field containerField = UIEntity.class.getDeclaredField("systemContainer");
        containerField.setAccessible(true);
        containerField.set(entity, mockContainer);
        return entity;
    }

    @Test
    void renderImplShouldCallRenderBelowThenRenderAboveForEachSystem() throws Exception {
        BaseSystem sys1 = mock(BaseSystem.class);
        BaseSystem sys2 = mock(BaseSystem.class);
        UIEntity entity = createEntityWithMockSystems(java.util.List.of(sys1, sys2));

        entity.renderImpl(0.75f);

        InOrder inOrder = inOrder(sys1, sys2);
        inOrder.verify(sys1).renderBelow(entity, 0.75f);
        inOrder.verify(sys2).renderBelow(entity, 0.75f);
        inOrder.verify(sys1).renderAbove(entity, 0.75f);
        inOrder.verify(sys2).renderAbove(entity, 0.75f);
    }

    @Test
    void renderImplShouldHandleEmptySystemList() throws Exception {
        UIEntity entity = createEntityWithMockSystems(java.util.List.of());
        assertDoesNotThrow(() -> entity.renderImpl(0.5f));
    }

    @Test
    void processInputImplShouldCallProcessInputOnEachSystem() throws Exception {
        BaseSystem sys1 = mock(BaseSystem.class);
        BaseSystem sys2 = mock(BaseSystem.class);
        java.util.List<com.fs.starfarer.api.input.InputEventAPI> events = java.util.List.of();
        UIEntity entity = createEntityWithMockSystems(java.util.List.of(sys1, sys2));

        entity.processInputImpl(events);

        verify(sys1).processInput(entity, events);
        verify(sys2).processInput(entity, events);
    }

    @Test
    void processInputImplShouldHandleEmptySystemList() throws Exception {
        UIEntity entity = createEntityWithMockSystems(java.util.List.of());
        assertDoesNotThrow(() -> entity.processInputImpl(java.util.List.of()));
    }

    @Test
    void advanceImplShouldCallAdvanceOnEachSystem() throws Exception {
        BaseSystem sys1 = mock(BaseSystem.class);
        BaseSystem sys2 = mock(BaseSystem.class);
        UIEntity entity = createEntityWithMockSystems(java.util.List.of(sys1, sys2));

        entity.advanceImpl(0.16f);

        verify(sys1).advance(entity, 0.16f);
        verify(sys2).advance(entity, 0.16f);
    }

    @Test
    void advanceImplShouldHandleEmptySystemList() throws Exception {
        UIEntity entity = createEntityWithMockSystems(java.util.List.of());
        assertDoesNotThrow(() -> entity.advanceImpl(0.1f));
    }
}