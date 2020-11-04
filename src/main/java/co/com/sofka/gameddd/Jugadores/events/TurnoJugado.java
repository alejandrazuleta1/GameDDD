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
    private final Recorrido distanciaInicial;
    private final IdJuego idJuego;
    private final Meta meta;
    private final Podium podium;
    private Recorrido distanciaFinal;

    public TurnoJugado(IdConductor idConductor, String nombreConductor, Recorrido distanciaInicial, IdJuego idJuego, Meta meta, Podium podium) {
        super("gameddd.Jugadores.TurnoJugado");
        this.idConductor = idConductor;
        this.distanciaInicial = distanciaInicial;
        this.nombreConductor = nombreConductor;
        this.idJuego = idJuego;
        this.meta = meta;
        this.podium = podium;
    }

    public TurnoJugado(IdConductor idConductor, String nombreConductor, Recorrido distanciaInicial, Recorrido distanciaFinal,IdJuego idJuego, Meta meta, Podium podium) {
        super("gameddd.Jugadores.TurnoJugado");
        this.idConductor = idConductor;
        this.distanciaInicial = distanciaInicial;
        this.distanciaFinal = distanciaFinal;
        this.nombreConductor = nombreConductor;
        this.idJuego = idJuego;
        this.meta = meta;
        this.podium = podium;
    }


    public IdConductor getIdConductor() {
        return idConductor;
    }

    public Recorrido getDistanciaInicial() {
        return distanciaInicial;
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

    public Recorrido getDistanciaFinal() {
        return distanciaFinal;
    }

    public void setDistanciaFinal(Recorrido distanciaFinal) {
        this.distanciaFinal = distanciaFinal;
    }
}
