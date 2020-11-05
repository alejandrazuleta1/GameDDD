package co.com.sofka.gameddd.useCase;

import co.com.sofka.business.generic.UseCase;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.business.support.ResponseEvents;
import co.com.sofka.gameddd.Jugadores.Jugadores;
import co.com.sofka.gameddd.Jugadores.commands.AsignarTurno;

import java.util.logging.Logger;

public class EjecutarTurnoUseCase extends UseCase<RequestCommand<AsignarTurno>, ResponseEvents> {

    private static final Logger logger = Logger.getLogger(EjecutarTurnoUseCase.class.getName());

    @Override
    public void executeUseCase(RequestCommand<AsignarTurno> asignarTurnoRequestCommand) {
        var command = asignarTurnoRequestCommand.getCommand();
        var jugadores = Jugadores.from(asignarTurnoRequestCommand.getCommand().getIdJugadores(), retrieveEvents());
        /*jugadores.agregarConductor(
                command.getConductorEnTurno().Id(),
                command.getConductorEnTurno().Nombre(),
                command.getConductorEnTurno().Carro());
         */
        jugadores.ejecutarTurno(command.getConductorEnTurno(), command.getIdJuego(),command.getMeta(), command.getPodium());
        jugadores.obtenerDistaciaRecorridaByConductor(command.getConductorEnTurno().Id());
        emit().onSuccess(new ResponseEvents(jugadores.getUncommittedChanges()));
    }
}
