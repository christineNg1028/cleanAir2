//CUCO2 User Application
//Christine Ng & Kelly Zhu
//Last Modified:

import java.io.*;
import java.util.*;
import java.lang.*;
import hsa.Console;
import java.awt.*;

public class UserApplication 
{  
  final static int CO2MAXLEVEL=450, SO2MAXLEVEL=1;
  final static double NO2MAXLEVEL=0.1;
  static Console c; //The output console
  
  public static void main(String[] args) throws IOException 
  {  
    c = new Console (25, 125); //Resize output console
    
    //Variables
    String userLocation, lung, heart, smoke, co2Safety, so2Safety, no2Safety;
    int userAge;
    double hourlyCO2 = 0, hourlySO2 = 0, hourlyNO2 = 0;
    double co2DailyAvg = 0, so2DailyAvg = 0, no2DailyAvg = 0, co2WeeklyAvg = 0, so2WeeklyAvg = 0, no2WeeklyAvg = 0;
    boolean sensitiveGroup = false, safeOrNot = true;
    
    //ArrayLists
    ArrayList <Double> co2HourlyLevels = new ArrayList <Double> ();
    ArrayList <Double> so2HourlyLevels = new ArrayList <Double> ();
    ArrayList <Double> no2HourlyLevels = new ArrayList <Double> ();
    ArrayList <Double> co2DailyAverages = new ArrayList <Double> ();
    ArrayList <Double> so2DailyAverages = new ArrayList <Double> ();
    ArrayList <Double> no2DailyAverages = new ArrayList <Double> ();
    
    //Get user's location
    c.println("Which GTA city are you in?");
    userLocation = c.readLine().trim();
    while (!userLocation.equalsIgnoreCase("markham")&&!userLocation.equalsIgnoreCase("toronto")&&!userLocation.equalsIgnoreCase("mississauga")
             &&!userLocation.equalsIgnoreCase("richmond hill")&&!userLocation.equalsIgnoreCase("brampton"))
    {
      c.println("No data available. Enter a different city.");
      userLocation = c.readLine().trim();
    }
    c.clear();
    
    //Construct Sensor object
    Sensor gasReader = new Sensor(userLocation);
    Thread detector = new Thread(gasReader);
    
    //Get user's personal info: age + health concerns
    c.println("Please answer the following to receive a better report of the potential health complications associated with air quality:");
    while (true)
    {
      c.println("\nWhat is your age?");
      String tempAge = c.readLine().trim();
      try{
        userAge = Integer.parseInt(tempAge);
        if (userAge>0){
          break;
        }
      }
      catch (NumberFormatException e){
      }
    }
    c.println("\nEnter 'y' if you have any lung or heart conditions, or are a smoker. Or enter 'n' for no.");
    String input = c.readLine().trim();
    while (!input.equalsIgnoreCase("y")&&!input.equalsIgnoreCase("n"))
    {
      c.println("Invalid input. Enter 'y' for yes or 'n' for no.");
      input = c.readLine().trim();
    }
    
    //Determine if sensitive group
    if (input.equalsIgnoreCase("y")||userAge > 64 || userAge < 15)
    {
      sensitiveGroup=true;
    }
    
    //Start detector Thread to start taking readings
    detector.start();
    
    //Receive data every hr (60s) 
    try{
      while (true){
        c.clear();
        Scanner dataReader = new Scanner(new File("gasLevelReadings.txt"));
        
        //Receive hourly readings
        hourlyCO2 = dataReader.nextDouble();
        hourlySO2 = dataReader.nextDouble();
        hourlyNO2 = dataReader.nextDouble();
        
        //Save hourly readings in array lists
        co2HourlyLevels.add(Double.valueOf(hourlyCO2));
        so2HourlyLevels.add(Double.valueOf(hourlySO2));
        no2HourlyLevels.add(Double.valueOf(hourlyNO2));
        
        //Update daily averages
        co2DailyAvg =  gasAverage(co2HourlyLevels);
        so2DailyAvg =  gasAverage(so2HourlyLevels);
        no2DailyAvg =  gasAverage(no2HourlyLevels);
        
        if (co2HourlyLevels.size()==24){
          //Clear all hourly data after 24h to mark new day
          co2HourlyLevels.clear();
          so2HourlyLevels.clear();
          no2HourlyLevels.clear();
          
          //Save daily averages in array lists
          co2DailyAverages.add(Double.valueOf(co2DailyAvg));
          so2DailyAverages.add(Double.valueOf(so2DailyAvg));
          no2DailyAverages.add(Double.valueOf(no2DailyAvg));
          
          //Update weekly averages
          co2WeeklyAvg = gasAverage(co2DailyAverages);
          so2WeeklyAvg = gasAverage(so2DailyAverages);
          no2WeeklyAvg = gasAverage(no2DailyAverages);
        }
        
        //Determine safe/unsafe levels of each gas
        co2Safety = getCO2Safety(hourlyCO2, sensitiveGroup);
        so2Safety = getSO2Safety(hourlySO2);
        no2Safety = getNO2Safety(hourlyNO2);
        
        //Output hourly readings, daily + weekly averages
        c.println(userLocation.toUpperCase() + "\n");
        c.println("Today's Averages\tCurrent Hourly Levels\tThis Week's Averages");
        c.println("CO2: " + co2DailyAvg + " ppm\tCO2: " + hourlyCO2 + " ppm\tCO2: " + co2WeeklyAvg + " ppm");
        colourIcon(co2Safety, 370, 75);
        c.println("SO2: " + so2DailyAvg + " ppm\t\tSO2: " + hourlySO2 + " ppm\t\tSO2: " + so2WeeklyAvg + " ppm");
        colourIcon(so2Safety, 370, 95);
        c.println("NO2: " + no2DailyAvg + " ppm\t\tNO2: " + hourlyNO2 + " ppm\t\tNO2: " + no2WeeklyAvg + " ppm");
        colourIcon(no2Safety, 370, 115);
        
        //Output safety level and message
        safeOrNot = overallSafe(co2Safety, so2Safety, no2Safety);
        if (safeOrNot == false){
           message(hourlyCO2, hourlySO2, hourlyNO2, sensitiveGroup);
           pollutionSources();
        }
        
        //Wait for next readings in database
        try{
          Thread.sleep(60000);
        }
        catch (InterruptedException e){
        }    
      }
    }
    catch (FileNotFoundException e){
    }
  }
  
  public static double gasAverage(ArrayList<Double> gas) 
  {
    double gasTotal = 0, gasAvg;
    
    //Caculate avg from all data currently stored in array list
    for (int i=0; i<gas.size(); i++) {
      gasTotal = gasTotal+gas.get(i);
    }
    gasAvg = gasTotal/gas.size();
    
    //Format gasAvg
    String gasAvgFormat = String.format("%.3f", gasAvg);
    gasAvg = Double.parseDouble(gasAvgFormat);
    
    return gasAvg;
  }
  //-------------------------------------------------------------------
  public static String getCO2Safety(double co2CurrentLevel, boolean healthConditions) {
    String co2Safety;

    if (co2CurrentLevel<=CO2MAXLEVEL) {
      co2Safety = "SAFE";
    }
    else if (co2CurrentLevel>CO2MAXLEVEL && co2CurrentLevel<=600) {
      if (healthConditions == false){
        co2Safety = "MODERATE";
      }
      else{
        co2Safety = "UNSAFE";
      }
    }
    else {
      co2Safety = "UNSAFE";
    }
 
    return co2Safety;
  }
//----------------  
  public static String getSO2Safety(double so2CurrentLevel) {
    String so2Safety;
    
    if (so2CurrentLevel<=SO2MAXLEVEL) {
      so2Safety = "SAFE";
    }
    else {
      so2Safety = "UNSAFE";
    }

    return so2Safety;
  }
  //-------------------
  public static String getNO2Safety(double no2CurrentLevel) {
    String no2Safety;
    
    if (no2CurrentLevel<=NO2MAXLEVEL) {
      no2Safety = "SAFE";
    }
    else {
      no2Safety = "UNSAFE";
    }
    
    return no2Safety;
  }
  //----------------
  public static void colourIcon(String gasSafety, int x, int y){
    if (gasSafety.equals("SAFE")){
      c.setColor(Color.green);
    }
    else if (gasSafety.equals("MODERATE")){
      c.setColor(Color.orange);
    }
    else{
      c.setColor(Color.red);
    }
    c.drawString("*", x, y);
  }
  //-------------------
  public static boolean overallSafe(String co2Safety, String so2Safety, String no2Safety) {
    if (co2Safety.equals("SAFE") && so2Safety.equals("SAFE") && no2Safety.equals("SAFE")){
      c.println("\nExternal Air Quality: SAFE\nIt is an optimal time to be outdoors.");
      return true;
    }
    else if (co2Safety.equals("MODERATE") && so2Safety.equals("SAFE") && no2Safety.equals("SAFE")){
      c.println("\nExternal Air Quality: MODERATE");
      return false;
    }
    else{
      c.println("\nExternal Air Quality: UNSAFE");
      return false;
    }
  }
  
  public static void message(double co2CurrentLevel, double so2CurrentLevel, double no2CurrentLevel, boolean healthConditions)
  {
    //CO2
    if (co2CurrentLevel>CO2MAXLEVEL && co2CurrentLevel<=600)
      if (healthConditions == false)
        c.println("MODERATE CO2 Level: Wear a mask outdoors to ensure your safety.");
      else
        c.println("UNSAFE CO2 Level: It is best to stay indoors at this time to avoid breathing problems.");
    else if (co2CurrentLevel>600 && co2CurrentLevel<=1000)
      c.println("UNSAFE CO2 Level: Watch out for odors in the air and stiffness in your body! Wear a mask while outdoors or stay indoors!");
    else if (co2CurrentLevel>1000 && co2CurrentLevel<=2500)
      c.println("UNSAFE CO2 Level: You may be feeling drowsy at the moment, evacuate the area and get indoors!");
    else if (co2CurrentLevel>2500 && co2CurrentLevel<=5000)
      c.println("UNSAFE CO2 Level: You may experience adverse health effects at this point, evacuate the area and get indoors immediately!");
    else if (co2CurrentLevel>5000 && co2CurrentLevel<=10000)
      c.println("UNSAFE CO2 Level: This is within the maximum allowed concentration range within a 8 hour working period, evacuate the area immediately and get indoors!");
    else if (co2CurrentLevel>10000 && co2CurrentLevel<=30000)
      c.println("UNSAFE CO2 Level: This is within the maximum allowed concentration range within a 15 minute working period, evacuate the area immediately and get indoors!");
    else if (co2CurrentLevel>30000 && co2CurrentLevel<=40000)
      c.println("UNSAFE CO2 Level: The air will be slightly intoxicating, your breathing and pulse rate will increase and you will feel nauseous. Evacuate the area immediately, get indoors and call for help!");
    else if (co2CurrentLevel>40000 && co2CurrentLevel<=50000)
      c.println("UNSAFE CO2 Level: You will have a pounding headache and will be slightly impaired. Evacuate the area immediately, get indoors and call for help!");
    else if (co2CurrentLevel>50000 && co2CurrentLevel<=100000)
      c.println("UNSAFE CO2 Level: You most likely fall unconscious and will face death if you do not evacuate. Get indoors immediately and call for help!");
    
    //SO2
    if (so2CurrentLevel>SO2MAXLEVEL && so2CurrentLevel<=2)
      if (healthConditions == false)
        c.println("UNSAFE SO2 Level: You may experience mild irritatation in your eyes, nose, throat, and sinuses, resulting in choking and coughing. It is best to stay indoors!");
      else
        c.println("UNSAFE SO2 Level: Since you fall within the sensitive group, you may experience severe irritatation in your eyes, nose, throat, and sinuses, resulting in choking and coughing. Evacuate the area immediately and get indoors!");
    else if (so2CurrentLevel>2 && so2CurrentLevel<=50)
      if (healthConditions == false)
        c.println("UNSAFE SO2 Level: The current level of sulfur dioxide is very risky for your health. Evacuate the area, get indoors and call for help!");
      else
        c.println("UNSAFE SO2 Level: Since you fall within the sensitive group, the current level of sulfur dioxide could be fatal. Evacuate the area, get indoors and call for help!");
    else if (so2CurrentLevel>50 && so2CurrentLevel<=100)
      if (healthConditions == false)
        c.println("UNSAFE SO2 Level: The current level of sulfur dioxide could be fatal. Evacuate the area, get indoors and call for help!");
      else
        c.println("UNSAFE SO2 Level: Since you fall within the sensitive group, the current level of sulfur dioxide is fatal. Evacuate the area, get indoors and call for help!");
    
    //NO2
    if (no2CurrentLevel>NO2MAXLEVEL && no2CurrentLevel<=5)
      if (healthConditions == false)
        c.println("UNSAFE NO2 Level: It is best that you stay home to avoid health complications, or limit your exposure and wear a mask.");
      else
        c.println("UNSAFE NO2 Level: This level of nitrogen dioxide produces negative health effects among vulnerable populations, including asthmatics and others with respiratory issues. Evacuate the area immediately and get indoors!");
    else if (no2CurrentLevel>5)
      if (healthConditions == false)
        c.println("UNSAFE NO2 Level: The air may cause serious health effects or discomfort. Evacuate the area, get indoors and call for help!");
      else
        c.println("UNSAFE NO2 Level: Since you have existing health issues, you may experience fatal health effects or severe discomfort. Evacuate the area, get indoors and call for help!");
  }
  
  public static void pollutionSources(){
    try{
      Scanner envReader = new Scanner(new File("pollutionCauses.txt"));
      c.println("\nPollution Sources:");
      while (envReader.hasNextLine()){
        c.println(envReader.nextLine());
      }
    }
    catch (FileNotFoundException e){
    }
  }
}