package co.com.sofka.gameddd.events;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.values.IdCompetencia;

public class JugadoresCreado  extends DomainEvent {

    private final IdCompetencia idCompetencia;

    public IdCompetencia getIdCompetencia() {
        return idCompetencia;
    }

    public JugadoresCreado(IdCompetencia idCompetencia) {
        super("gameDDD.JugadoresCreado");
        this.idCompetencia=idCompetencia;
    }
}
