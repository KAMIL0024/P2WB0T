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

package pl.kamil0024.commands.listener;

import com.google.common.eventbus.Subscribe;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import pl.kamil0024.core.Ustawienia;
import pl.kamil0024.moderation.commands.MuteCommand;

public class GuildListener {

    public GuildListener() { }

    @Subscribe
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (!event.getGuild().getId().equals(Ustawienia.instance.bot.guildId)) return;
        if (MuteCommand.hasMute(event.getMember())) {
            Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            if (msg == null) return; // tak
            MessageReaction.ReactionEmote emote = event.getReactionEmote();
            if (emote.isEmote()) msg.removeReaction(emote.getEmote(), event.getUser()).queue();
            else msg.removeReaction(emote.getEmoji(), event.getUser()).queue();
        }
    }

}
