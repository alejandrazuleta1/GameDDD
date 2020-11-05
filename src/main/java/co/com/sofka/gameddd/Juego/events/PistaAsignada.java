package co.com.sofka.gameddd.Juego.events;

import co.com.sofka.gameddd.Juego.values.Pista;

public class PistaAsignada extends co.com.sofka.domain.generic.DomainEvent {
    private final Pista pista;

    public PistaAsignada(Pista pista) {
        super("gameddd.Juego.events.PistaAsignada");
        this.pista = pista;
    }

    public Pista getPista() {
        return pista;
    }
}
