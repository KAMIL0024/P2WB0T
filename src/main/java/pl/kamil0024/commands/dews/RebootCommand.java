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

package pl.kamil0024.commands.dews;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pl.kamil0024.core.command.Command;
import pl.kamil0024.core.command.CommandContext;
import pl.kamil0024.core.command.enums.PermLevel;
import pl.kamil0024.core.module.ModulManager;
import pl.kamil0024.core.util.EventWaiter;
import pl.kamil0024.music.MusicModule;
import pl.kamil0024.stats.StatsModule;

import java.util.concurrent.TimeUnit;

public class RebootCommand extends Command {

    public static Boolean reboot = false;

    private ModulManager modulManager;
    private StatsModule statsModule;
    private EventWaiter eventWaiter;
    private MusicModule musicModule;

    public RebootCommand(ModulManager modulManager, StatsModule statsModule, EventWaiter eventWaiter, MusicModule musicModule) {
        name = "reboot";
        permLevel = PermLevel.DEVELOPER;

        this.statsModule = statsModule;
        this.modulManager = modulManager;
        this.eventWaiter = eventWaiter;
        this.musicModule = musicModule;
    }

    @Override
    public boolean execute(CommandContext context) {
        reboot = true;
        context.send("Wyłączam...").complete();
        System.exit(0);
//        context.getShardManager().setStatus(OnlineStatus.DO_NOT_DISTURB);
//        context.getShardManager().setActivity(Activity.playing("Wyłącznie bota w toku..."));
//
//        musicModule.load();
//        modulManager.disableAll();
//        statsModule.getStatsCache().databaseSave();

//        context.send("Zrobić builda? (y/n)").queue();

//        eventWaiter.waitForEvent(GuildMessageReceivedEvent.class,
//                (event) -> event.getAuthor().getId().equals(context.getUser().getId()) && event.getChannel().getId().equals(context.getChannel().getId()),
//                (event) -> {
//                    if (event.getMessage().getContentRaw().toLowerCase().equals("y")) {
//                        context.send("Robię builda...").queue();
//                        ShellCommand.shell("cd /home/debian/P2WB0T && screen -dmS Tak ./start.sh");
//                    } else {
//                        context.getShardManager().shutdown();
//                        System.exit(0);
//                    }
//                }, 1, TimeUnit.MINUTES, () -> {}
//        );
        return true;
    }

}
