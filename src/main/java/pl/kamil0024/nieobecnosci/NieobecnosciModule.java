package pl.kamil0024.nieobecnosci;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import pl.kamil0024.core.database.NieobecnosciDao;
import pl.kamil0024.core.module.Modul;
import pl.kamil0024.nieobecnosci.listeners.NieobecnosciListener;

public class NieobecnosciModule implements Modul {

    @Inject private ShardManager api;
    @Inject private NieobecnosciDao nieobecnosciDao;

    private boolean start = false;

    private NieobecnosciListener nieobecnosciListener;
    private NieobecnosciManager nieobecnosciManager;

    public NieobecnosciModule(ShardManager api, NieobecnosciDao nieobecnosciDao, NieobecnosciManager nieobecnosciManager) {
        this.api = api;
        this.nieobecnosciDao = nieobecnosciDao;
        this.nieobecnosciManager = nieobecnosciManager;
    }

    @Override
    public boolean startUp() {
        this.nieobecnosciListener = new NieobecnosciListener(api, nieobecnosciDao, nieobecnosciManager);
        api.addEventListener(nieobecnosciListener);
        setStart(true);
        return true;
    }

    @Override
    public boolean shutDown() {
        api.removeEventListener(nieobecnosciListener);
        setStart(false);
        return true;
    }

    @Override
    public String getName() {
        return "nieobecnosci";
    }

    @Override
    public boolean isStart() {
        return this.start;
    }

    @Override
    public void setStart(boolean bol) {
        this.start = bol;
    }

}