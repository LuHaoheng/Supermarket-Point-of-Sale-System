@echo off
cd /d "%~dp0"
if not exist build\classes mkdir build\classes
(for /r src %%f in (*.java) do @echo "%%f" & for /r test %%f in (*.java) do @echo "%%f") > build\sources.txt
javac -encoding UTF-8 -d build\classes @build\sources.txt
if errorlevel 1 exit /b 1
java -cp build\classes pos.PosServiceTest
