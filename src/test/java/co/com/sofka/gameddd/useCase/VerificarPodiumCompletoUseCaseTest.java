package co.com.sofka.gameddd.useCase;

import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.support.TriggeredEvent;
import co.com.sofka.gameddd.Juego.events.JuegoCreado;
import co.com.sofka.gameddd.Juego.events.JuegoFinalizado;
import co.com.sofka.gameddd.Juego.events.PodiumActualizado;
import co.com.sofka.gameddd.Juego.values.IdJuego;
import co.com.sofka.gameddd.Juego.values.Meta;
import co.com.sofka.gameddd.Juego.values.Podium;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class VerificarPodiumCompletoUseCaseTest extends  UseCaseHandleBaseTest{

    @Test
    void verificarPodiumCompletoTest_casoFeliz(){
        VerificarPodiumCompletoUseCase useCase = new VerificarPodiumCompletoUseCase();

        PodiumActualizado podiumActualizado = new PodiumActualizado(
                new Podium("Alejandra","Zuleta","González"),
                IdJuego.of("1")
        );

        when(repository.getEventsBy(anyString())).thenReturn(List.of(
                new JuegoCreado(IdJuego.of("1"),new Meta(1000),new Podium())
        ));

        useCase.addRepository(repository);

        UseCaseHandler.getInstance()
                .setIdentifyExecutor("1")
                .asyncExecutor(useCase, new TriggeredEvent<>(podiumActualizado))
                .subscribe(subscriber);

        verify(subscriber, times(1)).onNext(eventCaptor.capture());
        //JuegoFinalizado juegoFinalizado = (JuegoFinalizado) eventCaptor.getAllValues().get(0);
    }
}