package co.com.sofka.gameddd.Juego.events;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.Juego.values.IdJuego;
import co.com.sofka.gameddd.Juego.values.Podium;

public class PodiumActualizado extends DomainEvent {

    private Podium podium;
    private IdJuego idJuego;

    public PodiumActualizado(Podium podium, IdJuego idJuego) {
        super("gameddd.Juego.events.PodiumActualizado");
        this.podium = podium;
        this.idJuego = idJuego;
    }

    public Podium getPodium() {
        return podium;
    }

    public IdJuego getIdJuego() {
        return idJuego;
    }
}
