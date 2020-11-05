package co.com.sofka.gameddd.useCase;

import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.gameddd.Juego.commands.IniciarJuego;
import co.com.sofka.gameddd.Juego.events.PistaAsignada;
import co.com.sofka.gameddd.Juego.values.*;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CrearJuegoUseCaseTest extends  UseCaseHandleBaseTest {

    @Test
    void iniciarJuegoTest(){
        Pista pista = new Pista(new IdPista("Ciudad"));
        IniciarJuego iniciarJuego = new IniciarJuego(new IdJuego("1"),new Meta(300), new Podium(),pista);
        CrearJuegoUseCase useCase = new CrearJuegoUseCase();
        UseCaseHandler.getInstance()
                .asyncExecutor(
                        useCase,
                        new RequestCommand<>(iniciarJuego))
                .subscribe(subscriber);

        verify(subscriber,times(2)).onNext(eventCaptor.capture());
        PistaAsignada pistaAsignada = (PistaAsignada) eventCaptor.getAllValues().get(1);
        System.out.println(pistaAsignada.getPista().value());
    }
}