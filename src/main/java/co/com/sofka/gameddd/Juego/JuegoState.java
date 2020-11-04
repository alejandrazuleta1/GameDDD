package co.com.sofka.gameddd.Juego;

import co.com.sofka.domain.generic.EventChange;
import co.com.sofka.gameddd.Juego.events.ConductorFinalizoRecorrido;
import co.com.sofka.gameddd.Juego.events.MetaAsignada;
import co.com.sofka.gameddd.Juego.events.PodiumAsignado;

public class JuegoState extends EventChange {

    public JuegoState(Juego juego) {
        apply((ConductorFinalizoRecorrido conductorFinalizoRecorrido)->{
            juego.podium.agregar(conductorFinalizoRecorrido.getNombreConductor());
        });

        apply((MetaAsignada metaAsignada)->{
            juego.meta = metaAsignada.getMeta();
        });

        apply((PodiumAsignado podiumAsignado)->{
            juego.podium = podiumAsignado.getPodium();
        });
    }
}
