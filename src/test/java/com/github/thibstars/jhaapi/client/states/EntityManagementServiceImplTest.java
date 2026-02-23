package com.github.thibstars.jhaapi.client.states;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.states.response.State;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

class EntityManagementServiceImplTest {

    private Configuration configuration;
    private StatesService statesService;
    private EntityManagementServiceImpl entityManagementService;

    @BeforeEach
    void setUp() {
        configuration = Mockito.mock(Configuration.class);
        statesService = Mockito.mock(StatesService.class);
        entityManagementService = new EntityManagementServiceImpl(configuration, statesService);
        
        Mockito.when(configuration.getEntityId()).thenReturn("switch.jhaapi_application");
        Mockito.when(configuration.getEntityFriendlyName()).thenReturn("JHAAPI Application");
        Mockito.when(configuration.isEntityReadOnly()).thenReturn(false);
        Mockito.when(configuration.isEntityShutdownEnabled()).thenReturn(false);
    }

    @Test
    void shouldInitializeWhenEnabledAndEntityDoesNotExist() {
        Mockito.when(configuration.isEntityEnabled()).thenReturn(true);
        Mockito.when(statesService.getState("switch.jhaapi_application")).thenReturn(Optional.empty());

        entityManagementService.initialize();

        Mockito.verify(statesService).updateState("switch.jhaapi_application", "off", "JHAAPI Application", false);
        Mockito.verify(statesService).updateState("switch.jhaapi_application", "on", "JHAAPI Application", false);
    }

    @Test
    void shouldUpdateStateWhenEnabledAndEntityExists() {
        Mockito.when(configuration.isEntityEnabled()).thenReturn(true);
        Mockito.when(statesService.getState("switch.jhaapi_application")).thenReturn(Optional.of(Mockito.mock(State.class)));

        entityManagementService.initialize();

        // Should NOT create (off), but SHOULD turn on
        Mockito.verify(statesService, Mockito.never()).updateState(ArgumentMatchers.eq("switch.jhaapi_application"), ArgumentMatchers.eq("off"), ArgumentMatchers.eq("JHAAPI Application"), ArgumentMatchers.anyBoolean());
        Mockito.verify(statesService).updateState("switch.jhaapi_application", "on", "JHAAPI Application", false);
    }

    @Test
    void shouldNotInitializeWhenDisabled() {
        Mockito.when(configuration.isEntityEnabled()).thenReturn(false);

        entityManagementService.initialize();

        Mockito.verify(statesService, Mockito.never()).getState(Mockito.anyString());
        Mockito.verify(statesService, Mockito.never()).updateState(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
    }

    @Test
    void shouldTurnOn() {
        Mockito.when(configuration.isEntityEnabled()).thenReturn(true);
        
        entityManagementService.turnOn();

        Mockito.verify(statesService).updateState("switch.jhaapi_application", "on", "JHAAPI Application", false);
    }

    @Test
    void shouldTurnOff() {
        Mockito.when(configuration.isEntityEnabled()).thenReturn(true);

        entityManagementService.turnOff();

        Mockito.verify(statesService).updateState("switch.jhaapi_application", "off", "JHAAPI Application", false);
    }
    
    @Test
    void shouldNotTurnOnWhenDisabled() {
        Mockito.when(configuration.isEntityEnabled()).thenReturn(false);

        entityManagementService.turnOn();

        Mockito.verify(statesService, Mockito.never()).updateState(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
    }
}
