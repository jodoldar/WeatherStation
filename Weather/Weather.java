/**
 * La clase Weather se encarga de mandar los datos mas recientes recibidos de la estación meteorológica conectada
 * al equipo a la pagina web Weather Underground
 * 
 * @author Josep Dols @jodoldar 
 * @version 1.2 - Third Release
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
            double temperatura=0, humedad=0, dewpoint;
            while(in.hasNext()){
                String aux = in.nextLine();
                if(aux.startsWith(" ")){
                    String auxTrim = aux.trim();
                    //Bloque para introducir la hora actual
                    if(auxTrim.startsWith("dateTime:")){
                        resultado = resultado + "&dateutc=now";
                    }else if(auxTrim.startsWith("h_1:")){   //Bloque para introducir la humedad relativa
                        String[] parts = auxTrim.split(":");
                        int valor = Integer.parseInt(parts[1].trim());
                        resultado = resultado + "&humidity=" + valor;
                    }else if(auxTrim.startsWith("slp:")){   //Bloque para introducir la presión atmosferica
                        String[] parts = auxTrim.split(":");
                        float valor = Float.parseFloat(parts[1].trim());
                        float valorIn = valor * (float)0.02952998751;   //Cambio de unidad: mBar -> inHg
                        resultado = resultado + "&baromin=" + valorIn;
                    }else if(auxTrim.startsWith("t_1:")){   //Bloque para introducir la temperatura
                        String[] parts = auxTrim.split(":");
                        float valor = Float.parseFloat(parts[1].trim());
                        float valorF = ((valor*9)/5) + 32;              //Cambio de unidad: ºC -> ºF
                        resultado = resultado + "&tempf=" + valorF;
                    }else if(auxTrim.startsWith("winddir:")){   //Bloque para introducir la dirección del viento
                        String[] parts = auxTrim.split(":");
                        int valor = Integer.parseInt(parts[1].trim());
                        double valorReal = valor * 22.5;        //Adaptar escala [0-15] -> [0-337,5]
                        resultado = resultado + "&winddir=" + valorReal;
                    }else if(auxTrim.startsWith("windgust:")){
                        String[] parts = auxTrim.split(":");
                        float valor = Float.parseFloat(parts[1].trim());
                        resultado = resultado + "&windgustmph=" + valor;
                    }else if(auxTrim.startsWith("windspeed:")){
                        String[] parts = auxTrim.split(":");
                        float valor = Float.parseFloat(parts[1].trim());
                        resultado = resultado + "&windspeedmph=" + valor;
                    }
                }
            }
            //Cálculo del punto de rocio a partir de los valores obtenidos de temperatura y humedad
            dewpoint = Math.pow((humedad/100),0.125);
            dewpoint = dewpoint * (112+(0.9*temperatura));
            dewpoint = dewpoint + (temperatura*0.1);
            dewpoint = dewpoint -112;
            double dewpointF = ((dewpoint*9)/5)+32;
            resultado = resultado + "&dewptf=" + dewpointF;
        }catch(FileNotFoundException e){
            System.err.println("Hay fallo");
        }
        //Ultimos bloques necesarios para completar el envio
        resultado = resultado + "&rainin=0&softwaretype=TFASinus&action=updateraw";
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

