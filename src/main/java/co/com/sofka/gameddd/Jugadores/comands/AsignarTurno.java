package co.com.sofka.gameddd.Jugadores.comands;

import co.com.sofka.domain.generic.Command;
import co.com.sofka.gameddd.Juego.values.IdJuego;
import co.com.sofka.gameddd.Juego.values.Meta;
import co.com.sofka.gameddd.Juego.values.Podium;
import co.com.sofka.gameddd.Jugadores.entities.Conductor;
import co.com.sofka.gameddd.Jugadores.values.IdJugadores;

public class AsignarTurno implements Command {
    private IdJugadores idJugadores;
    private Conductor conductorEnTurno;
    private IdJuego idJuego;
    private final Meta meta;
    private final Podium podium;

    public AsignarTurno(IdJugadores idJugadores,Conductor conductorEnTurno, IdJuego idJuego, Meta meta, Podium podium) {
        this.conductorEnTurno = conductorEnTurno;
        this.idJuego = idJuego;
        this.meta = meta;
        this.podium = podium;
        this.idJugadores = idJugadores;
    }

    public Conductor getConductorEnTurno() {
        return conductorEnTurno;
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

    public IdJugadores getIdJugadores() {
        return idJugadores;
    }
}
