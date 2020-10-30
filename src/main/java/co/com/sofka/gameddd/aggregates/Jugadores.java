package co.com.sofka.gameddd.aggregates;

import co.com.sofka.domain.generic.AggregateEvent;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.JugadoresState;
import co.com.sofka.gameddd.entities.Carro;
import co.com.sofka.gameddd.entities.Conductor;
import co.com.sofka.gameddd.events.JugadorAdicionado;
import co.com.sofka.gameddd.events.JugadoresCreado;
import co.com.sofka.gameddd.values.IdCompetencia;
import co.com.sofka.gameddd.values.IdConductor;
import co.com.sofka.gameddd.values.IdJugadores;

import java.util.ArrayList;
import java.util.List;

public class Jugadores extends AggregateEvent<IdJugadores> {
    public List<Conductor> conductores;
    public IdCompetencia idCompetencia;

    public Jugadores(IdJugadores entityId, IdCompetencia idCompetencia) {
        super(entityId);
        appendChange(new JugadoresCreado(idCompetencia)).apply();
    }

    private Jugadores(IdJugadores entityId){
        super(entityId);
        subscribe(new JugadoresState(this));
    }

    public void agregarConductor(IdConductor idConductor, String nombre, Carro carro){
        appendChange(new JugadorAdicionado(idConductor,nombre,carro)).apply();
    }

    public static Jugadores from (IdJugadores idJugadores, List<DomainEvent> events){
        var Jugadores = new Jugadores(idJugadores);
        events.forEach(Jugadores::applyEvent);
        return Jugadores;
    }


}
