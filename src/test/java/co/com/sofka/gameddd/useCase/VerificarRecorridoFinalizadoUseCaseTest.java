package co.com.sofka.gameddd.useCase;

import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.support.TriggeredEvent;
import co.com.sofka.gameddd.Juego.events.JuegoCreado;
import co.com.sofka.gameddd.Juego.values.IdJuego;
import co.com.sofka.gameddd.Juego.values.Meta;
import co.com.sofka.gameddd.Juego.values.Podium;
import co.com.sofka.gameddd.Jugadores.events.TurnoJugado;
import co.com.sofka.gameddd.Jugadores.values.IdConductor;
import co.com.sofka.gameddd.Jugadores.values.Recorrido;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class VerificarRecorridoFinalizadoUseCaseTest extends  UseCaseHandleBaseTest{

    @Test
    void verificarRecorridoFinalizadoTest_casoFeliz(){
        VerificarRecorridoFinalizadoUseCase useCase = new VerificarRecorridoFinalizadoUseCase();

        TurnoJugado turnoJugadoEvent = new TurnoJugado(
                new IdConductor("123456"),
                "Alejandra",
                new Recorrido(300),
                IdJuego.of("1"),
                new Meta(300),
                new Podium());

        when(repository.getEventsBy(anyString())).thenReturn(List.of(
                new JuegoCreado(IdJuego.of("1"),new Meta(300),new Podium())
        ));

        useCase.addRepository(repository);

        UseCaseHandler.getInstance()
                .setIdentifyExecutor("1")
                .asyncExecutor(useCase, new TriggeredEvent<>(turnoJugadoEvent))
                .subscribe(subscriber);

        verify(subscriber, times(3)).onNext(eventCaptor.capture());
    }
}