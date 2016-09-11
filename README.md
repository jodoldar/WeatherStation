# WeatherStation
Small project to retrieve and send data to Weather Underground

This project is created to be used with other software and hardware at the same time. In the software part, it is based in the use of the
weewx libraries, to retrieve the data from the physical weather station (a TFA Sinus in my case); in the hardware part, there is a 
Raspberry Pi, which runs these software and process the information to send it to Weather Underground.

To make this project work, a text file with the current weather information is required to be placed in the home directory, to do this,
we have to use weewx; the exact command is:
<code>sudo wee_device --current > /home/pi/current.txt</code>

Once we have the text file, the java app automatically parse the context and send the data.

To make this work completely automatic, the best option is to use <code>cron</code>. In my opinion, a good time choice is to retrieve
data from the station every 10 minutes, and execute the java application every 11 minutes. With this configuration, both commands never
runs at the same time, making the weewx operation able to finish before the java application starts.
