package co.com.sofka.gameddd.Juego.events;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.Juego.values.Podium;

public class PodiumActualizado extends DomainEvent {

    private Podium podium;

    public PodiumActualizado(Podium podium) {
        super("gameddd.Juego.events.PodiumActualizado");
        this.podium = podium;
    }

    public Podium getPodium() {
        return podium;
    }
}
