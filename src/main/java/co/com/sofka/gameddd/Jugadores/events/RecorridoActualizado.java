package co.com.sofka.gameddd.Jugadores.events;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.Jugadores.values.Recorrido;

public class RecorridoActualizado extends DomainEvent {
    private Recorrido recorrido;

    public RecorridoActualizado(Recorrido recorrido) {
        super("gameddd.Jugadores.events.RecorridoActualizado");
        this.recorrido = recorrido;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }
}
