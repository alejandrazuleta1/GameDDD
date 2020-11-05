package co.com.sofka.gameddd.Juego;

import co.com.sofka.domain.generic.AggregateEvent;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.gameddd.Juego.events.*;
import co.com.sofka.gameddd.Juego.values.IdJuego;
import co.com.sofka.gameddd.Juego.values.Meta;
import co.com.sofka.gameddd.Juego.values.Podium;

import java.util.List;

public class Juego extends AggregateEvent<IdJuego> {

    Podium podium;
    Meta meta;

    public Juego(IdJuego entityId,Meta meta,Podium podium) {
        super(entityId);
        this.meta = meta;
        this.podium = podium;
    }

    private Juego(IdJuego entityId) {
        super(entityId);
        subscribe(new JuegoState(this));
    }

    public static Juego from(IdJuego entityId, List<DomainEvent> eventList) {
        var Juego = new Juego(entityId);
        eventList.forEach(Juego::applyEvent);
        return Juego;
    }

    public boolean verificarPodiumDisponible(){
        return podium.value().primerLugar() == null
                || podium.value().segundoLugar() == null
                || podium.value().tercerLugar() == null;
    }

    public void agregarAlPodium(String nombreConductor){
        appendChange(new ConductorFinalizoRecorrido(nombreConductor)).apply();
    }

    public void obtenerPodiumActualizado(){
        appendChange(new PodiumActualizado(podium, idJuego()));
    }

    public void agregarMetaPodium(Meta meta, Podium podium){
        appendChange(new MetaAsignada(meta)).apply();
        appendChange(new PodiumAsignado(podium)).apply();
    }

    public void finalizar() {
        appendChange(new JuegoFinalizado()).apply();
    }

    public IdJuego idJuego() {
        return this.entityId;
    }

    public Meta getMeta() {
        return meta;
    }

    public Podium getPodium() {
        return podium;
    }
}
