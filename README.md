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
 - Java Runtime Enviroment (JRE) 11

This distribution contains the following files:
-------------------------------------------------

```
   history.txt                - History of NTag
   license.txt                - License information
   readme.txt                 - This file
   tag_mapping.pdf            - Tag mapping documentation   
   libs/jaudiotagger-*.jar    - Jaudiotagger Tagging library 
   ntag.jar                   - Executable Java program   
   
Tag-Editor

   ntag.desktop               - Linux Launcher
   ntag.exe                   - Windows executable (jar wrapper)
   ntag_logging.properties    - Logging and Debug settings
```

Build with Maven:
--------------------------

```
mvn clean package exec:java -p profile
```

profiles: windows, linux

Download & Installation:
--------------------------

Arch Linux AUR package:

https://aur.archlinux.org/packages/ntag

Others:

https://github.com/nrittsti/ntag/releases

Launch from command line:
--------------------------

```
java --module-path libs --add-modules=javafx.controls,javafx.fxml,javafx.swing,java.logging,jaudiotagger,java.desktop,java.xml.bind -jar ntag.jar
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
