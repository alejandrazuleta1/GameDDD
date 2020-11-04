package co.com.sofka.gameddd.Jugadores;

import co.com.sofka.domain.generic.EventChange;
import co.com.sofka.gameddd.Jugadores.Jugadores;
import co.com.sofka.gameddd.Jugadores.entities.Conductor;
import co.com.sofka.gameddd.Jugadores.events.*;
import co.com.sofka.gameddd.Jugadores.values.Recorrido;

import java.util.ArrayList;


public class JugadoresState extends EventChange {

    public JugadoresState(Jugadores jugadores) {
        apply((JugadorAdicionado jugadorAdicionado)->{
            jugadores.conductores.add(
                    new Conductor(
                            jugadorAdicionado.getIdConductor(),
                            jugadorAdicionado.getNombre(),
                            jugadorAdicionado.getCarro()));
        });

        apply((JugadorEnTurnoAsignado jugadorEnTurnoAsignado)->{
            jugadores.conductores.add(jugadorEnTurnoAsignado.getConductor());
        });

        apply((JugadoresCreado jugadoresCreadoevent)->{
            jugadores.conductores = new ArrayList<Conductor>(0);
        });

        apply((TurnoJugado turnoJugado)->{
            jugadores.conductores
                    .stream().filter(conductor -> conductor.Id().value().equals(turnoJugado.getIdConductor().value()))
                    .findFirst()
                    .get()
                    .moverCarro();
        });
    }
}
