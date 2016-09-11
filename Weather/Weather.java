/**
 * La clase Weather se encarga de mandar los datos mas recientes recibidos de la estación meteorológica conectada
 * al equipo a la pagina web Weather Underground
 * 
 * @author Josep Dols @jodoldar 
 * @version 1.0 - First Release
 */
import java.util.*;
import java.io.*;
import java.net.*;

public class Weather {
    public static String sendData(String url) throws Exception{
        String result = "";
        URL direccion = new URL(url);
        HttpURLConnection con = (HttpURLConnection) direccion.openConnection();
        con.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while((line = rd.readLine())!=null){
            result = result + line;
        }
        rd.close();
        return result;
    }
    public static String parseCurrent(){
        //Los campos ID y Password estan sin completar para poder ser reutilizados
        String resultado = "https://weatherstation.wunderground.com/weatherstation/updateweatherstation.php?ID=XXXXXX&PASSWORD=XXXXXXX";
        try{
            Scanner in = new Scanner(new File("/home/pi/current.txt"));
            while(in.hasNext()){
                String aux = in.nextLine();
                if(aux.startsWith(" ")){
                    String auxTrim = aux.trim();
                    //Bloque para introducir la hora actual
                    if(auxTrim.startsWith("dateTime:")){
                        resultado = resultado + "&dateutc=now";
                    //Bloque para introducir la humedad relativa
                    }else if(auxTrim.startsWith("h_1:")){
                        String[] parts = auxTrim.split(":");
                        int valor = Integer.parseInt(parts[1].trim());
                        resultado = resultado + "&humidity=" + valor;
                    //Bloque para introducir la presión atmosferica
                    }else if(auxTrim.startsWith("slp:")){
                        String[] parts = auxTrim.split(":");
                        float valor = Float.parseFloat(parts[1].trim());
                        float valorIn = valor * (float)0.02952998751; 
                        resultado = resultado + "&baromin=" + valorIn;
                    //Bloque para introducir la temperatura
                    }else if(auxTrim.startsWith("t_1:")){
                        String[] parts = auxTrim.split(":");
                        float valor = Float.parseFloat(parts[1].trim());
                        float valorF = ((valor*9)/5) + 32;
                        resultado = resultado + "&tempf=" + valorF;
                    //Bloque para introducir la dirección del viento
                    }else if(auxTrim.startsWith("winddir:")){
                        String[] parts = auxTrim.split(":");
                        int valor = Integer.parseInt(parts[1].trim());
                        resultado = resultado + "&winddir=" + valor;
                    }
                }
            }
        }catch(FileNotFoundException e){
            System.err.println("Hay fallo");
        }
        //Ultimos bloques necesarios para completar el envio
        resultado = resultado + "&softwaretype=TFASinus&action=updateraw";
        return resultado;
    }
    public static void main(String[] args){
        String currentURL = parseCurrent();
        try{
            String result = sendData(currentURL);
            System.out.println(result);
        }catch(Exception e){
            System.err.println("Fallo en el envio");
        }
    }
}

