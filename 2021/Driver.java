import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.PrintStream;
import java.util.HashMap;

public class Driver {
    
    public static String out;
    
    public static void main(String[] args) {
        
        if (args.length != 2) {
            System.out.println("Requires 1 argument: path to input file");
            return;
        }

        String path = args[0];
        out = args[1];

        try {
            
            Scanner sc = new Scanner(new File(path));
            
            var knowns = sc.nextLine().split(" ");
            int duration = Integer.parseInt(knowns[0]);
            int numIntersections = Integer.parseInt(knowns[1]);
            int numStreets = Integer.parseInt(knowns[2]);
            int numCars = Integer.parseInt(knowns[3]);
            int points = Integer.parseInt(knowns[4]);            

            ArrayList<ArrayList<String>> adjList = new ArrayList<ArrayList<String>>(numIntersections);
            for (int i = 0; i < numIntersections; i++) 
                adjList.add(new ArrayList<String>()); 

            ArrayList<Street> streetList = new ArrayList<Street>();
            for (int i=0; i < numStreets; i++) {
                var streetStr = sc.nextLine().split(" ");
                Street street = new Street();
                street.StreetStart = Integer.parseInt(streetStr[0]);
                street.StreetEnd = Integer.parseInt(streetStr[1]);
                street.StreetName = streetStr[2];
                street.StreetTravelTime = Integer.parseInt(streetStr[3]);
                streetList.add(street);

                adjList.get(street.StreetEnd).add(street.StreetName);
            }                   
            
            ArrayList<Car> carList = new ArrayList<Car>();
            for (int i=0; i < numCars; i++) {
                var carStr = sc.nextLine().split(" ");
                Car car = new Car();
                car.NumStreetsInPath = Integer.parseInt(carStr[0]);
                car.Path = new ArrayList<String>();
                for (int j=1; j < car.NumStreetsInPath + 1; j++) {
                    car.Path.add(carStr[j]);
                }
            }

            Result result = new Result();
            ArrayList<ResultIntersection> intersections = new ArrayList<ResultIntersection>();
            result.Intersections = intersections;

            for (ArrayList<String> intersection : adjList) {
                if (intersection.size() == 1){
                    intersections.add(CreateResultIntersection(adjList, intersection, intersection.get(0), duration));
                } else {
                    ResultIntersection resultIntersection = new ResultIntersection();
                    resultIntersection.ID = adjList.indexOf(intersection);
                    resultIntersection.NumIncomingStreets = intersection.size();
                    HashMap<String,Integer> schedule = new HashMap<String,Integer>();
                    for (String street : intersection){
                        schedule.put(street, 5);
                    }
                    resultIntersection.LightSchedule = schedule;
                    intersections.add(resultIntersection);
                }
            }

            buildOutput(result);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }    

    private static ResultIntersection CreateResultIntersection(ArrayList<ArrayList<String>> adjList, ArrayList<String> intersection, String incomingStreet, int duration) {        
        ResultIntersection resultIntersection = new ResultIntersection();
        resultIntersection.ID = adjList.indexOf(intersection);
        resultIntersection.NumIncomingStreets = intersection.size();
        HashMap<String,Integer> schedule = new HashMap<String,Integer>();
        schedule.put(incomingStreet, duration);
        resultIntersection.LightSchedule = schedule;

        return resultIntersection;        
    }

    private static void buildOutput(Result result) {                

        try {
            
            var outputFile = new File(out + ".txt");
            
            if (!outputFile.exists())
                outputFile.createNewFile();
                        
            var writer = new PrintStream(outputFile);
            
            writer.println(result.Intersections.size());
            
            for (int i = 0; i < result.Intersections.size(); i++) {
                
                writer.println(i);
                writer.println(result.Intersections.get(i).NumIncomingStreets);
                var streets = result.Intersections.get(i).LightSchedule.keySet();

                for (String street : streets) {
                    writer.println(street + " " + result.Intersections.get(i).LightSchedule.get(street));
                }
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Street {
    public int StreetStart;
    public int StreetEnd;
    public String StreetName;
    public int StreetTravelTime;
}

class Car {
    public int NumStreetsInPath;
    public ArrayList<String> Path;
}

class Result {    
    public ArrayList<ResultIntersection> Intersections;   
}

class ResultIntersection {
    public int ID = 0;
    int NumIncomingStreets = 0;
    HashMap<String,Integer> LightSchedule;    
}