package co.com.sofka.gameddd.Juego.events;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.Juego.values.Meta;

public class MetaAsignada extends DomainEvent {

    private Meta meta;

    public MetaAsignada(Meta meta) {
        super("gameddd.Juego.MetaAsignada");
        this.meta = meta;
    }

    public Meta getMeta() {
        return meta;
    }
}
