package co.com.sofka.gameddd.useCase;

import co.com.sofka.business.annotation.EventListener;
import co.com.sofka.business.generic.UseCase;
import co.com.sofka.business.support.ResponseEvents;
import co.com.sofka.business.support.TriggeredEvent;
import co.com.sofka.gameddd.Juego.Juego;
import co.com.sofka.gameddd.Jugadores.events.TurnoJugado;

import java.util.logging.Level;
import java.util.logging.Logger;

@EventListener(eventType = "gameddd.Jugadores.TurnoJugado")
public class VerificarRecorridoFinalizadoUseCase extends UseCase<TriggeredEvent<TurnoJugado>, ResponseEvents> {

    private static final Logger logger = Logger.getLogger(VerificarRecorridoFinalizadoUseCase.class.getName());

    @Override
    public void executeUseCase(TriggeredEvent<TurnoJugado> turnoJugadoTriggeredEvent) {
        var event = turnoJugadoTriggeredEvent.getDomainEvent();

        var juego = Juego.from(event.getIdJuego(),retrieveEvents());
        juego.agregarMetaPodium(event.getMeta(),event.getPodium());

        if(event.getDistanciaFinal().value() >= juego.getMeta().value() && juego.verificarPodiumDisponible()){
            juego.agregarAlPodium(event.getNombreConductor());
            juego.obtenerPodiumActualizado();
            logger.log(Level.INFO, "El carro finalizó");
        }
        emit().onSuccess(new ResponseEvents(juego.getUncommittedChanges()));
    }
}
