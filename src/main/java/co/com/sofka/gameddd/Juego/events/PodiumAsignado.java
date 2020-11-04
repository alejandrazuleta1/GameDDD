package co.com.sofka.gameddd.Juego.events;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.Juego.values.Meta;
import co.com.sofka.gameddd.Juego.values.Podium;

public class PodiumAsignado extends DomainEvent {

    private Podium podium;
    public PodiumAsignado(Podium podium) {
        super("gameddd.Juego.PodiumAsignado");
        this.podium = podium;
    }

    public Podium getPodium() {
        return podium;
    }
}
