import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.deploy.util.StringUtils;
import org.junit.Test;

public class main {

    String tomBeholder = "";
    String nettoBeholder = "";
    String bruttoBeholder = "";

    @Test
    public void test() {
        try (Socket socket = new Socket("127.0.0.1", 8000)) {
            OutputStream sos = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(sos);
            InputStream is = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String in;

            while (true) {
                // Get ID and name
                pw.println("RM20 8 \"INDTAST NR\" \"\" \"&3\"");
                pw.flush();
                in = reader.readLine();
                System.out.println(in);
                in = reader.readLine();
                System.out.println(in);

                // Wait for the GUI to update
                sleep(10);

                // Ask if the name is correct
                pw.println("P111 \"Er dit navn \'" + findID(in) + "\'?\"");
                pw.flush();
                in = reader.readLine();
                System.out.println(in);
                in = reader.readLine();
                System.out.println(in);
                // Figure out if the name is correct else run the code from the start
                if (p111Answer(in)) {
                    System.out.println("Answer is: 'JA'");
                    break;
                } else {
                    System.out.println("Answer is: 'NEJ'");
                    sleep(10);
                }
            }
            // Get batch number
            pw.println("RM20 8 \"INDTAST BATCH NR\" \"\" \"&3\"");
            pw.flush();
            in = reader.readLine();
            System.out.println(in);
            in = reader.readLine();
            System.out.println(in);
            String batch = findBatchName(in);

            while (true) {
                // Instructions about the weight
                pw.println("P111 \"VENLIGST UBELAST VÆGT, TRYK JA FOR AT FORTSÆTTE\"");
                pw.flush();
                in = reader.readLine();
                System.out.println(in);
                in = reader.readLine();
                System.out.println(in);
                if (p111Answer(in)) {
                    System.out.println("JA");
                    break;
                } else {
                    System.out.println("NEJ");
                    sleep(10);
                }
            }

            // Vægten Tareres
            pw.println("T");
            pw.flush();
            in = reader.readLine();
            System.out.println(in);

            while (true) {
                // Tom beholder på vægt
                pw.println("P111 \"VENLIGST PLACER TOM BEHOLDER PÅ VÆGT OG TRYK JA\" \"\" \"&3\"");
                pw.flush();
                in = reader.readLine();
                System.out.println(in);
                in = reader.readLine();
                System.out.println(in);

                if (p111Answer(in)) {
                    System.out.println("JA");
                    break;
                } else {
                    System.out.println("NEJ");
                }
            }

            pw.println("S");
            pw.flush();
            in = reader.readLine();
            System.out.println(in);
            tomBeholder = findKG(in);
            System.out.println("Den tomme beholder vejer: " + tomBeholder + "kg");

            // Vægten Tareres
            pw.println("T");
            pw.flush();
            in = reader.readLine();
            System.out.println(in);

            while (true) {
                // Beholder med produkt i
                pw.println("P111 \"VENLIGST PLACER BEHOLDER MED PÅ PRODUKT OG TRYK JA\" \"\" \"&3\"");
                pw.flush();
                in = reader.readLine();
                System.out.println(in);
                in = reader.readLine();
                System.out.println(in);
                if (p111Answer(in)) {
                    System.out.println("JA");
                    break;
                } else {
                    System.out.println("NEJ");
                }
            }

            pw.println("S");
            pw.flush();
            in = reader.readLine();
            System.out.println(in);
            nettoBeholder = findKG(in);
            System.out.println("Netto beholder vejer: " + nettoBeholder + "kg");

            // Vægten Tareres
            pw.println("T");
            pw.flush();
            in = reader.readLine();
            System.out.println(in);

            while (true) {
                // Fjern brutto
                pw.println("P111 \"VENLISGT FJERN BRUTTO VÆGT OG TRYK JA\" \"\" \"&3\"");
                pw.flush();
                in = reader.readLine();
                System.out.println(in);
                in = reader.readLine();
                System.out.println(in);
                if (p111Answer(in)) {
                    System.out.println("YES");
                    break;
                } else {
                    System.out.println("NEJ");
                }
            }

            pw.println("S");
            pw.flush();
            in = reader.readLine();
            System.out.println(in);
            bruttoBeholder = "" + findKG(in);
            System.out.println("Brutto beholder vejer: -" + bruttoBeholder + "kg");

            pw.println("T");
            pw.flush();
            in = reader.readLine();
            System.out.println(in);

            if (Double.parseDouble(tomBeholder) + Double.parseDouble(nettoBeholder) == Double.parseDouble(bruttoBeholder)) {
                pw.println("D OK");
                pw.flush();
                in = reader.readLine();
                System.out.println(in);
            } else {
                pw.println("D KASSER");
                pw.flush();
                in = reader.readLine();
                System.out.println(in);
            }

            sleep(1000);

            pw.println("Q");
            pw.flush();


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void sleep(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String findID(String in) {
        List<String> ID = new ArrayList<>();
        for (int i = 11; i < 99; i++) {
            ID.add("" + i);
        }

        String patternString = "\\b(" + StringUtils.join(ID, "|") + ")\\b";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(in);

        int id = 11;

        while (matcher.find()) {
            System.out.println("ID is: " + matcher.group(1));
            id = Integer.parseInt(matcher.group(1));
        }

        List<String> name = new ArrayList<>();
        name.add("Anders And");
        name.add("Stig");
        name.add("Christian");
        name.add("Nicklas");
        name.add("Hans");
        name.add("Rasmus");
        name.add("Mathias");

        for (int i = 11+name.size(); i < 99; i++) {
            name.add("None");
        }

        System.out.println("Name is: " + name.get(id - 11));
        return name.get(id - 11);
    }

    private Boolean p111Answer(String in) {
        List<String> answer = new ArrayList<>();
        answer.add("NEJ");
        answer.add("JA");

        String patternString = "\\b(" + StringUtils.join(answer, "|") + ")\\b";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(in);

        while (matcher.find()) {
            return matcher.group(1).equals(answer.get(1));
        }
        return false;
    }

    private String findKG(String in) {
        List<String> KG = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.000");
        df.setRoundingMode(RoundingMode.HALF_UP);
        for (double i = -6.000; i < 6.000; i += 0.100) {
            KG.add("" + df.format(i).replace(",", "."));
        }

        String patternString = "\\b(" + StringUtils.join(KG, "|") + ")\\b";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(in);

        String kg = "";

        while (matcher.find()) {
            //System.out.println("KG is: " + matcher.group(1));
            kg = "" + matcher.group(1);
        }

        return kg;
    }

    private String findBatchName(String in) {
        List<String> BN = new ArrayList<>();
        for (int i = 1000; i < 9999; i++) {
            BN.add("" + i);
        }

        String patternString = "\\b(" + StringUtils.join(BN, "|") + ")\\b";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(in);

        int bn = 1000;

        while (matcher.find()) {
            System.out.println("Batch NR is: " + matcher.group(1));
            bn = Integer.parseInt(matcher.group(1));
        }

        List<String> batch = new ArrayList<>();
        batch.add("Salt");
        batch.add("Sukker");
        batch.add("Pepper");
        batch.add("Bananer");
        batch.add("Æbler");
        batch.add("Guleroder");
        batch.add("Tomater");

        for (int i = 1000+batch.size(); i < 9999; i++) {
            batch.add("None");
        }

        System.out.println("Batch name is: " + batch.get(bn - 1000));
        return batch.get(bn - 1000);
    }
}
