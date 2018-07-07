package com.ri.wse.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class writer {

    public void writerFile(List<String> a, int i) throws IOException {
        File file = new File("./Pagclean/"+i +".txt.clean");

        // Se o arquivo nao existir, ele gera
        if (!file.exists()) {
            file.createNewFile();
        }

        // Prepara para escrever no arquivo
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        for(String str: a) {
            fw.write(str);
        }
        fw.close();
    }
}
