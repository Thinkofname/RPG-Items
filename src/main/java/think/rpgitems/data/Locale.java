/*
 *  This file is part of RPG Items.
 *
 *  RPG Items is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  RPG Items is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with RPG Items.  If not, see <http://www.gnu.org/licenses/>.
 */
package think.rpgitems.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import think.rpgitems.Plugin;

public class Locale {

    private static HashMap<String, String> strings = new HashMap<String, String>();

    public static void init(Plugin plugin) {
        try {
            InputStream defs = plugin.getResource("default.lang");
            load(defs);
            defs.close();
            File altFile = new File(plugin.getDataFolder(), "default.lang");
            if (altFile.exists()) {
                FileInputStream alts = new FileInputStream(altFile);
                load(alts);
                alts.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void load(InputStream in) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;
        while ((line = r.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("-") || line.length() == 0)
                continue;
            String[] args = line.split("=");
            args[0] = args[0].trim();
            args[1] = args[1].trim();
            args[1] = args[1].substring(1, args[1].length() - 1);
            strings.put(args[0], args[1]);
        }
    }

    public static String get(String name) {
        if (strings.containsKey(name))
            return strings.get(name);
        return "Locale error: " + name;
    }
}
