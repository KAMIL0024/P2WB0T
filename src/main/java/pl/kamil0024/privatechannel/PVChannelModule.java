package pl.kamil0024.privatechannel;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.sharding.ShardManager;
import pl.kamil0024.core.module.Modul;
import pl.kamil0024.core.socket.SocketManager;
import pl.kamil0024.privatechannel.listeners.PVChannelListener;

public class PVChannelModule implements Modul {

    private final PVChannelListener listener;
    private final EventBus eventBus;

    @Getter
    private final String name = "pvchannels";

    @Getter @Setter
    private boolean start = false;

    public PVChannelModule(ShardManager api, SocketManager socketManager, EventBus eventBus) {
        this.eventBus = eventBus;
        listener = new PVChannelListener(api, socketManager);
    }

    @Override
    public boolean startUp() {
        eventBus.register(listener);
        return true;
    }

    @Override
    public boolean shutDown() {
        eventBus.unregister(listener);
        return true;
    }

}
