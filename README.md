# NTag (audio file tag editor)

![ScreenShot](https://raw.github.com/nrittsti/ntag/master/ntag.png)

Main features: 
-------------------
  - Viewing and editing tags in audio files
  - Supported tag fields: title, artist, album, track, disc, year, date, comment, composer, album artist, language, lyrics, genre, compilation
  - Supported audio formats: MP3, MP4 (M4A, M4B), FLAC, OGG and WMA
  - Supported tag formats: ID3v1.1, ID3v2.3, ID3v2.4, MP4, ASF and Vorbis Comment
  - Batch editing of multiple files
  - Extended tag field editor (create, edit, delete)
  - Supported cover artwork formats : JPG, PNG 
  - Resize/Shrink cover artwork
  - Rename files based on the tag information
  - Filter files by missing artwork/lyrics/metadata
  - Portable Java App
  
System Requirements :
----------------------

 - 250MB of free RAM
 - OpenJDK 12 or newer

This distribution contains the following files:
-------------------------------------------------

```
   history.txt                - History of NTag
   license.txt                - License information
   readme.txt                 - This file
   tag_mapping.pdf            - Tag mapping documentation   
   lib/jaudiotagger-*.jar     - Jaudiotagger Tagging library 
   ntag.jar                   - Executable Java program   
   
Tag-Editor

   ntag.desktop               - Linux Launcher
   ntag_logging.properties    - Logging and Debug settings
```

Build with Gradle:
-----------------

```
gradle clean build run
```

gradle.properties: platform=linux or platform=win

Download & Installation:
--------------------------

Arch Linux AUR package:

https://aur.archlinux.org/packages/ntag

Others:

https://github.com/nrittsti/ntag/releases

OpenJDK Windows 10 Installation
--------------------------

Download OpenJDK from

https://jdk.java.net/java-se-ri/12

Extract the zip file into a folder, e.g. C:\Program Files\Java\ and it will create a jdk-12 folder.

Set PATH:
- Select Control Panel and then System.
- Click Advanced and then Environment Variables.
- Add the location of the bin folder of the JDK installation to the PATH variable in System Variables.
- The following is a typical value for the PATH variable: C:\WINDOWS\system32;C:\WINDOWS;"C:\Program Files\Java\jdk-12\bin"

Set JAVA_HOME:
- Under System Variables, click New.
- Enter the variable name as JAVA_HOME.
- Enter the variable value as the installation path of the JDK (without the bin sub-folder).
- Click OK. 
- Click Apply Changes.

Launch from command line:
--------------------------

```
java --module-path lib --add-modules=javafx.controls,javafx.fxml,javafx.swing,java.logging,jaudiotagger,java.desktop,java.xml.bind -jar ntag.jar
```

Options:

```
-h --home         custom home directory
-p --portable     use working directory as home
```


Project Web site :
--------------------

https://github.com/nrittsti/ntag/

--------------------------------------------------------------------------------
Licence:
--------------------------------------------------------------------------------

NTag is free software: you can redistribute it and/or modify  
it under the terms of the GNU General Public License as published by  
the Free Software Foundation, either version 3 of the License, or  
(at your option) any later version.

NTag is distributed in the hope that it will be useful,  
but WITHOUT ANY WARRANTY; without even the implied warranty of  
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
GNU General Public License for more details.  

You should have received a copy of the GNU General Public License  
along with NTag.  If not, see <http://www.gnu.org/licenses/>.

Copyright 2018, Nico Rittstieg

--------------------------------------------------------------------------------
Third party libraries used by NTag
--------------------------------------------------------------------------------

Nuvola Icon Theme  
Autor:   David Vignoni  
Licence: LGPL

JAudiotagger Library 2.2.4  
Autor:   http://www.jthink.net/jaudiotagger/  
Licence: LGPL

--------------------------------------------------------------------------------
End of document
