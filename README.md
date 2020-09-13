# cleanAir2
#### Building a <em>cleanAir</em> city together.

### The Problem
Every year, around 7 million deaths are due to exposure to air pollution as anthropogenic transportation, industrialization, and resource production continues to release harmful aerosols into our atmosphere. Air pollution is a detrimental risk to public safety; causing lung cancer, stroke, and several other diseases, linking to an estimation of 14,000 premature deaths annually in Canada.

Adding on to this issue, current Air Quality Indexes that are available to the public present data in an inefficient way, often overloading with raw numbers, confusing colors, clustered graphs and complex diagrams. As a result, Canadians are discouraged from using these platforms, causing a lack of awareness surrounding the severity of air pollution in their location, the health complications that could arise from high exposure of aerosols, and when to take protective measures.

### The Solution
cleanAir2 is a web app that delivers specific recommendations tailored to the user quickly and easily. Users don't need to analyze tables to self-identify the group they fall under and their risk tolerance. Nor do they need to research various safety measures they can take.

The platform prompts users for personal data first– categorizing them accordingly by age, health and location, then immediately outputs personalized information. Gas concentrations of air pollution gases: carbon dioxide, sulfur dioxide, and nitrogen dioxide are displayed.

**NOTE: The website is not fully functioning yet, as JavaScript code is required for the backend portion. Due to time constraints, the frontend portion featuring the UI was completed on Wix and a POC Java program was developed to demonstrate the functionality of the application.**

### How it Works
1. Prompts user for their location, age and any health conditions

- To determine whether user falls within a sensitive group (children, seniors, people with health conditions) 
- Thresholds that determine whether air quality is safe/moderate/unsafe are different (ex. If air is moderate for healthy adult, would be unsafe for sensitive person)

![](https://github.com/christineNg1028/cleanAir2/blob/master/prompt1.jpg)![](https://github.com/christineNg1028/cleanAir2/blob/master/prompt1j.jpg)

![](https://github.com/christineNg1028/cleanAir2/blob/master/prompt2.jpg)![](https://github.com/christineNg1028/cleanAir2/blob/master/prompt3.jpg)

![](https://github.com/christineNg1028/cleanAir2/blob/master/prompt2and3j.jpg)

2. Every hour (60s just demonstration purposes), program displays current hourly levels in their location

- Lights beside each level indicating safe/moderate/unsafe
- Average calculations
- IoT equipment collects and stores real-time data in a database that the platform creates a data feed from to regularly update info

![](https://github.com/christineNg1028/cleanAir2/blob/master/concentrations.jpg)![](https://github.com/christineNg1028/cleanAir2/blob/master/outputj.jpg)

3. Safety Report

- Overall safety level depends on all 3 gases, translates into a safety meter to help people make more informed decisions
- If overall safety level is moderate/unsafe, the user is notified of potential health risks and informs them of protective measures they can take (ex. wear mask/stay indoors) based on the info they entered
- Any detected pollution sources (ex. mobile, stationary, area, natural) are displayed to back up the numbers and encourage users to follow safety guidelines

![](https://github.com/christineNg1028/cleanAir2/blob/master/safetyReport.jpg)
