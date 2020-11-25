package com.punuo.sys.net.delay;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DxNetworkUtil {

    public static List<DxIfconfig> getIfconfig() {
        String[] commandLine = new String[]{
                "ifconfig"
        };

        List<DxIfconfig> ifconfigs = new ArrayList<>();

        try {
            Process process = Runtime.getRuntime().exec(commandLine);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            DxIfconfig conf = new DxIfconfig();
            do {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }

                line = "";

                if (line.matches("\\s*|\\t|\\r|\\n")) {
                    if (conf.isNotBlank()) {
                        ifconfigs.add(conf);
                        conf = new DxIfconfig();
                    }
                }
                else if (line.trim().matches("inet addr:(\\d{1,3}\\.){3}\\d{1,3}( ){2}" +
                        "(Bcast:(\\d{1,3}\\.){3}\\d{1,3}( ){2}){0,1}" +
                        "Mask:(\\d{1,3}\\.){3}\\d{1,3}")) {
//                    System.out.println(line.trim());

                    String[] props = line.trim().split("( ){2}");
                    for (String prop : props) {
                        if (prop.length() == 0) {
                            continue;
                        }

                        String[] kv = prop.split(":");
                        if (kv[0].startsWith("inet addr")) {
                            conf.inetAddr = kv[1];
                        } else if (kv[0].startsWith("Bcast")) {
                            conf.bcast = kv[1];
                        } else if (kv[0].startsWith("Mask")) {
                            conf.mask = kv[1];
                            Log.i("kuiya", "掩码 "+conf.mask);
                        }
                    }
                }
            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ifconfigs;
    }

}
