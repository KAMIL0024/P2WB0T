/*
 *
 *    Copyright 2020 P2WB0T
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pl.kamil0024.rekrutacyjny;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.sharding.ShardManager;
import pl.kamil0024.core.command.Command;
import pl.kamil0024.core.command.CommandManager;
import pl.kamil0024.core.module.Modul;
import pl.kamil0024.rekrutacyjny.commands.OgloszenieCommand;
import pl.kamil0024.rekrutacyjny.listeners.SyncListener;

import java.util.ArrayList;

public class RekruModule implements Modul {


    private final CommandManager commandManager;
    private final EventBus eventBus;

    @Getter
    private final String name = "rekrutacyjny";

    @Getter
    @Setter
    private boolean start = false;

    private SyncListener listener;
    private ArrayList<Command> cmd;

    public RekruModule(CommandManager commandManager, EventBus eventBus) {
        this.commandManager = commandManager;
        this.eventBus = eventBus;
    }

    @Override
    public boolean startUp() {
        cmd = new ArrayList<>();
        cmd.add(new OgloszenieCommand());
        cmd.forEach(commandManager::registerCommand);

        listener = new SyncListener();
        eventBus.register(listener);
        setStart(true);
        return true;
    }

    @Override
    public boolean shutDown() {
        eventBus.unregister(listener);
        commandManager.unregisterCommands(cmd);
        setStart(false);
        return true;
    }

}
