import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {

        //Initierar alla objekt.
        ServerSocket serverSocket;
        Socket socket;
        InputStreamReader inputStreamReader;
        OutputStreamWriter outputStreamWriter;
        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;

        //Starta Servern
        try {
            //Kontrollera att Socket nummer är ledigt. Avbryt om socket är upptagen
            serverSocket = new ServerSocket(8080);
        }
        catch (Exception e)  {
            System.out.println(e);
            return;
        }

        while (true){
            try {
                //Väntar på specifik socket efter trafik
                socket = serverSocket.accept();

                //Initierar Reader och Writer och kopplar dem till socket
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

                bufferedReader = new BufferedReader(inputStreamReader);
                bufferedWriter = new BufferedWriter(outputStreamWriter);

                while (true){
                    //Hämta och klientens meddelande och skicka den till funktion
                    String message = bufferedReader.readLine();

                    String returnData = openUpData(message);

                    System.out.println("Message recieved and sent back!");

                    //Skriver ut JSONObjekt
                    bufferedWriter.write(returnData);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    //Avsluta om användaren skriver quit
                    if (message.equalsIgnoreCase("exit")){
                        break;
                    }
                }
                //Stäng kopplingar
                socket.close();
                inputStreamReader.close();
                outputStreamWriter.close();
                bufferedReader.close();
                bufferedWriter.close();
            }
            catch (Exception e){
                System.out.println(e);
            }
        }
    }

    //Metod för att öppna upp data
    static String openUpData (String message) throws ParseException, IOException {
        //Steg 1. Bygg upp JSON object baserat på inkommande string
        JSONParser parser = new JSONParser();
        JSONObject jsonOb = (JSONObject) parser.parse(message);

        //Steg 2. Läs av URL och HTTP-Metod för att veta vad Klienten vill
        String url = jsonOb.get("httpURL").toString();
        String method = jsonOb.get("httpMethod").toString();

        //Steg 3. Dela upp URL med .split() metod
        String[] urls = url.split("/");

        //Steg 4. Använd en SwitchCase för att kolla vilken data som ska användas
        switch (urls[0]) {
            case "allPersons": {
                if (method.equals("get")) {
                    //Vill hämta data om personer
                    //TODO Lägg till logik om man vill hämta all information om en person
                    //TODO Lägg till logik om man vill hämta specifik information om en specifik person

                    //Skapa JSONReturn objektet
                    JSONObject jsonReturn = new JSONObject();
                    //Hämta data från JSON-fil
                    jsonReturn.put("data", parser.parse(new FileReader("src/data.json")).toString());

                    //Inkluderar HTTP status Code
                    jsonReturn.put("httpStatusCode", 200);

                    //Returnera JSON-String
                    return jsonReturn.toJSONString();
                }
                break;
            }
            case "personOne" : {
                if (method.equals("get")) {

                    //Skapa JSONReturn objektet
                    JSONObject jsonReturn = new JSONObject();
                    //Hämta data från JSON-fil
                    jsonReturn.put("p1", parser.parse(new FileReader("src/data.json")).toString());

                    //Inkluderar HTTP status Code
                    jsonReturn.put("httpStatusCode", 200);

                    //Returnera JSON-String
                    return jsonReturn.toJSONString();
                }
            }
        }

        return "Message received";
    }
}