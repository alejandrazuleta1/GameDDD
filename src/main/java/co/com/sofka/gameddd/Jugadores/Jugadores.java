package co.com.sofka.gameddd.Jugadores;

import co.com.sofka.domain.generic.AggregateEvent;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.Juego.values.IdJuego;
import co.com.sofka.gameddd.Juego.values.Meta;
import co.com.sofka.gameddd.Juego.values.Podium;
import co.com.sofka.gameddd.Jugadores.entities.Carro;
import co.com.sofka.gameddd.Jugadores.entities.Conductor;
import co.com.sofka.gameddd.Jugadores.events.*;
import co.com.sofka.gameddd.Jugadores.values.IdConductor;
import co.com.sofka.gameddd.Jugadores.values.IdJugadores;

import java.util.List;

public class Jugadores extends AggregateEvent<IdJugadores> {
    public List<Conductor> conductores;

    private Jugadores(IdJugadores entityId){
        super(entityId);
        subscribe(new JugadoresState(this));
    }

    public Jugadores(IdJugadores entityId, List<Conductor> conductores) {
        super(entityId);
        appendChange(new JugadoresCreado(entityId)).apply();
    }

    public Jugadores(IdJugadores entityId, Conductor conductorEnturno) {
        super(entityId);
        appendChange(new JugadorEnTurnoAsignado(conductorEnturno)).apply();
    }

    public void agregarConductor(IdConductor idConductor, String nombre, Carro carro){
        appendChange(new JugadorAdicionado(idConductor,nombre,carro)).apply();
    }

    public static Jugadores from (IdJugadores idJugadores, List<DomainEvent> events){
        var Jugadores = new Jugadores(idJugadores);
        events.forEach(Jugadores::applyEvent);
        return Jugadores;
    }

    public void ejecutarTurno(Conductor conductor, IdJuego idJuego, Meta meta, Podium podium) {
        appendChange(new TurnoJugado(conductor.Id(), conductor.Nombre(),conductor.Carro().getRecorrido(), idJuego, meta, podium)).apply();
    }

    public void obtenerDistaciaRecorridaByConductor(IdConductor idConductor){
        var auxConductor = this.conductores
                .stream().filter(conductor -> conductor.Id().value().equals(idConductor.value()))
                .findFirst()
                .get();
        appendChange(new RecorridoActualizado(auxConductor.Carro().getRecorrido())).apply();
    }


}
