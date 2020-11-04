package co.com.sofka.gameddd.Juego.events;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.Juego.values.IdJuego;
import co.com.sofka.gameddd.Juego.values.Meta;
import co.com.sofka.gameddd.Juego.values.Podium;

public class JuegoCreado extends DomainEvent {
    private IdJuego idJuego;
    private Meta meta;
    private Podium podium;

    public JuegoCreado(IdJuego idJuego,Meta meta,Podium podium) {
        super("gameddd.Juego.JuegoCreado");
        this.idJuego = idJuego;
        this.meta = meta;
        this.podium = podium;
    }

    public IdJuego getIdJuego() {
        return idJuego;
    }
}
