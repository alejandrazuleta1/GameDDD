package co.com.sofka.gameddd.Jugadores.comands;

import co.com.sofka.domain.generic.Command;
import co.com.sofka.gameddd.Jugadores.entities.Conductor;
import co.com.sofka.gameddd.Juego.values.IdCompetencia;

import java.util.List;

public class AlistarJugadores implements Command {
    private List<Conductor> conductores;
    private IdCompetencia idCompetencia;

    public AlistarJugadores(List<Conductor> conductores, IdCompetencia idCompetencia){
        this.conductores = conductores;
        this.idCompetencia = idCompetencia;
    }

    public AlistarJugadores() {
    }

    public List<Conductor> getConductores() {
        return conductores;
    }

    public IdCompetencia getIdCompetencia() {
        return idCompetencia;
    }
}
