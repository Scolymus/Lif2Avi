# Lif2Avi

This software extracts all videos and photos from .lif files into avi videos using Fiji with just pressing a button. 

**HOW TO INSTALL IT IN ECLIPSE**

1) File->Import->Git->Projects from Git (with smart import)
2) Clone URI
3) Write "https://github.com/Scolymus/Lif2Avi" in URI textfield (without quotes)
4) Next
5) Next
6) Next
7) Finish

**HOW TO RUN IT FROM ECLIPSE**

1) Run->Run as->Maven build...
2) Write "clean compile exec:java -Dexec.mainClass=main.Lif2Avi" (without quotes) in Goals textfield
3) Apply
4) Run

# How can I load the videos? #

There are 2 ways:

1. Open directly the plug-in. If no image is opened, it will ask you to open the .lif/images you want to open.

2. Open the files you needed and then, open the plug-in.


No problem will be found if you open images after the plugin is loaded.

# I want to open a .lif project. What should I check? #

After selecting the .lif project, just click open. After it, you will see a window as in the image. Please, **be sure to have only checked** the "use virtual stack" (blue). Only select the "Display metadata" (red) if you want to read value for FPS, magnification, time interval and binning automatically from the data stored in your .lif project. Finally, press "OK". After clicking, a new window will be opened. Here you can select all the videos you want to convert. Notice that there is a button avalaible to select all of them at the bottom of the window. Accept this window.

![Bioformats options](https://github.com/Scolymus/Lif2Avi/blob/master/Help/Bioformats_options.PNG?raw=true)

# Do I will have any problem if my .lif contains an image? #

Absolutely not! Images files will be written as .tif images and can include the scale bar.

# Can I convert many .lif files at once? #

Yes! Just go to the "Bulk process" tab and add the files that you want in this box (red). After checking for other options that you would want to activate/configurate, go to the "General tab" and activate "Bulk process" (blue). Finally, you can press "SAVE VIDEOS!" (black).

**NOTE**: By choosing this option, all videos/images included in each .lif project will be converted. Options to be converted will be get from the metadata file and if no data is included there, it will be taken from default values that will appear in each corresponding section. Notice that no user interaction is needed after pressing "SAVE VIDEOS!". All the process will be done automatically.

![Adding in bulk](https://github.com/Scolymus/Lif2Avi/blob/master/Help/save_videos.PNG?raw=true)
![Bulk option](https://github.com/Scolymus/Lif2Avi/blob/master/Help/bulk_process.PNG?raw=true)

# How do I save my videos? #

Just click in "SAVE VIDEOS!"

# Where are my videos going to be saved? #

By default, the plug-in gets the route of the first opened image. In case you have opened a lif project, all videos will be saved in the same folder where your lif project is. Actually, you can see the route where they are going to be saved in the text box next to "Save in...". You can put any other folder if you want by modifying directly this text or by clicking the "Browse..." button. If folder does not exist, a question dialog will be opened to cconfirm the creation of this folder.

# What if I have recorded videos with different FPS? #

You need to open the metadata file stored in the lif project. If it is opened, this textbox will be green. In case you see it green, FPS will be introduced deppending on the metadata file provided. If the image being saved does not exist in the lif metadata, the value of the textbox will be selected.

# What is the "mode" option? #

ImageJ give the opportunity to compress the data by converting each frame to a jpeg image or to a png image. Jpeg create smaller files but its quality its lower.

# What is the "Divide videos"? #

Sometimes, you can have very huge videos. In that case, in the process of creating the video it could happen that the PC runs out of RAM. This will ruin all the conversion. To avoid this, you can activate this option. This will let to the plug-in to calculate the amount of RAM avalaible and depending this and the size of every frame it will make the longest video it can make without running out of memory. By doing this, your video will be splitted in different videos which later [you will be able to join all of them](https://bitbucket.org/microswimmers/auto_lif2avi/wiki/Configuring%20ffmpeg). This is specially important when you include the scale bar and/or the time stamp.

# Can I also save the metadata information as an extra file? #

Yes! You need to be sure that the option "Write metadata in a file" (under "Other options" tab) is selected. If it is, a new text file called "metadata.txt" will be created at the same directory where videos are going to be saved.

# How do I include a scale bar?

Scale bar can be added into **all** your videos by just clicking at "Include scale bar" (blue). After activating it, the drop list down the check box will activated (red). By default, "Local input" is selected. "Local input" will get all the parameters introduced at the "Scale bar" tab. Otherwise, other saved configurations can be selected.

![Adding a scale bar](https://github.com/Scolymus/Lif2Avi/blob/master/Help/scale_1.PNG)

#Which parameters can I introduce?

Scale bar will be added by just calling the inner ImageJ plug-in. Therefore, the parameters you can change are the same ones that you will change when adding the scale bar in ImageJ.

* **Magnification**: This is the objective magnification. It is very important to introduce the correct magnification as this will include the correct size of the image.
* **Binning**: This is the binning option present in Leica's microscopes. When binning is used, images are taken at lower resolution. It is very important to introduce the correct binning as this will include the correct size of the image.
* **Units**: This is the units of the scale bar. If the option "Do not show units" is selected, no units will be shown in the image but the scale bar will be calculated as it would be in micrometers. **NOTE!:** *Still in version 4.0, this is only for text including. Conversion will be always done within micrometers and scale bar will be printed as micrometers. This option will only write something as unit, but will not affect into the conversion.*
* **Font size, serif font, bold and colour** (next to overlay) refer to label format for just the letters of the scale (eg. 1 um).
* **Overlay**: This is an option included at ImageJ plug-in that writes the label of units closer to the the scale.

-------------------------------------------------------------------------------------------------------

* **Scale bar size**: Length of the scale in the units indicated at "Units". If the option "Do not show units" is selected, the length of the scale bar will be calculated as it would be in micrometers.
* **Height (px)**: Height that will have the scale bar in pixels.
* **Lable all frames**: All the video will have the scale bar included, if this option is not selected, only the first frame will have the scale bar.
* **Location**: Location at where the scale bar will be drawed.
* **Colour** (next to "Save configuration!" button): This is the colour of only the bar.

-------------------------------------------------------------------------------------------------------

* **Save configuration!**: All the data introduced in this tab will be recorded. By doing this, next time this configuration will be avalaible by going to the drop list seen in Image 1, red.

![Saving scale bar](https://github.com/Scolymus/Lif2Avi/blob/master/Help/scale_2.PNG)

#What happens if I do not remember the magnification/binning/my videos have different magnification/binning within the .lif file?

Luckly, lif2avi plug-in can help you with these problems. If you still have your data in a .lif project, all the magnification and binning values for all the videos within the .lif file are stored in the .lif file. To read directly from the .lif project, please **be sure to load also the metadata from the .lif project**. When you mark this option, maginifcation and binning drop list will be coloured in green. If after loading the images you load the metadata, please change the tab just to be sure that the plugin will detect it. If the metadata do not include the needed information for some of your images, default values will be loaded from the input given at the "Scale bar" tab.

# How do I include the time?

Time can be added into **all** your videos by just clicking at "Include time" (blue). After activating it, the drop list down the check box will activated (red). By default, "Local input" is selected. "Local input" will get all the parameters introduced at the "Time stamper" tab. Otherwise, other saved configurations can be selected.

![Adding time](https://github.com/Scolymus/Lif2Avi/blob/master/Help/time_1.PNG)

#Which parameters can I introduce?

Time will be added by just calling the inner ImageJ plug-in. Therefore, the parameters you can change are the same ones that you will change when adding the time in ImageJ.

* **Starting time**: This is the time that will be printed in the first frame.
* **Interval**: This is the time interval between 2 frames.
* **Font size and antialising** refer to label format.
* **00:00 / units / decimal places**: Time can be printed as 00:00 or as in the format XXXX units. Decimal places in the time stamp can be added.
* **Location**: Location at where the time will be drawed.

-------------------------------------------------------------------------------------------------------

* **Save configuration!**: All the data introduced in this tab will be recorded. By doing this, next time this configuration will be avalaible by going to the drop list seen in Image 1, red.

![Adding time](https://github.com/Scolymus/Lif2Avi/blob/master/Help/time_2.PNG)

#What happens if I do not remember the time interval/my videos have different time interval within the .lif file?

Luckly, lif2avi plug-in can help you with these problems. If you still have your data in a .lif project, all the interval values for all the videos within the .lif file are stored in the .lif file. To read directly from the .lif project, please **be sure to load also the metadata from the .lif project**. When you mark this option, interval text box will be coloured in green. If after loading the images you load the metadata, please change the tab just to be sure that the plugin will detect it. If the metadata do not include the needed information for some of your images, default values will be loaded from the input given at the "Time stamper" tab.

# What is ffmpeg?

FFMPEG is a cross-platform solution to record, convert and stream audio and video. It includes libavcodec - the leading audio/video codec library. It has been developed since earlies 2000 and today, is one of the open source projects best known. The name of the project is inspired by the MPEG video standards group, together with "FF" for "fast forward".

**Why do I need ffmpeg?**

You need it if you want to concat the videos that you create with the option "Split videos".

**How do I configure it?**

First, download last version from [here](https://www.ffmpeg.org/download.html). If you are using Windows, Shared library version is ok ([64 bits](https://ffmpeg.zeranoe.com/builds/win64/shared/ffmpeg-20160828-a37e6dd-win64-shared.zip) / [32 bits](https://ffmpeg.zeranoe.com/builds/win32/shared/ffmpeg-20160828-a37e6dd-win32-shared.zip)). After it, extract all the files into a new folder. Next, introduce the route where you unzipped all in the textbox that you can see at the tab "Other options", next to "ffmpeg directory" (blue). Finally, press "Save configuration!" to avoid introducing this location each time you open the program. If the directoy is correct, after converting all the splitted videos it will start to join them automatically.

![ffmpeg](https://github.com/Scolymus/Lif2Avi/blob/master/Help/ffmpeg.PNG)
