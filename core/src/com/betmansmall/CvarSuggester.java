package com.betmansmall;

import com.betmansmall.console.Console;
import com.betmansmall.console.ConsoleUtils;
import com.betmansmall.cvar.Cvar;
import com.betmansmall.util.StringUtils;

import java.util.Set;
import java.util.SortedMap;

public enum CvarSuggester implements Console.SuggestionProvider {
    INSTANCE;

    @Override
    public int suggest(Console console, CharSequence buffer, String[] args, int targetArg) {
        String arg = targetArg == args.length ? "" : args[targetArg];
        SortedMap<String, Cvar> cvars = TTW.cvars.prefixMap(arg);
        switch (cvars.size()) {
            case 0:
                return 0;
            case 1:
                String alias = cvars.firstKey();
                console.in.append(alias, arg.length());
                return 1;
            default:
                Set<String> aliases = cvars.keySet();
                String commonPrefix = StringUtils.commonPrefix(aliases);
                if (commonPrefix.length() > arg.length()) {
                    console.in.append(commonPrefix, arg.length());
                } else {
                    ConsoleUtils.printList(console, aliases, 4, 36);
                }

                return aliases.size();
        }
    }
}
