@ECHO OFF

cd ..
set DIST_DIR="%CD%"
echo %DIST_DIR%
cd bin_windows

FOR /F "tokens=1* delims=REG_SZ " %%A IN ('REG QUERY HKLM\System\CurrentControlSet\Services\Tcpip\Parameters /v Domain') DO (
SET CURR_DOMAIN=%%B
)

set JAVAW=..\\..\\jre\\bin\\javaw.exe
if not exist %JAVAW% set JAVAW=javaw
wrapper.exe --name="Codeine" --mode=Main --install %JAVAW% -DcodeinePeerPort=49671 -DinstallDir=%DIST_DIR% -Xdebug -DDNS_DOMAIN_NAME=%CURR_DOMAIN% -Xrunjdwp:transport=dt_socket,server=y,suspend=n -Xmx100M -cp ..\bin\codeine.jar codeine.CodeinePeerBootstrap
