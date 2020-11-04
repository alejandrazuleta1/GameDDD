package co.com.sofka.gameddd.useCase;

import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.gameddd.Juego.Juego;
import co.com.sofka.gameddd.Juego.events.JuegoCreado;
import co.com.sofka.gameddd.Juego.values.IdJuego;
import co.com.sofka.gameddd.Juego.values.Meta;
import co.com.sofka.gameddd.Juego.values.Podium;
import co.com.sofka.gameddd.Jugadores.Jugadores;
import co.com.sofka.gameddd.Jugadores.comands.AlistarJugadores;
import co.com.sofka.gameddd.Jugadores.comands.AsignarTurno;
import co.com.sofka.gameddd.Jugadores.entities.Carro;
import co.com.sofka.gameddd.Jugadores.entities.Conductor;
import co.com.sofka.gameddd.Jugadores.events.JugadorAdicionado;
import co.com.sofka.gameddd.Jugadores.events.JugadoresCreado;
import co.com.sofka.gameddd.Jugadores.events.RecorridoActualizado;
import co.com.sofka.gameddd.Jugadores.events.TurnoJugado;
import co.com.sofka.gameddd.Jugadores.values.IdCarro;
import co.com.sofka.gameddd.Jugadores.values.IdConductor;
import co.com.sofka.gameddd.Jugadores.values.IdJugadores;
import co.com.sofka.gameddd.Jugadores.values.Recorrido;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class EjecutarTurnoUseCaseTest extends  UseCaseHandleBaseTest{

    @Test
    void ejecutarTurnoTest(){
        Conductor conductor = new Conductor(
                new IdConductor("123456"),
                "Alejandra",
                new Carro(
                        new IdCarro("UDZ123"),
                        new Recorrido(100)));

        Juego juego = new Juego(
                new IdJuego("1"),
                new Meta(300),
                new Podium()
        );

        Jugadores jugadores = new Jugadores(new IdJugadores(),conductor);

        //AlistarJugadoresUseCase alistarJugadoresUseCase= new AlistarJugadoresUseCase();
        EjecutarTurnoUseCase useCase = new EjecutarTurnoUseCase();

        when(repository.getEventsBy(anyString())).thenReturn(List.of(
                new JugadoresCreado(jugadores.identity()),
                new JugadorAdicionado(conductor.Id(),conductor.Nombre(),conductor.Carro())
        ));

        useCase.addRepository(repository);

        AsignarTurno asignarTurno = new AsignarTurno(jugadores.identity(),conductor, juego.idJuego(), juego.getMeta(), juego.getPodium());

        UseCaseHandler.getInstance()
                .setIdentifyExecutor(jugadores.identity().value())
                .asyncExecutor(
                        useCase,
                        new RequestCommand<>(asignarTurno))
                .subscribe(subscriber);

        verify(subscriber,times(2)).onNext(eventCaptor.capture());
        RecorridoActualizado recorridoActualizado = (RecorridoActualizado) eventCaptor.getAllValues().get(1);
        TurnoJugado turnoJugado = (TurnoJugado) eventCaptor.getAllValues().get(0);
        System.out.println(recorridoActualizado.getRecorrido().value());
        System.out.println(turnoJugado.getDistanciaFinal().value());
    }
}