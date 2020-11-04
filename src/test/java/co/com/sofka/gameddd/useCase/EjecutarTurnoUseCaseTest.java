package co.com.sofka.gameddd.useCase;

import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.gameddd.Juego.Juego;
import co.com.sofka.gameddd.Juego.values.IdJuego;
import co.com.sofka.gameddd.Juego.values.Meta;
import co.com.sofka.gameddd.Juego.values.Podium;
import co.com.sofka.gameddd.Jugadores.comands.AsignarTurno;
import co.com.sofka.gameddd.Jugadores.entities.Carro;
import co.com.sofka.gameddd.Jugadores.entities.Conductor;
import co.com.sofka.gameddd.Jugadores.events.TurnoJugado;
import co.com.sofka.gameddd.Jugadores.values.IdCarro;
import co.com.sofka.gameddd.Jugadores.values.IdConductor;
import co.com.sofka.gameddd.Jugadores.values.Recorrido;
import org.junit.jupiter.api.Test;

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

        EjecutarTurnoUseCase useCase = new EjecutarTurnoUseCase();
        AsignarTurno asignarTurno = new AsignarTurno(conductor, juego.idJuego(), juego.getMeta(), juego.getPodium());

        UseCaseHandler.getInstance()
                .asyncExecutor(
                        useCase,
                        new RequestCommand<>(asignarTurno))
                .subscribe(subscriber);

        verify(subscriber,times(3)).onNext(eventCaptor.capture());
        TurnoJugado turnoJugado = (TurnoJugado) eventCaptor.getAllValues().get(2);
        System.out.println(turnoJugado.getNombreConductor() + turnoJugado.getDistanciaTotal().value());
    }
}