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

package pl.kamil0024.commands;

import com.google.common.eventbus.EventBus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil0024.api.APIModule;
import pl.kamil0024.commands.dews.*;
import pl.kamil0024.commands.kolkoikrzyzyk.KolkoIKrzyzykManager;
import pl.kamil0024.commands.listener.GiveawayListener;
import pl.kamil0024.commands.listener.GuildListener;
import pl.kamil0024.commands.system.*;
import pl.kamil0024.commands.zabawa.*;
import pl.kamil0024.core.Ustawienia;
import pl.kamil0024.core.command.Command;
import pl.kamil0024.core.command.CommandExecute;
import pl.kamil0024.core.command.CommandManager;
import pl.kamil0024.core.database.*;
import pl.kamil0024.core.logger.Log;
import pl.kamil0024.core.module.Modul;
import pl.kamil0024.core.module.ModulManager;
import pl.kamil0024.core.socket.SocketManager;
import pl.kamil0024.core.userstats.manager.UserstatsManager;
import pl.kamil0024.core.util.EventWaiter;
import pl.kamil0024.core.util.kary.KaryJSON;
import pl.kamil0024.embedgenerator.entity.EmbedRedisManager;
import pl.kamil0024.moderation.commands.StatusCommand;
import pl.kamil0024.moderation.listeners.ModLog;
import pl.kamil0024.music.MusicModule;
import pl.kamil0024.music.utils.SpotifyUtil;
import pl.kamil0024.stats.StatsModule;
import pl.kamil0024.status.StatusModule;
import pl.kamil0024.weryfikacja.WeryfikacjaModule;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandsModule implements Modul {

    private static final Logger logger = LoggerFactory.getLogger(CommandsModule.class);

    private final CommandManager commandManager;
    private final ShardManager api;
    private final EventWaiter eventWaiter;
    private final KaryJSON karyJSON;
    private final CaseDao caseDao;
    private final ModulManager modulManager;
    private final CommandExecute commandExecute;
    private final UserDao userDao;
    private final NieobecnosciDao nieobecnosciDao;
    private final RemindDao remindDao;
    private final GiveawayDao giveawayDao;
    private final StatsModule statsModule;
    private final MusicModule musicModule;
    private final MultiDao multiDao;
    private final TicketDao ticketDao;
    private final ApelacjeDao apelacjeDao;
    private final AnkietaDao ankietaDao;
    private final EmbedRedisManager embedRedisManager;
    private final WeryfikacjaDao weryfikacjaDao;
    private final WeryfikacjaModule weryfikacjaModule;
    private final RecordingDao recordingDao;
    private final SocketManager socketManager;
    private final DeletedMessagesDao deletedMessagesDao;
    private final AcBanDao acBanDao;
    private final UserstatsManager userstatsManager;
    private final StatusModule statusModule;
    private final APIModule apiModule;
    private final SpotifyUtil spotifyUtil;
    private final EventBus eventBus;

    @Getter
    private final String name = "commands";

    @Getter @Setter
    private boolean start = false;
    private final ModLog modLog;

    // Listeners
    private KolkoIKrzyzykManager kolkoIKrzyzykManager;

    private GuildListener guildListener;

    private ArrayList<Command> cmd;

    public CommandsModule(CommandManager commandManager, ShardManager api, EventWaiter eventWaiter, KaryJSON karyJSON, CaseDao caseDao, ModulManager modulManager, CommandExecute commandExecute, UserDao userDao, ModLog modLog, NieobecnosciDao nieobecnosciDao, RemindDao remindDao, GiveawayDao giveawayDao, StatsModule statsModule, MusicModule musicModule, MultiDao multiDao, TicketDao ticketDao, ApelacjeDao apelacjeDao, AnkietaDao ankietaDao, EmbedRedisManager embedRedisManager, WeryfikacjaDao weryfikacjaDao, WeryfikacjaModule weryfikacjaModule, RecordingDao recordingDao, SocketManager socketManager, DeletedMessagesDao deletedMessagesDao, AcBanDao acBanDao, UserstatsManager userstatsManager, StatusModule statusModule, APIModule apiModule, SpotifyUtil spotifyApi, EventBus eventBus) {
        this.commandManager = commandManager;
        this.api = api;
        this.eventWaiter = eventWaiter;
        this.karyJSON = karyJSON;
        this.caseDao = caseDao;
        this.modulManager = modulManager;
        this.commandExecute = commandExecute;
        this.userDao = userDao;
        this.modLog = modLog;
        this.nieobecnosciDao = nieobecnosciDao;
        this.remindDao = remindDao;
        this.giveawayDao = giveawayDao;
        this.statsModule = statsModule;
        this.musicModule = musicModule;
        this.multiDao = multiDao;
        this.ticketDao = ticketDao;
        this.apelacjeDao = apelacjeDao;
        this.ankietaDao = ankietaDao;
        this.embedRedisManager = embedRedisManager;
        this.weryfikacjaDao = weryfikacjaDao;
        this.weryfikacjaModule = weryfikacjaModule;
        this.recordingDao = recordingDao;
        this.socketManager = socketManager;
        this.deletedMessagesDao = deletedMessagesDao;
        this.acBanDao = acBanDao;
        this.userstatsManager = userstatsManager;
        this.statusModule = statusModule;
        this.apiModule = apiModule;
        this.spotifyUtil = spotifyApi;
        this.eventBus = eventBus;

        ScheduledExecutorService executorSche = Executors.newSingleThreadScheduledExecutor();
        executorSche.scheduleWithFixedDelay(() -> {
            try {
                logger.debug("Startuje taska");
                tak(api);
            } catch (Exception e) {
                Log.newError(e, getClass());
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public boolean startUp() {
        GiveawayListener giveawayListener = new GiveawayListener(giveawayDao, api);
        kolkoIKrzyzykManager = new KolkoIKrzyzykManager(api, eventWaiter);
        guildListener = new GuildListener();

        eventBus.register(guildListener);

        cmd = new ArrayList<>();

        cmd.add(new PingCommand());
        cmd.add(new BotinfoCommand(commandManager, modulManager, socketManager));
        cmd.add(new HelpCommand(commandManager));
        cmd.add(new PoziomCommand());
        cmd.add(new EvalCommand(eventWaiter, commandManager, caseDao, modLog, karyJSON, commandExecute, userDao, nieobecnosciDao, remindDao, modulManager, giveawayListener, giveawayDao, statsModule, multiDao, musicModule, ticketDao, apelacjeDao, ankietaDao, embedRedisManager, weryfikacjaDao, weryfikacjaModule, socketManager, deletedMessagesDao, acBanDao, userstatsManager, statusModule, spotifyUtil));
        cmd.add(new ForumCommand());
        cmd.add(new UserinfoCommand());
        cmd.add(new McpremiumCommand());
        cmd.add(new RemindmeCommand(remindDao, eventWaiter));
        cmd.add(new ModulesCommand(modulManager));
        cmd.add(new CytujCommand());
        cmd.add(new GiveawayCommand(giveawayDao, eventWaiter, giveawayListener));
        cmd.add(new RebootCommand());
        cmd.add(new ShellCommand());
        cmd.add(new ArchiwizujCommand());
        cmd.add(new PogodaCommand());
        cmd.add(new KolkoIKrzyzykCommand(kolkoIKrzyzykManager));
        cmd.add(new RecordingCommand(recordingDao, eventWaiter));
        cmd.add(new SasinCommand());
        cmd.add(new StatsCommand(userstatsManager.userstatsDao));
        cmd.add(new WeryfikacjaCommand(apiModule, weryfikacjaModule));
        cmd.add(new SpotifyStatsCommand(spotifyUtil, eventWaiter));

        cmd.forEach(commandManager::registerCommand);
        setStart(true);
        return true;
    }

    private void tak(ShardManager api) {
        RemindmeCommand.check(remindDao, api);
        TextChannel txt = api.getTextChannelById(Ustawienia.instance.channel.status);
        if (txt != null) {
            Message botMsg = null;
            MessageHistory history = null;

            try {
                history = txt.getHistoryFromBeginning(15).complete();
            } catch (Exception ignored) { }

            if (history != null && !history.isEmpty()) {
                for (Message message : history.getRetrievedHistory()) {
                    if (message.getAuthor().getId().equals(Ustawienia.instance.bot.botId)) {
                        botMsg = message;
                        break;
                    }
                }

                if (botMsg != null) {
                    try {
                        String c = StatusCommand.getMsg(null, null, null, botMsg.getContentRaw());
                        botMsg.editMessage(c).complete();
                    } catch (Exception e) {
                        Log.newError(e, getClass());
                    }
                }
            }
        } else Log.newError("Kanal do statusu jest nullem", getClass());

    }

    @Override
    public boolean shutDown() {
        kolkoIKrzyzykManager.stop();
        eventBus.unregister(guildListener);
        commandManager.unregisterCommands(cmd);
        setStart(false);
        return true;
    }

    @Data
    @AllArgsConstructor
    public static class KaraJSON {
        private final int id;
        private final String powod;
    }

    @Data
    @AllArgsConstructor
    public static class WarnJSON {
        private final int warns;
        private final String kara;
        private final String czas;
    }

}