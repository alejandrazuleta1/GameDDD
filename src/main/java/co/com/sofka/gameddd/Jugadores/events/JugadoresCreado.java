package co.com.sofka.gameddd.Jugadores.events;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.Jugadores.values.IdJugadores;

public class JugadoresCreado  extends DomainEvent {

    private final IdJugadores idJugadores;

    public JugadoresCreado(IdJugadores idJugadores) {
        super("gameddd.Jugadores.events.JugadoresCreado");
        this.idJugadores = idJugadores;
    }

    public IdJugadores getIdJugadores() {
        return idJugadores;
    }
}
