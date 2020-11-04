package co.com.sofka.gameddd.Jugadores.events;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.Jugadores.entities.Conductor;
import co.com.sofka.gameddd.Jugadores.values.IdConductor;

public class JugadorEnTurnoAsignado extends DomainEvent {
    private final Conductor conductor;

    public JugadorEnTurnoAsignado(Conductor conductor) {
        super("gameddd.Jugadores.events.JugadorEnTurnoAsignado");
        this.conductor = conductor;
    }

    public Conductor getConductor() {
        return conductor;
    }
}
