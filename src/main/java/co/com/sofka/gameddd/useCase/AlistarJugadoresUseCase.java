package co.com.sofka.gameddd.useCase;

import co.com.sofka.business.generic.UseCase;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.business.support.ResponseEvents;
import co.com.sofka.gameddd.Jugadores.Jugadores;
import co.com.sofka.gameddd.Jugadores.comands.AlistarJugadores;
import co.com.sofka.gameddd.Jugadores.values.IdJugadores;

public class AlistarJugadoresUseCase extends UseCase<RequestCommand<AlistarJugadores>, ResponseEvents> {

    @Override
    public void executeUseCase(RequestCommand<AlistarJugadores> alistarJugadoresRequestCommand) {
        var command = alistarJugadoresRequestCommand.getCommand();
        var jugadores = new Jugadores(new IdJugadores(),command.getConductores());
        command.getConductores().forEach((conductor -> {
            jugadores.agregarConductor(
                    conductor.identity(),
                    conductor.Nombre(),
                    conductor.Carro()
                    );
        }));
        emit().onSuccess(new ResponseEvents(jugadores.getUncommittedChanges()));
    }
}
