package co.com.sofka.gameddd.Jugadores;

import co.com.sofka.domain.generic.EventChange;
import co.com.sofka.gameddd.Jugadores.Jugadores;
import co.com.sofka.gameddd.Jugadores.entities.Conductor;
import co.com.sofka.gameddd.Jugadores.events.JugadorAdicionado;
import co.com.sofka.gameddd.Jugadores.events.JugadoresCreado;

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
