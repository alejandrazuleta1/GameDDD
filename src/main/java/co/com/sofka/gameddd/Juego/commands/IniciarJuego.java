package co.com.sofka.gameddd.Juego.commands;

import co.com.sofka.domain.generic.Command;
import co.com.sofka.gameddd.Juego.values.IdJuego;
import co.com.sofka.gameddd.Juego.values.Meta;
import co.com.sofka.gameddd.Juego.values.Pista;
import co.com.sofka.gameddd.Juego.values.Podium;

public class IniciarJuego implements Command {
    private final Pista pista;
    private final Meta meta;
    private final Podium podium;
    private final IdJuego idJuego;

    public IniciarJuego( IdJuego idJuego,Meta meta, Podium podium,Pista pista) {
        this.pista = pista;
        this.meta = meta;
        this.podium = podium;
        this.idJuego = idJuego;
    }

    public Pista getPista() {
        return pista;
    }

    public Meta getMeta() {
        return meta;
    }

    public Podium getPodium() {
        return podium;
    }

    public IdJuego getIdJuego() {
        return idJuego;
    }
}
