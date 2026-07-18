package test.wfg.native_ui.internal.ui.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import org.mockito.*;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.internal.ui.core.UIElement;
import wfg.native_ui.ui.core.UIElementAPI;
import wfg.native_ui.ui.event.IdentifiedPanel;
import wfg.native_ui.ui.event.UIEventBus;
import wfg.native_ui.ui.system.*;

public class UIContainerTest {

    // ---- Static mocks for environment ----
    private static MockedStatic<Global> globalMock;
    private static MockedStatic<UIEventBus> eventBusMock;

    // System factory mocks (same as UIEntityTest)
    private static MockedStatic<BackgroundSystem> bgMock;
    private static MockedStatic<HoverGlowSystem> hoverMock;
    private static MockedStatic<DebugBgSystem> debugMock;
    private static MockedStatic<OutlineSystem> outlineMock;
    private static MockedStatic<AudioFeedbackSystem> audioMock;
    private static MockedStatic<RawInputSystem> rawInputMock;
    private static MockedStatic<InteractionSystem> interactionMock;
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

            // Create system mocks
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

            // Load core classes (no UIConstants)
            Class.forName("wfg.native_ui.util.Globals");
            Class.forName("wfg.native_ui.internal.ui.core.UIElement");
            Class.forName("wfg.native_ui.internal.ui.core.UIEntity");
            Class.forName("wfg.native_ui.internal.ui.core.UIContainer");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialise test environment", e);
        }
    }

    // Inside UIContainerTest class
    private UIElement createElement() {
        final PositionAPI pos = mock(PositionAPI.class);
        return new UIElement(pos);
    }

    @BeforeAll
    static void mockStatics() {
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
    void resetMocks() {
        eventBusMock.reset();
    }

    // ---- Instance fields ----
    private UIContainer container;
    private PositionAPI mockContainerPos;
    private UIPanelAPI mockParent;

    @BeforeEach
    void setUp() {
        mockContainerPos = mock(PositionAPI.class);
        when(mockContainerPos.getX()).thenReturn(0f);
        when(mockContainerPos.getY()).thenReturn(0f);
        when(mockContainerPos.getWidth()).thenReturn(200f);
        when(mockContainerPos.getHeight()).thenReturn(100f);
        container = new UIContainer(mockContainerPos);

        mockParent = mock(UIPanelAPI.class);
    }

    // ---- add(UIComponentAPI) ----
    @Test
    void addShouldInsertAndReturnPosition() {
        UIComponentAPI child = mock(UIComponentAPI.class);
        PositionAPI childPos = mock(PositionAPI.class);
        when(child.getPosition()).thenReturn(childPos);

        PositionAPI result = container.add(child);

        assertSame(childPos, result);
        assertTrue(container.getChildren().contains(child));
    }

    @Test
    void addDuplicateShouldNotAddAgain() {
        UIComponentAPI child = mock(UIComponentAPI.class);
        when(child.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(child);
        container.add(child);
        assertEquals(1, container.getChildren().size());
    }

    @Test
    void addShouldFireAttachedForUIElement() {
        UIElement element = createElement();
        container.add(element);
        eventBusMock.verify(() -> UIEventBus.fireAttached(element));
    }

    @Test
    void removeShouldDeleteChildAndFireDetached() {
        UIElement element = createElement();
        container.add(element);
        container.remove(element);
        assertFalse(container.getChildren().contains(element));
        eventBusMock.verify(() -> UIEventBus.fireDetached(element));
    }

    @Test
    void clearChildrenRemovesAllAndCallsDetach() {
        UIElement el1 = createElement();
        UIElement el2 = createElement();
        container.add(el1);
        container.add(el2);

        container.clearChildren();

        assertTrue(container.getChildren().isEmpty());
        eventBusMock.verify(() -> UIEventBus.fireDetached(el1));
        eventBusMock.verify(() -> UIEventBus.fireDetached(el2));
    }

    @Test
    void addShouldNotFireAttachedForPlainComponent() {
        UIComponentAPI comp = mock(UIComponentAPI.class);
        when(comp.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(comp);
        eventBusMock.verify(() -> UIEventBus.fireAttached(any()), never());
    }

    @Test
    @Disabled("Requires mPos.add / setParent, which is not yet available")
    void addShouldAddToPositionAndSetParent() {
        // TODO placeholder
    }

    @Test
    void removeNonExistentShouldDoNothing() {
        UIComponentAPI child = mock(UIComponentAPI.class);
        when(child.getPosition()).thenReturn(mock(PositionAPI.class));
        container.remove(child); // no exception
        eventBusMock.verify(() -> UIEventBus.fireDetached(any()), never());
    }

    @Test
    @Disabled("Requires mPos.remove / setParent(null)")
    void removeShouldDetachPosition() {
        // TODO placeholder
    }

    // ---- getChildren / getChildrenCopy / clearChildren ----
    @Test
    void getChildrenReturnsInternalList() {
        UIComponentAPI child = mock(UIComponentAPI.class);
        when(child.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(child);
        assertSame(container.getChildren().get(0), child);
    }

    @Test
    void getChildrenCopyIsIndependent() {
        UIComponentAPI child = mock(UIComponentAPI.class);
        when(child.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(child);
        List<UIComponentAPI> copy = container.getChildrenCopy();
        assertEquals(1, copy.size());
        copy.clear();
        assertEquals(1, container.getChildren().size()); // original unchanged
    }

    // ---- bringToTop ----
    @Test
    void bringToTopMovesChildToEndAndNotifiesParent() {
        UIComponentAPI child = mock(UIComponentAPI.class);
        when(child.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(child);
        container.setParent(mockParent);

        container.bringToTop(child);

        assertEquals(child, container.getChildren().get(container.getChildren().size()-1));
        verify(mockParent).bringComponentToTop(container);
    }

    @Test
    void bringToTopWithoutParent() {
        UIComponentAPI child = mock(UIComponentAPI.class);
        when(child.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(child);
        container.bringToTop(child);
        assertEquals(child, container.getChildren().get(container.getChildren().size()-1));
        verify(mockParent, never()).bringComponentToTop(any());
    }

    @Test
    void bringToTopUnknownChildDoesNothing() {
        UIComponentAPI child = mock(UIComponentAPI.class);
        when(child.getPosition()).thenReturn(mock(PositionAPI.class));
        container.bringToTop(child);
        // no exception, no change
    }

    // ---- bringToTopWithinItself ----
    @Test
    void bringToTopWithinItselfMovesToEndButDoesNotNotifyParent() {
        container.setParent(mockParent);
        UIComponentAPI child = mock(UIComponentAPI.class);
        when(child.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(child);
        container.bringToTopWithinItself(child);
        assertEquals(child, container.getChildren().get(container.getChildren().size()-1));
        verify(mockParent, never()).bringComponentToTop(any());
    }

    // ---- sendToBottom ----
    @Test
    void sendToBottomMovesToFrontAndCallsSendToBack() {
        container.setParent(mockParent);
        UIComponentAPI child = mock(UIComponentAPI.class);
        when(child.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(child);

        container.sendToBottom(child);

        assertEquals(child, container.getChildren().get(0));
        verify(mockParent).sendToBottom(container);
    }

    @Test
    void sendToBottomWithoutParent() {
        container.setParent(null);
        UIComponentAPI child = mock(UIComponentAPI.class);
        when(child.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(child);
        container.sendToBottom(child);
        assertEquals(child, container.getChildren().get(0));
    }

    // ---- sendToBottomWithinItself ----
    @Test
    void sendToBottomWithinItselfMovesToFrontNoParent() {
        container.setParent(mockParent);
        UIComponentAPI child = mock(UIComponentAPI.class);
        when(child.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(child);
        container.sendToBottomWithinItself(child);
        assertEquals(child, container.getChildren().get(0));
        verify(mockParent, never()).sendToBottom(any());
    }

    // ---- getChild by class ----
    @Test
    void getChildByClassReturnsFirstMatch() {
        UIComponentAPI a = mock(UIComponentAPI.class);
        UIElementAPI b = mock(UIElementAPI.class);
        when(a.getPosition()).thenReturn(mock(PositionAPI.class));
        when(b.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(a);
        container.add(b);

        assertSame(a, container.getChild(UIComponentAPI.class));
        assertSame(b, container.getChild(UIElementAPI.class));
        assertNull(container.getChild(TooltipMakerAPI.class)); // none
    }

    // ---- getChild by panelId ----
    @Test
    void getChildByPanelIdReturnsMatchingIdentifiedPanel() {
        UIComponentAPI regular = mock(UIComponentAPI.class);
        UIComponentAPI identified = mock(UIComponentAPI.class, withSettings().extraInterfaces(IdentifiedPanel.class));
        when(regular.getPosition()).thenReturn(mock(PositionAPI.class));
        when(identified.getPosition()).thenReturn(mock(PositionAPI.class));
        when(((IdentifiedPanel) identified).getPanelId()).thenReturn("foo");

        container.add(regular);
        container.add(identified);

        assertNull(container.getChild("bar"));
        assertEquals(identified, container.getChild("foo"));
        assertNull(container.getChild((String) null)); // disambiguate null
    }

    // ---- advanceImpl delegation to children ----
    @Test
    void advanceImplAdvancesChildren() {
        UIComponentAPI c1 = mock(UIComponentAPI.class);
        UIComponentAPI c2 = mock(UIComponentAPI.class);
        when(c1.getPosition()).thenReturn(mock(PositionAPI.class));
        when(c2.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(c1);
        container.add(c2);

        container.advanceImpl(0.1f);

        verify(c1).advance(0.1f);
        verify(c2).advance(0.1f);
    }

    // ---- processInputImpl delegation to children ----
    @Test
    void processInputImplForwardsEventsToChildren() {
        UIComponentAPI c1 = mock(UIComponentAPI.class);
        UIComponentAPI c2 = mock(UIComponentAPI.class);
        when(c1.getPosition()).thenReturn(mock(PositionAPI.class));
        when(c2.getPosition()).thenReturn(mock(PositionAPI.class));
        container.add(c1);
        container.add(c2);
        List<InputEventAPI> events = List.of(mock(InputEventAPI.class));

        container.processInputImpl(events);

        verify(c1).processInput(events);
        verify(c2).processInput(events);
    }

    // ---- renderImpl ordering ----
    @Test
    void renderImplCallsSystemsAndChildrenInOrder() {
        // Setup mock systems
        BaseSystem sys1 = mock(BaseSystem.class);
        BaseSystem sys2 = mock(BaseSystem.class);
        UISystemContainer mockSysContainer = mock(UISystemContainer.class);
        when(mockSysContainer.getAll()).thenReturn(List.of(sys1, sys2));
        // Inject mock container into container (spy to verify system calls)
        UIContainer spyContainer = spy(container);
        doReturn(mockSysContainer).when(spyContainer).system();

        UIComponentAPI child = mock(UIComponentAPI.class);
        when(child.getPosition()).thenReturn(mock(PositionAPI.class));
        spyContainer.add(child);

        spyContainer.renderImpl(0.5f);

        InOrder inOrder = inOrder(sys1, sys2, spyContainer, child);
        inOrder.verify(sys1).renderBelow(spyContainer, 0.5f);
        inOrder.verify(sys2).renderBelow(spyContainer, 0.5f);
        inOrder.verify(spyContainer).renderBelowImpl(0.5f);
        inOrder.verify(child).render(0.5f);
        inOrder.verify(sys1).renderAbove(spyContainer, 0.5f);
        inOrder.verify(sys2).renderAbove(spyContainer, 0.5f);
        inOrder.verify(spyContainer).renderAboveImpl(0.5f);
    }

    @Test
    @Disabled("Requires mPos.addChild / setParent")
    void addPosShouldAddPosition() {
        // TODO placeholder
    }

    @Test
    @Disabled("Requires mPos.removeChild")
    void removePosShouldRemovePosition() {
        // TODO placeholder
    }
}