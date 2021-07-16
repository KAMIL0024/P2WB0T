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

package pl.kamil0024.ticket;


import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.Setter;
import pl.kamil0024.core.database.TXTTicketDao;
import pl.kamil0024.core.module.Modul;
import pl.kamil0024.core.redis.RedisManager;
import pl.kamil0024.ticket.components.ComponentListener;

public class TicketModule implements Modul {

    private final RedisManager redisManager;
    private final TXTTicketDao txtTicketDao;
    private final EventBus eventBus;

    @Getter
    private final String name = "ticket";

    @Getter
    @Setter
    private boolean start = false;

    private ComponentListener listener = null;

    public TicketModule(RedisManager redisManager, TXTTicketDao txtTicketDao, EventBus eventBus) {
        this.redisManager = redisManager;
        this.txtTicketDao = txtTicketDao;
        this.eventBus = eventBus;
    }

    @Override
    public boolean startUp() {
        this.listener = new ComponentListener(txtTicketDao, redisManager);
        eventBus.register(listener);
        setStart(true);
        return true;
    }

    @Override
    public boolean shutDown() {
        eventBus.unregister(listener);
        setStart(false);
        return true;
    }

}
