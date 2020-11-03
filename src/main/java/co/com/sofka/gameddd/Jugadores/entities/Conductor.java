package co.com.sofka.gameddd.Jugadores.entities;

import co.com.sofka.domain.generic.Entity;
import co.com.sofka.gameddd.Jugadores.values.IdConductor;

import java.util.Random;

public class Conductor extends Entity<IdConductor> {

    private String nombre;
    private Carro carro;

    public Conductor(IdConductor entityId, String nombre, Carro carro) {
        super(entityId);
        this.carro = carro;
        this.nombre = nombre;
    }

    public Integer lanzarDado(){
        var rn = new Random();
        return 1 + rn.nextInt(6 - 1 + 1);
    }

    public void moverCarro(){
        carro.avanzar(lanzarDado()*100);
    }

    public String Nombre() {
        return nombre;
    }

    public Carro Carro() {
        return carro;
    }
}
