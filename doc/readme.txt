                                   README
--------------------------------------------------------------------------------
                       NTag (audio file tag editor)
--------------------------------------------------------------------------------

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

This distribution contains the following files:
-------------------------------------------------

   jre/                       - Java Runtime Enviroment
   lib/                       - Third party libraries
   *.metainfo.xml             - Appstream Flathub metadata
   history.txt                - History of NTag
   license.txt                - License information
   ntag.cmd                   - Windows Launcher
   ntag.exe                   - Windows exe (optional)
   ntag.sh                    - Linux CLI Launcher
   ntag.desktop               - Linux Desktop Launcher
   ntag.jar                   - Executable Java program
   ntag.desktop               - Linux Launcher
   ntag_logging.properties    - Logging and Debug settings
   readme.txt                 - This file
   tag_mapping.pdf            - Tag mapping documentation

Build with Gradle:
-------------------

./gradlew jre build run

gradle.properties: platform=linux or platform=win

Download & Installation:
--------------------------

Flatpak Linux package:

flatpak install flathub com.github.nrittsti.NTag
See: https://github.com/nrittsti/ntag/wiki/Flatpak

Arch Linux AUR package:

https://aur.archlinux.org/packages/ntag

Winget Package Manager for Windows:

See: https://github.com/microsoft/winget-cli

```
winget install NTag
```

Chocolatey Package Manager for Windows:

See: https://chocolatey.org/install

```
choco install ntag --version=1.2.11
```

Others:

https://github.com/nrittsti/ntag/releases

Launch from command line:
--------------------------

./jre/bin/java --module-path lib --add-modules=javafx.controls,javafx.fxml,javafx.swing,java.logging,jaudiotagger,java.desktop,java.json -jar ntag.jar

Options:

-h --home         custom home directory
-p --portable     use working directory as home

Linux HiDPI Settings:
--------------------------

Windows and MacOS take care of the scaling automatically.
Linux Java  applications using JavaFX can be scaled by defining the glass.gtk.uiScale VM property when invoking java.
The value can be an integer percentage value, an integer DPI value (where 96dpi represents a scale factor of 100%, and
for example 192dpi represents a scale factor of 200%), or a float value.

You can simply use the following configuration file

~/.config/ntag/hidpi.config

... and configure HiDPI Scalling in percent or dpi. For example:

HIDPI_SCALING=130%
HIDPI_SCALING=144dpi

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

Copyright 2021, Nico Rittstieg

--------------------------------------------------------------------------------
Third party libraries used by NTag
--------------------------------------------------------------------------------

Nuvola Icon Theme
Autor:   David Vignoni
Licence: LGPL

JAudiotagger Library 3.0.1
Autor:   https://www.jthink.net/jaudiotagger/
Licence: LGPL

Open JDK 17
Autor:   https://jdk.java.net/17/
Licence: GPL v2 with the Classpath Exception

OpenJFX
Autor:   https://github.com/openjdk/jfx/
Licence: GPL v2 with the Classpath Exception

--------------------------------------------------------------------------------
End of document
