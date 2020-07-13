package pl.kamil0024.commands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import pl.kamil0024.core.command.Command;
import pl.kamil0024.core.command.CommandContext;
import pl.kamil0024.core.command.enums.PermLevel;
import pl.kamil0024.core.database.CaseDao;
import pl.kamil0024.core.database.config.CaseConfig;
import pl.kamil0024.core.util.UserUtil;

import java.time.Instant;
import java.util.List;

public class CheckCommand extends Command {

    private CaseDao caseDao;

    public CheckCommand(CaseDao caseDao) {
        name = "check";
        permLevel = PermLevel.HELPER;
        cooldown = 5;
        this.caseDao = caseDao;
    }

    @Override
    public boolean execute(CommandContext context) {
        User user = context.getParsed().getUser(context.getArgs().get(0));
        if (user == null) {
            context.send("Nie ma takiego użytkownika!").queue();
            return false;
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(UserUtil.getColor(context.getMember()));
        eb.setTimestamp(Instant.now());
        eb.setFooter("Check");
        eb.setThumbnail(user.getAvatarUrl());

        boolean maBana = false;
        List<Guild.Ban> bany = context.getGuild().retrieveBanList().complete();
        for (Guild.Ban ban : bany) {
            if (ban.getUser().getId().equals(user.getId())) {
                maBana = true;
                break;
            }
        }
        Member mem = context.getParsed().getMember(user.getId());

        String kara = "???";
        List<CaseConfig> kary = caseDao.getAll(user.getId());
        if (!kary.isEmpty()) kara = String.valueOf(kary.get(kary.size() - 1).getKara().getKaraId());

        eb.addField(context.getTranslate("check.name"), UserUtil.getLogName(user), false);
        eb.addField(context.getTranslate("check.ban"), maBana ? "Tak" : "Nie", false);
        if (mem != null) {
            eb.addField(context.getTranslate("check.mute"), MuteCommand.hasMute(mem) ? "Tak" : "Nie", false);
        }
        eb.addField(context.getTranslate("check.onserwer"), mem != null ? "Tak" : "Nie", false);
        eb.addField(context.getTranslate("check.lastcase"), "ID: " + kara, false);
        context.send(eb.build()).queue();
        return true;
    }
}
