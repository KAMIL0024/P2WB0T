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

package pl.kamil0024.musicbot.core.util;

import lombok.Getter;

public class BetterStringBuilder {

    @Getter Number lines;
    @Getter StringBuilder stringBuilder;

    public BetterStringBuilder() {
        this.lines = 0;
        this.stringBuilder = new StringBuilder();
    }

    public BetterStringBuilder append(Object obj) {
        stringBuilder.append(obj);
        return this;
    }

    public BetterStringBuilder appendLine(Object obj) {
        stringBuilder.append(obj).append("\n");
        return this;
    }

    public String build() {
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return build();
    }

}
