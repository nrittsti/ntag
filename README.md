# NTag (audio file tag editor)

<a href="https://github.com/nrittsti/ntag"><img alt="GitHub NTag status" src="https://github.com/nrittsti/ntag/workflows/Tests/badge.svg"></a>

![ScreenShot](https://raw.github.com/nrittsti/ntag/master/ntag.png)

NTag is a free, portable music tag editor for MP3, MP4 (M4A/M4B), FLAC, OGG and WMA
files. It lets you view, edit and organize the metadata of your music library —
one file at a time or hundreds at once.

Documentation: <https://github.com/nrittsti/ntag/wiki>

## Features

- View and edit tags for single files or **batches of files** at once
- Supported tag fields: title, artist, album, track, disc, year, date, comment,
  composer, album artist, language, lyrics, genre, rating and compilation flag
- Supported audio formats: **MP3, MP4 (M4A, M4B), FLAC, OGG, WMA**
- Supported tag formats: ID3v1.1, ID3v2.3, ID3v2.4, MP4, ASF and Vorbis Comment
- Extended low-level tag editor (create, edit, delete individual tag frames)
- Embedded cover artwork:
  - View, load, save, paste (clipboard) and remove artwork (JPG, PNG)
  - **Shrink / re-encode artwork** to fit device limitations (size, resolution, format)
- Rename files based on tag information with a custom pattern
- Renumber tracks — e.g. persist the current sort order or a shuffled playlist
- Quick filters to find files that are missing artwork, lyrics, rating or other metadata
- Configurable columns, sorting, and a built-in lyrics search
- Portable Java application, no installation required

## System requirements

- **250 MB** of free RAM
- Java 25 is bundled inside the release archives (no separate installation needed)

## Getting started

1. **Download** a release archive for your operating system from the
   [releases page](https://github.com/nrittsti/ntag/releases) — or install it via
   [Flatpak](#installation) / [AUR](#installation).
2. **Unpack** the archive (Linux/macOS: `tar.gz`, Windows: `zip` or the Inno Setup installer).
3. **Start NTag**:
   - Linux: run `ntag.sh` or launch `ntag.desktop`
   - Windows: double click `ntag.cmd` or use the Start Menu shortcut (installer build)
   - macOS: open `NTag.app`
   - Alternative: [launch from the command line](#launch-from-command-line)
4. Open a folder with the **Open Directory** button (`CTRL + O`).
5. Edit tags in the **Editor** panel on the right and press **Save** (`CTRL + S`).

Your settings and profiles are stored in your home directory:

| Platform | Location |
| -------- | -------- |
| Linux    | `~/.config/ntag/` (or `$XDG_CONFIG_HOME/ntag`) |
| Windows  | `%APPDATA%\ntag` |
| macOS    | `~/.config/ntag/` |
| Flatpak  | `~/.var/app/com.github.nrittsti.NTag/config/ntag` |

Use `-p/--portable` to keep everything in the application folder instead (see below).

## Keyboard shortcuts

| Shortcut | Action |
| -------- | ------ |
| `CTRL + O` | Open directory |
| `CTRL + S` | Save files |
| `CTRL + A` | Adjust artwork |
| `CTRL + R` | Rename files |
| `CTRL + N` | Number tracks |
| `CTRL + I` | Open settings |
| `CTRL + E` | Hide / show editor |
| `CTRL + Q` | Quit |
| `F5` | Refresh the file list (re-apply current filter) |

## Launch from command line

```
./jre/bin/java --module-path libs --add-modules=javafx.controls,javafx.fxml,javafx.swing,java.logging,jaudiotagger,java.desktop,jakarta.json -jar ntag.jar
```

Options:

```
-h --home <dir>    use a custom home (config) directory
-p --portable      use the current working directory as home
```

## Installation

Flatpak (Linux):

```
flatpak install flathub com.github.nrittsti.NTag
```

See the [Flatpak wiki page](https://github.com/nrittsti/ntag/wiki/Flatpak) for
installation, sandbox access and configuration file details.

Arch Linux (AUR):

```
yay -S ntag
```

https://aur.archlinux.org/packages/ntag

Windows installer and all other platforms:

https://github.com/nrittsti/ntag/releases

## Available release archives

```
   Linux:   ntag-<ver>-linux_bin.tar.gz
   Windows: ntag-<ver>-win_bin.zip (portable) and ntag-<ver>-win_setup.exe (Inno Setup installer with Start Menu shortcuts)
   macOS:   ntag-<ver>-macos_bin.tar.gz (contains a self-contained NTag.app bundle)
```

## Linux Wayland support

Java 25 LTS supports Wayland natively.
If you encounter issues, NTag works with xorg-xwayland as a compatibility layer.
https://wiki.archlinux.org/title/Wayland#Xwayland

## Linux HiDPI settings

Windows and macOS take care of the scaling automatically.
Linux Java applications using JavaFX can be scaled by defining the
`glass.gtk.uiScale` VM property when invoking java. The value can be an integer
percentage value, an integer DPI value (where 96dpi represents a scale factor of
100%, and for example 192dpi represents a scale factor of 200%), or a float value.

You can simply use the following configuration file

```
~/.config/ntag/hidpi.config
```

... and configure HiDPI scaling in percent or dpi. For example:

```
HIDPI_SCALING=130%
HIDPI_SCALING=144dpi
```

## Distribution contents

```
   jre/                       - Java Runtime Environment
   libs/                      - Third party libraries
   *.metainfo.xml             - Appstream Flathub metadata
   history.txt                - History of NTag
   license.txt                - License information
   ntag.cmd                   - Windows Launcher
   ntag.sh                    - Linux CLI Launcher
   ntag.desktop               - Linux Desktop Launcher
   ntag.jar                   - Executable Java program
   ntag_logging.properties    - Logging and Debug settings
   readme.txt                 - This file
   tag_mapping.pdf            - Tag mapping documentation
```

## Build with Maven

```
mvn clean package
```

The build emits the release archive for the operating system you build on
(`linux-dist`, `windows-dist` or `macos-dist` Maven profile). Requires a JDK 25 toolchain.

```
mvn javafx:run    # run the desktop app directly from source
mvn test          # run the unit tests
```

## Project website

https://github.com/nrittsti/ntag/

--------------------------------------------------------------------------------
## Licence

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
## Third party libraries used by NTag

Nuvola Icon Theme
Autor:   David Vignoni
Licence: LGPL

JAudiotagger Library 3.0.1
Autor:   https://www.jthink.net/jaudiotagger/
Licence: LGPL

Open JDK 25 Autor:   https://openjdk.org/projects/jdk/25/
Licence: GPL v2 with the Classpath Exception

OpenJFX Autor:   https://github.com/openjdk/jfx/
Licence: GPL v2 with the Classpath Exception

--------------------------------------------------------------------------------
End of document
