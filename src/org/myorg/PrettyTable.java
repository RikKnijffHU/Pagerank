package org.myorg;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Roelant Ossewaarde on 24/02/2017.
 */
public class PrettyTable {
    static private String CHARS = "abcdefghijklmnopqrstuvwxyz";
    Map<Character, HashMap<Character, Double>> table = new HashMap<Character, HashMap<Character, Double>>();

    public PrettyTable() {
        for (int i = 0; i<CHARS.length(); i++) {
            table.put(CHARS.charAt(i), new HashMap<Character, Double>());
            for (int j = 0; j<CHARS.length(); j++) {
                table.get(CHARS.charAt(i)).put(CHARS.charAt(j), 0.0);
            }
        }
    }

    private List<String> getFilenames(String path) {
        List<String> results = new ArrayList<String>();


        File[] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        return results;
    }

    private String readFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String output = "";
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            output = sb.toString();
        } finally {
            br.close();
        }
        return(output);
    }

    public void parseMROutput(String pathname) {
        try {
            StringBuffer buffer = new StringBuffer();
            // read in all Hadoop output files
            for (String filename : getFilenames(pathname))
            {
                if (filename.startsWith("part")) buffer.append(readFile(pathname+"/"+filename));
            }
            String output = buffer.toString();
            String[] lines = output.split("\n");
            // parse lines and store values in HashMap.
            for (String line : lines) {
                if (line.split("\t")[0].length() > 1) {
                    Character firstChar = line.split("\t")[0].charAt(0);
                    Character secondChar = line.split("\t")[0].charAt(1);
                    if ((table.containsKey(firstChar) && table.get(firstChar).containsKey(secondChar))) {
                        Double originalValue = table.get(firstChar).get(secondChar);
                        table.get(firstChar).put(secondChar, originalValue + Double.parseDouble(line.split("\t")[1]));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void percentageOutput(){
    	
    	for (int i = 0; i<CHARS.length(); i++) {
            table.get(CHARS.charAt(i));
            for (int j = 0; j<CHARS.length(); j++) {
                table.get(CHARS.charAt(i)).put(CHARS.charAt(j), 0.0);
            }
        }
    	
    for(int i = 0; i<table.size();i++){
    	for(int b = 0;b<table.size();b++){
    		
    	}
    }
    }

    public void printText() {
        // print first line, header.
        System.out.print("   ");
        for (Character firstChar : CHARS.toCharArray()) {
            System.out.print(" "+ firstChar + " ");
        }
        System.out.println();

        for (Character firstChar : CHARS.toCharArray()) {
            System.out.print(firstChar+": ");
            for (Character secondChar : CHARS.toCharArray()) {
                System.out.print(" "+table.get(firstChar).get(secondChar)+" ");

            }
            System.out.println("\n");

        }

    }
    public void printHTML(String filename ) {
        try{
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            // print first line, header.
            writer.println("<html>");
            writer.println("<table>");
            writer.println("<thead>");
            writer.println("<tr><th></th>");
            for (Character firstChar : CHARS.toCharArray()) {
                writer.print("<th>"+ firstChar + "</th>");
            }
            writer.println("</tr>");
            writer.println("</thead></tbody>");

            for (Character firstChar : CHARS.toCharArray()) {
                writer.println("<tr><td>");
                writer.print(firstChar+": ");
                writer.println("</td>");
                for (Character secondChar : CHARS.toCharArray()) {
                    writer.print("<td>"+table.get(firstChar).get(secondChar)+"</td>");

                }
                writer.println("</tr>");

            }
            writer.println("</tbody>");
            writer.print("</table>");
            writer.print("</html>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
