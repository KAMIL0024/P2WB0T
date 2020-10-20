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

package pl.kamil0024.chat;

import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import pl.kamil0024.chat.listener.KaryListener;
import pl.kamil0024.core.Ustawienia;
import pl.kamil0024.core.command.CommandExecute;
import pl.kamil0024.core.util.UserUtil;
import pl.kamil0024.core.util.kary.KaryJSON;

import java.awt.*;
import java.util.Objects;

@Data
public class Action {

    private ListaKar kara;
    private Message msg;
    private KaryJSON karyJSON;

    private boolean pewnosc = true;
    private boolean isDeleted = true;
    private String botMsg = null;

    public Action(KaryJSON karyJSON) {
        this.karyJSON = karyJSON;
    }

    public Action(ListaKar kara, Message msg, KaryJSON karyJSON) {
        this.kara = kara;
        this.msg = msg;
    }

    @SuppressWarnings("ConstantConditions")
    public void send() {
        if (kara == null || msg == null) throw new NullPointerException("kara lub msg jest nullem");

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.red);

        eb.addField("Użytkownik", UserUtil.getFullNameMc(Objects.requireNonNull(msg.getMember())), false);
        eb.addField("Treść wiadomości", msg.getContentRaw(), false);
        eb.addField("Kanał", msg.getTextChannel().getAsMention(), false);
        eb.addField("Za co ukarać?", kara.getPowod(), false);

        if (!pewnosc) {
            eb.addField("!UWAGA!", "Zgłoszenie może ukazać się fałszywe. Radze zajrzeć do kontekstu prowadzonej rozmowy", false);
        }
        if (!isDeleted) eb.addField("Link do wiadomości", String.format("[%s](%s)", "KLIK", msg.getJumpUrl()), false);
        eb.setFooter("jakiś randomowy footer bo embedy się bugują na telefonie");
        TextChannel txt = msg.getJDA().getTextChannelById(Ustawienia.instance.channel.moddc);
        if (txt == null) throw new NullPointerException("Kanał do modów dc jest nullem");
        txt.sendMessage(eb.build()).queue(m -> {
                m.addReaction(CommandExecute.getReaction(msg.getAuthor(), true)).queue();
                m.addReaction(CommandExecute.getReaction(msg.getAuthor(), false)).queue();
                m.addReaction(msg.getAuthor().getJDA().getEmoteById("623630774171729931")).queue();
                setBotMsg(m.getId());
                KaryListener.getEmbedy().add(this);
        });
    }

    public enum ListaKar {
        ZACHOWANIE("Wszelkiej maści wyzwiska, obraza, wulgaryzmy, prowokacje, groźby i inne formy przemocy"),
        FLOOD("Nadmierny spam, flood lub caps lock wiadomościami lub emotikonami"),
        SKROTY("Pisanie obraźliwych lub nieetycznych skrótów (w zależności od sytuacji i kontekstu)"),
        LINK("Reklama stron, serwisów lub serwerów gier/Discord niepowiązanych w żaden sposób z P2W.pl");

        @Getter private final String powod;

        ListaKar(String powod) {
            this.powod = powod;
        }

    }

}
