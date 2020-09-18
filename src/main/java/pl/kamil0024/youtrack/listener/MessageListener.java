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

package pl.kamil0024.youtrack.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import pl.kamil0024.core.Ustawienia;
import pl.kamil0024.core.command.enums.PermLevel;
import pl.kamil0024.core.logger.Log;
import pl.kamil0024.core.util.BetterStringBuilder;
import pl.kamil0024.core.util.UserUtil;
import pl.kamil0024.youtrack.YouTrack;
import pl.kamil0024.youtrack.exceptions.APIException;
import pl.kamil0024.youtrack.models.Issue;
import pl.kamil0024.youtrack.models.Project;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageListener extends ListenerAdapter {

    private static final String NICO = "\uDB40\uDC00\uDB40\uDC00\uDB40\uDC00\uDB40\uDC00 \uDB40\uDC00\uDB40\uDC00\uDB40\uDC00\uDB40\uDC00";
    private static final Pattern ISSUE_ID = Pattern.compile("([A-Z0-9]+)-(\\d+)");

    private final YouTrack youTrack;

    public MessageListener(YouTrack youTrack) {
        this.youTrack = youTrack;
        try {
            youTrack.retrieveProjects();
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent e) {
        if (!e.isFromGuild() || !e.getGuild().getId().equals(Ustawienia.instance.bot.guildId) ||
                e.getAuthor().isBot() || UserUtil.getPermLevel(e.getAuthor()) == PermLevel.MEMBER) return;
        Matcher issue = ISSUE_ID.matcher(e.getMessage().getContentRaw());
        List<MessageEmbed> embedsToSend = new ArrayList<>();
        while (issue.find()) {
            String project = issue.group(1);
            String id = issue.group(2);
            try {
                boolean matches = false;
                for (Project p : youTrack.getProjects()) {
                    if (p.getShortName().equals(project)) {
                        matches = true;
                        break;
                    }
                }
                if (!matches) break;
                String iId = project + "-" + id;
                List<Issue> issues = youTrack.retrieveIssues(iId);
                Issue iss = null;
                for (Issue i : issues) if (i.getIdReadable().equals(iId)) iss = i;
                if (iss == null) break;
                embedsToSend.add(generateEmbed(iss));
            } catch (Exception ex) {
                Log.newError(ex, MessageListener.class);
            }
        }
        if (embedsToSend.size() == 0) return;
        if (embedsToSend.size() == 1) e.getChannel().sendMessage(embedsToSend.get(0)).queue();
        else {
            e.getChannel().sendMessage("W wiadomości wykryto zgłoszenia:").queue();
            int wyslano = 0;
            for (MessageEmbed em : embedsToSend) {
                e.getChannel().sendMessage(em).queue();
                wyslano++;
                if (wyslano == 3) {
                    int liczba = (embedsToSend.size() - wyslano);
                    if (liczba != 0) e.getChannel().sendMessage("...i " + liczba + " więcej").queue();
                    break;
                }
            }
        }
    }

    public static MessageEmbed generateEmbed(Issue i) {
        return generateEmbedBuilder(i).build();
    }

    public static EmbedBuilder generateEmbedBuilder(Issue i) {
        Issue.Field priorytet = null;
        Issue.Field typ = null;
        Issue.Field status = null;
        Issue.Field przypisane = null;
        Issue.Field trybGry = null;
        Issue.Field wersjaMc = null;
        Issue.Field serwer = null;
        Issue.Field arcade = null;
        Issue.Field wynikTestu = null;
        Issue.Field tester = null;
        Issue.Field nickZglaszajacego = null;
        Issue.Field iloscMonet = null;
        for (Issue.Field f : i.getFields()) {
            if (f.getName().equals("Priorytet")) priorytet = f;
            if (f.getName().equals("Typ")) typ = f;
            if (f.getName().equals("Status")) status = f;
            if (f.getName().equals("Przypisane do")) przypisane = f;
            if (f.getName().equals("Tryb Gry")) trybGry = f;
            if (f.getName().equals("Wersja Minecrafta")) wersjaMc = f;
            if (f.getName().equals("Serwer")) serwer = f;
            if (f.getName().equals("Tryby Na Arcade")) arcade = f;
            if (f.getName().equals("Tester")) tester = f;
            if (f.getName().equals("Wyniki Testu")) wynikTestu = f;
            if (f.getName().equals("Nick zgłaszającego")) nickZglaszajacego = f;
            if (f.getName().equals("Ilość monet")) iloscMonet = f;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(i.getIdReadable(), Ustawienia.instance.yt.url + "/issue/" + i.getIdReadable());
        eb.setTitle(i.getSummary());
        eb.setDescription(i.getDescription());
        eb.setColor(priorytet.getValue().get(0).getColor().getBackground());
        eb.setFooter(i.getReporter().getFullName(), i.getReporter().getAvatarUrl());
        eb.setTimestamp(Instant.ofEpochMilli(i.getCreated())).build();

        String info = "Priorytet: %s\nTyp: %s\nStatus: %s\nPrzypisane do: %s";
        eb.addField("Podstawowe Informacje",
                String.format(info,
                        value(priorytet),
                        value(typ),
                        value(status),
                        value(przypisane)),
                false);
        BetterStringBuilder bsb = new BetterStringBuilder();
        String trybGryValue = trybGry.getValue().get(0).getName();
        String value;
        switch (trybGryValue) {
            case "Lobby":
                bsb.appendLine("Tryb gry: Lobby");
                if (serwer != null) value = listToString(serwer.getValue());
                else value = "(Brak)";
                bsb.appendLine(NICO + NICO + NICO + NICO + "- Serwer(-y): " + value);
                break;
            case "Budowlany":
            case "BedWars":
            case "SkyWars":
            case "Murder Mystery":
            case "Housing | Freebuild (stara edycja)":
            case "Build Battle":
            case "Forum":
                bsb.appendLine("Tryb gry: " + value(trybGryValue));
                break;
            case "Arcade Games":
                bsb.appendLine("Tryb gry: Arcade Games");
                if (arcade != null) value = listToString(arcade.getValue());
                else value = "(Brak)";
                bsb.appendLine(NICO + NICO + NICO + NICO + "- Tryb(-y) gier na arcade: " + value(value));
                break;
        }

        BetterStringBuilder dodatkowe = new BetterStringBuilder();
        if (!trybGryValue.equals("Forum") && !trybGryValue.equals("Lobby") && wersjaMc != null) {
            dodatkowe.appendLine("Wersja Minecrafta: " + value(wersjaMc));
        }

        dodatkowe.appendLine("Tester: " + value(tester));
        dodatkowe.appendLine("Wyniki Testu: " + value(wynikTestu));
        dodatkowe.appendLine("Nick Zgłaszającego: " + value(nickZglaszajacego));
        dodatkowe.appendLine("Ilość monet: " + value(iloscMonet));
        eb.addField("Informacje Dodatkowe", dodatkowe.toString(), false);
        return eb;
    }

    private static String listToString(List<Issue.Field.FieldValue> lista) {
        StringBuilder sb = new StringBuilder();
        int tak = 1;
        for (Issue.Field.FieldValue f : lista) {
            sb.append(value(f.getName()));
            if (tak != lista.size()) sb.append(", ");
            tak++;
        }
        return sb.toString();
    }

    private static String value(@Nullable Issue.Field s) {
        if (s == null) return "**Brak**";
        int size = 1;
        try {
            StringBuilder sb = new StringBuilder();
            for (Issue.Field.FieldValue field : s.getValue()) {
                sb.append(field.getName());
                if (size < s.getValue().size()) sb.append(", ");
                size++;
            }
            return value(sb.toString());
        } catch (NullPointerException ignored) { }
        return "**Brak**";
    }

    private static String value(@Nullable String s) {
        if (s == null) return "**Brak**";
        try {
            return "**" + s + "**";
        } catch (NullPointerException ignored) { }
        return "**Brak**";
    }

}
