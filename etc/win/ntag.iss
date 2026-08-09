;   This file is part of NTag (audio file tag editor).
;
;   NTag is free software: you can redistribute it and/or modify
;   it under the terms of the GNU General Public License as published by
;   the Free Software Foundation, either version 3 of the License, or
;   (at your option) any later version.
;
;   NTag is distributed in the hope that it will be useful,
;   but WITHOUT ANY WARRANTY; without even the implied warranty of
;   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;   GNU General Public License for more details.
;
;   You should have received a copy of the GNU General Public License
;   along with NTag.  If not, see <http://www.gnu.org/licenses/>.
;
;   Copyright 2026, Nico Rittstieg
;
;   Build: ISCC.exe ntag.iss /DAppVersion=1.2.17 /DSourceDir=target\ntag-1.2.17-win_bin /DOutputDir=target

[Setup]
AppId={{CC6C02CD-E55A-49BB-9F87-5257801BF06A}
AppName=NTag
AppVersion={#AppVersion}
AppPublisher=Nico Rittstieg
UninstallDisplayName=NTag
AppPublisherURL=https://github.com/nrittsti/ntag
AppSupportURL=https://github.com/nrittsti/ntag/issues
DefaultDirName={autopf}\NTag
DefaultGroupName=NTag
DisableProgramGroupPage=yes
PrivilegesRequired=admin
OutputBaseFilename=ntag-{#AppVersion}-win_setup
OutputDir={#OutputDir}
SetupIconFile={#SourceDir}\ntag.ico
UninstallDisplayIcon={app}\ntag.ico
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
Compression=lzma2
SolidCompression=yes
WizardStyle=modern

[Files]
Source: "{#SourceDir}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\NTag"; Filename: "{app}\ntag.cmd"; WorkingDir: "{app}"; IconFilename: "{app}\ntag.ico"; Comment: "NTag audio file tag editor"
Name: "{group}\Uninstall NTag"; Filename: "{uninstallexe}"; IconFilename: "{app}\ntag.ico"
