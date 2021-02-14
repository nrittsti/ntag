; C:\Program Files (x86)\NSIS\makensis.exe ntag.nsi
; setup.exe /S

;----------------------------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;----------------------------------------------------
;General

  !define APP_NAME "NTag"
  !define APP_VERSION "XXX"

  Name "${APP_NAME} ${APP_VERSION}"
  OutFile "${APP_NAME}-${APP_VERSION}-win-setup.exe"
  Unicode True  

  ;Default installation folder
  InstallDir "$PROGRAMFILES\${APP_NAME}"

  ;Get installation folder from registry if available
  InstallDirRegKey HKLM "Software\${APP_NAME}" ""

  ;Request application privileges
  RequestExecutionLevel admin

  CRCCheck On

;--------------------------------
;Variables

  Var StartMenuFolder

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------

;Pages
  !insertmacro MUI_PAGE_LICENSE "licence.txt"
  !insertmacro MUI_PAGE_DIRECTORY
  
  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKU" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\${APP_NAME}" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  
  !insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder
  
  !insertmacro MUI_PAGE_INSTFILES
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section

  SetOutPath "$INSTDIR"
  
  file "ntag.cmd"
  file "ntag.ico"
  file "ntag_logging.properties"
  file "licence.txt"
  file "readme.txt"
  file "history.txt"
  file "ntag.jar"
  file /r "lib"
  file /r "jre"
  
  ;Store installation folder
  WriteRegStr HKLM "Software\${APP_NAME}" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
    CreateShortcut "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\NTag.lnk" "$INSTDIR\ntag.cmd" "" "$INSTDIR\ntag.ico"
  
  !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd
 
;--------------------------------
;Uninstaller Section

Section "Uninstall"

  Delete "$INSTDIR\ntag.cmd"
  Delete "$INSTDIR\ntag.ico"
  Delete "$INSTDIR\ntag_logging.properties"
  Delete "$INSTDIR\licence.txt"
  Delete "$INSTDIR\readme.txt"
  Delete "$INSTDIR\history.txt"
  Delete "$INSTDIR\ntag.jar"
  Delete "$INSTDIR\Uninstall.exe"
  RMDir /r "$INSTDIR\lib" 
  RMDir /r "$INSTDIR\jre"
  RMDir "$INSTDIR"
  
  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder
    
  Delete "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk"
  RMDir "$SMPROGRAMS\$StartMenuFolder"
  
  DeleteRegKey /ifempty HKLM "Software\${APP_NAME}"

SectionEnd