package com.company;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

// Get all the Israeli laws by using oData service

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        HashMap<String, Queue<Law>> lawsHash = new HashMap();

        String nextPage = "https://knesset.gov.il/Odata/ParliamentInfo.svc/KNS_IsraelLaw";
        while(nextPage != null) {
            String xml = getText(nextPage);
            String rest = getXmlPageLaws(xml, lawsHash);
            nextPage = getNextPage(rest);
        }

        String laws = "";
        for (Map.Entry<String, Queue<Law>> set : lawsHash.entrySet()) {
            laws += set.getKey() + "\n\n";
            for (Law law: set.getValue()) {
                laws += law.getName() + "\n";
            }
            laws += "\n\n\n";
        }

        createTxtFile("lawsFile", laws);
    }

    public static String getXmlPageLaws(String xml, HashMap<String, Queue<Law>> lawsHash) {
        while (xml.indexOf("<entry") > -1) {
            int entryStart = xml.indexOf("<entry>");
            int entryEnd = xml.indexOf("</entry>");
            String entry = xml.substring(entryStart, entryEnd + 8);

            int entryContentStart = entry.indexOf("<content");
            int entryContentEnd = entry.indexOf("</content>");
            String entryContent = entry.substring(entryContentStart, entryContentEnd + 10);

            int lawNameStart = entryContent.indexOf("<d:Name");
            int lawNameEnd = entryContent.indexOf("</d:Name>");
            String lawName = entryContent.substring(lawNameStart + 8, lawNameEnd);
            if (lawName.indexOf(">") >= 0)
                lawName = lawName.substring(lawName.indexOf(">") + 1, lawName.length() - 1);

            int lawStatusStart = entryContent.indexOf("<d:LawValidityDesc");
            int lawStatusEnd = entryContent.indexOf("</d:LawValidityDesc");
            String lawStatus = entryContent.substring(lawStatusStart + 19, lawStatusEnd);

            Law law = new Law(lawName, lawStatus);

            if(!lawsHash.containsKey(lawStatus)) {
                Queue<Law> q = new LinkedList();
                lawsHash.put(lawStatus, q);
            }
            Queue<Law> statusQueue = lawsHash.get(lawStatus);
            statusQueue.add(law);

            xml = xml.substring(entryEnd + 8);
        }

        return xml;
    }

    public static String getNextPage(String xml) {
        int urlStart = xml.indexOf("href=");
        int urlEnd = xml.indexOf("/>");

        if(urlStart < 0)
            return null;

        String url = xml.substring(urlStart + 6, urlEnd - 2);
        return url;
    }

    public static void createTxtFile(String FileName,String context) {
        try {
            File myObj = new File(FileName + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter(FileName + ".txt");
            myWriter.write(context);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static String getText(String url) throws IOException {
        //method call for generating json

        URL myurl = new URL(url);
        HttpURLConnection con = (HttpURLConnection)myurl.openConnection();
        con.setUseCaches(false);
        con.setRequestProperty("Content-Type", "text/html; charset=utf-8");
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setRequestProperty("Connection", "keep-alive");

        StringBuilder sb = new StringBuilder();
        int HttpResult =con.getResponseCode();
        if(HttpResult ==HttpURLConnection.HTTP_OK){
            BufferedReader br = new BufferedReader(new   InputStreamReader(con.getInputStream(),"utf-8"));

            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            System.out.println(""+sb.toString());

        }else{
            System.out.println(con.getResponseCode());
            System.out.println(con.getResponseMessage());
        }

        return sb.toString();
    }
}

