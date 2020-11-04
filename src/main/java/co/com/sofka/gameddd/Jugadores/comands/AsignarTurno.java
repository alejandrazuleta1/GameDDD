package co.com.sofka.gameddd.Jugadores.comands;

import co.com.sofka.domain.generic.Command;
import co.com.sofka.gameddd.Juego.values.IdJuego;
import co.com.sofka.gameddd.Juego.values.Meta;
import co.com.sofka.gameddd.Juego.values.Podium;
import co.com.sofka.gameddd.Jugadores.entities.Conductor;

public class AsignarTurno implements Command {
    private Conductor conductorEnTurno;
    private IdJuego idJuego;
    private final Meta meta;
    private final Podium podium;

    public AsignarTurno(Conductor conductorEnTurno, IdJuego idJuego, Meta meta, Podium podium) {
        this.conductorEnTurno = conductorEnTurno;
        this.idJuego = idJuego;
        this.meta = meta;
        this.podium = podium;
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
}
