package co.com.sofka.gameddd.Juego.entities;

import co.com.sofka.domain.generic.Entity;
import co.com.sofka.gameddd.Juego.values.Carril;
import co.com.sofka.gameddd.Juego.values.IdCompetencia;
import co.com.sofka.gameddd.Juego.values.Meta;

import java.util.List;

public class Competencia extends Entity<IdCompetencia> {
    Meta meta;
    Integer numeroMaximoDeCarriles;
    List<Carril> carriles;

    public Competencia(IdCompetencia entityId,Meta meta, Integer numeroMaximoDeCarriles) {
        super(entityId);
        this.meta = meta;
        this.numeroMaximoDeCarriles = numeroMaximoDeCarriles;
    }
}
