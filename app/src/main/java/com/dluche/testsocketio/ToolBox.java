package com.dluche.testsocketio;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by neomatrix on 27/11/17.
 */

public class ToolBox {

    public static void writeFF(String mensagem, String fileName) {

        String path = System.getenv("EXTERNAL_STORAGE") + "/DBase";
        //
        File fileDirectory = new File(path);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdir();
        }
        FileWriter f;
        try {
            f = new FileWriter(path + "/" + fileName, true);
            f.append(mensagem + "\n");

            f.flush();
            f.close();

        } catch (Exception e) {
            Log.d("TAG", "Erro: " + e.toString());
        }
    }
}
