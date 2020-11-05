package co.com.sofka.gameddd.Jugadores.commands;

import co.com.sofka.domain.generic.Command;
import co.com.sofka.gameddd.Jugadores.entities.Conductor;

import java.util.List;

public class AlistarJugadores implements Command {
    private List<Conductor> conductores;

    public AlistarJugadores(List<Conductor> conductores){
        this.conductores = conductores;
    }

    public List<Conductor> getConductores() {
        return conductores;
    }
}
