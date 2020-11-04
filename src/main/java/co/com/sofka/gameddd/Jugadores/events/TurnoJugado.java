package co.com.sofka.gameddd.Jugadores.events;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.Juego.values.IdJuego;
import co.com.sofka.gameddd.Juego.values.Meta;
import co.com.sofka.gameddd.Juego.values.Podium;
import co.com.sofka.gameddd.Jugadores.values.IdConductor;
import co.com.sofka.gameddd.Jugadores.values.Recorrido;

public class TurnoJugado extends DomainEvent {
    private final IdConductor idConductor;
    private final String nombreConductor;
    private final Recorrido distanciaTotal;
    private final IdJuego idJuego;
    private final Meta meta;
    private final Podium podium;

    public TurnoJugado(IdConductor idConductor, String nombreConductor, Recorrido distanciaTotal, IdJuego idJuego, Meta meta, Podium podium) {
        super("gameddd.Jugadores.TurnoJugado");
        this.idConductor = idConductor;
        this.distanciaTotal = distanciaTotal;
        this.nombreConductor = nombreConductor;
        this.idJuego = idJuego;
        this.meta = meta;
        this.podium = podium;
    }

    public IdConductor getIdConductor() {
        return idConductor;
    }

    public Recorrido getDistanciaTotal() {
        return distanciaTotal;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public IdJuego getIdJuego() {
        return idJuego;
    }

    public Meta getMeta() {
        return meta;
    }

    public Podium getPodium() {
        return podium;
    }
}
