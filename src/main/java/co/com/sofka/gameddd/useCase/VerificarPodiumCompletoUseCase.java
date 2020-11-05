package co.com.sofka.gameddd.useCase;

import co.com.sofka.business.annotation.EventListener;
import co.com.sofka.business.generic.UseCase;
import co.com.sofka.business.support.ResponseEvents;
import co.com.sofka.business.support.TriggeredEvent;
import co.com.sofka.gameddd.Juego.Juego;
import co.com.sofka.gameddd.Juego.events.PodiumActualizado;

import java.util.logging.Level;
import java.util.logging.Logger;

@EventListener(eventType = "gameddd.Juego.events.PodiumActualizado")
public class VerificarPodiumCompletoUseCase extends UseCase<TriggeredEvent<PodiumActualizado>, ResponseEvents> {

    private static final Logger logger = Logger.getLogger(VerificarPodiumCompletoUseCase.class.getName());

    @Override
    public void executeUseCase(TriggeredEvent<PodiumActualizado> podiumActualizadoTriggeredEvent) {
        var event = podiumActualizadoTriggeredEvent.getDomainEvent();

        var juego = Juego.from(event.getIdJuego(),retrieveEvents());

        var podiumCompleto = (event.getPodium().value().primerLugar() != null) &&
                (event.getPodium().value().segundoLugar() != null) &&
                (event.getPodium().value().tercerLugar() != null);
        if (podiumCompleto) {
            juego.finalizar();
            logger.log(Level.INFO, "El juego finalizó");
        }
        emit().onSuccess(new ResponseEvents(juego.getUncommittedChanges()));
    }
}
