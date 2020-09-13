//Sensor class
//Christine Ng
//Last Modified:
//Generates random gas levels within certain ranges depending on location and sends to the database in intervals

import java.io.*;
import java.util.*;
import java.lang.*;

public class Sensor implements Runnable
{
  //Attributes
  private double co2MinValue;
  private double co2MaxValue;
  private double so2MinValue;
  private double so2MaxValue;
  private double no2MinValue;
  private double no2MaxValue;
  private double co2Reading;
  private double so2Reading;
  private double no2Reading;
  
  //Constructor
  public Sensor(String loc){
    this.co2MinValue = 350;
    this.so2MinValue = 0;
      
    if (loc.equalsIgnoreCase("markham")||loc.equalsIgnoreCase("richmond hill"))
    {
      this.co2MaxValue = 460;
      this.so2MaxValue = 0.04;
      this.no2MinValue = 0.004;
      this.no2MaxValue = 0.014;
    }
    else if (loc.equalsIgnoreCase("mississauga"))
    {
      this.co2MaxValue = 472;
      this.so2MaxValue = 0;
      this.no2MinValue = 0.002;
      this.no2MaxValue = 0.01;
    }
    else if (loc.equalsIgnoreCase("toronto"))
    {
      this.co2MaxValue = 468;
      this.so2MaxValue = 0.04;
      this.no2MinValue = 0.008;
      this.no2MaxValue = 0.032;
    }
    else{ //brampton
      this.co2MaxValue = 438;
      this.so2MaxValue = 0;
      this.no2MinValue = 0;
      this.no2MaxValue = 0.036;
    }
  }
  
  //run method
  public void run()
  {
    while (true){
      this.hourlyReadings();
      
      if (this.co2Reading>450||this.so2Reading>1||this.no2Reading>0.1){
        this.pollutionCauses();
      }
      
      try{
        Thread.sleep(60000);
      }
      catch (InterruptedException e){
      }        
    }
  }
  
  //hourlyReadings method
  private void hourlyReadings()
  {
    this.co2Reading = Math.random()*(this.co2MaxValue-this.co2MinValue+1)+this.co2MinValue;
    this.so2Reading = Math.random()*(this.so2MaxValue-this.so2MinValue)+this.so2MinValue;
    this.no2Reading = Math.random()*(this.no2MaxValue-this.no2MinValue)+this.no2MinValue;
    
    try{
      PrintWriter out = new PrintWriter(new File("gasLevelReadings.txt"));
      out.printf("%-10.3f", this.co2Reading);
      out.printf("%-10.3f", this.so2Reading);
      out.printf("%-10.3f", this.no2Reading);
      out.close();
    }
    catch (FileNotFoundException e){
    }
  }
  
  //pollutionCauses method
  private void pollutionCauses()
  {
    try{
      PrintWriter out2 = new PrintWriter(new File("pollutionCauses.txt"));
      if (this.co2Reading>450 && this.co2Reading<=600||this.so2Reading>1 && this.so2Reading<=2
            ||this.no2Reading>0.1 && this.no2Reading<=5)
        out2.println("MOBILE – cars, buses, planes, trucks, and trains");
      if (this.co2Reading>600 && this.co2Reading<=1000||this.so2Reading>1 && this.so2Reading<=2
            ||this.no2Reading>0.1 && this.no2Reading<=5)
        out2.println("STATIONARY – power plants, oil refineries, industrial facilities, and factories");
      if (this.co2Reading>1000 && this.co2Reading<=2500||this.so2Reading>2 && this.so2Reading<=50
            ||this.no2Reading>0.1 && this.no2Reading<=5)
        out2.println("AREA – agricultural areas, cities, and wood burning fireplaces");
      if (this.co2Reading>2500||this.so2Reading>50 && this.so2Reading<=100||this.no2Reading>5)
        out2.println("NATURAL – wildfires, volcanoes, etc.");
      
      out2.close();
    }
    catch (FileNotFoundException e){
    }
  }
}