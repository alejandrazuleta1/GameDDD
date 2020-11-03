package co.com.sofka.gameddd.useCase;

import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.gameddd.Jugadores.comands.AlistarJugadores;
import co.com.sofka.gameddd.Jugadores.entities.Carro;
import co.com.sofka.gameddd.Jugadores.entities.Conductor;
import co.com.sofka.gameddd.Jugadores.events.JugadoresCreado;
import co.com.sofka.gameddd.Jugadores.useCase.AlistarJugadoresUseCase;
import co.com.sofka.gameddd.Jugadores.values.IdCarro;
import co.com.sofka.gameddd.Juego.values.IdCompetencia;
import co.com.sofka.gameddd.Jugadores.values.IdConductor;
import co.com.sofka.gameddd.Jugadores.values.Recorrido;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AlistarJugadoresUseCaseTest extends  UseCaseHandleBaseTest {

    @Test
    void alistarJugadoresTest(){
        List<Conductor> conductores = new ArrayList<>();
        IdCompetencia idCompetencia = new IdCompetencia("1");
        conductores.add(new Conductor(
                new IdConductor("123456"),
                "Alejandra",
                new Carro(
                        new IdCarro("udz567"),
                        new Recorrido(0))
        ));
        conductores.add(new Conductor(
                new IdConductor("1234567"),
                "Alezandra",
                new Carro(
                        new IdCarro("udz547"),
                        new Recorrido(0))
        ));

        AlistarJugadoresUseCase useCase = new AlistarJugadoresUseCase();
        AlistarJugadores alistarJugadores = new AlistarJugadores(conductores,idCompetencia);
        UseCaseHandler.getInstance().asyncExecutor(useCase, new RequestCommand<>(alistarJugadores)).subscribe(subscriber);
        verify(subscriber,times(3)).onNext(eventCaptor.capture());

        JugadoresCreado jugadoresCreado = (JugadoresCreado) eventCaptor.getAllValues().get(0);
        System.out.println(jugadoresCreado.getIdCompetencia().value());

        Assertions.assertEquals("1",jugadoresCreado.getIdCompetencia().value());
        Assertions.assertEquals(3,eventCaptor.getAllValues().size());
    }

}