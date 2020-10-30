package co.com.sofka.gameddd.aggregates;

import co.com.sofka.domain.generic.AggregateEvent;
import co.com.sofka.gameddd.entities.Competencia;
import co.com.sofka.gameddd.values.JuegoId;
import co.com.sofka.gameddd.values.Podium;

public class Juego extends AggregateEvent<JuegoId> {
    Competencia competencia;
    Podium podium;

    public Juego(JuegoId entityId) {
        super(entityId);
    }

    public void asignarTurno(){

    }

    public void asignarConductorACarril(){

    }

    public void actualizarCarrilPorTurnoJugado(){

    }

    public void verificarCompetenciaFinalizada(){

    }




}
