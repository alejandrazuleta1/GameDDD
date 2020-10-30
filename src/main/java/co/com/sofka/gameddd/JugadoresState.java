package co.com.sofka.gameddd;

import co.com.sofka.domain.generic.EventChange;
import co.com.sofka.gameddd.aggregates.Jugadores;
import co.com.sofka.gameddd.entities.Competencia;
import co.com.sofka.gameddd.entities.Conductor;
import co.com.sofka.gameddd.events.JugadorAdicionado;
import co.com.sofka.gameddd.events.JugadoresCreado;

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

        apply((JugadoresCreado jugadoresCreadoevent)->{
            jugadores.conductores = new ArrayList<Conductor>(0);
            jugadores.idCompetencia = jugadoresCreadoevent.getIdCompetencia();
        });
    }
}
