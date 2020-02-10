echo "test add"




@echo off 
SETLOCAL
rem software to be used as part of source/version control
set SOFTWARE="C:\Program Files\Git\bin\bash.exe"
rem ==================================================================
rem =============== User defined parameters ==========================
rem ==================================================================  
rem IF CLEARCASE IS NOT IN THE DEFAULT PATH ADD IT HERE set PATH=[Path_to_clearcase];%PATH%
rem set CLEARCASE_URL=https://prdccrc.wellpoint.com:16443/ccrc
rem set CLEARCASE_REPOS=/vob/SalesApplications_src/Projects/Siebel/OnePointSiebel/rep obj/
set SRC_USR=%USERNAME%
set SRC_PSWD=
set SET_REMOTE_URL=call %SOFTWARE% --login -i -c "git remote set-url origin https://%SRC_USR%@bitbucket.anthem.com/scm/siebel_crm/siebel_crm.git"
set UPDATE=call %SOFTWARE% --login -i -c "GIT_SSL_NO_VERIFY=true git pull origin develop"
set ADD=call %SOFTWARE% --login -i -c "git add ."
set COMMIT=call %SOFTWARE% --login -i -c "git commit -m
set PUSH=call %SOFTWARE% --login -i -c "GIT_SSL_NO_VERIFY=true git push -u origin develop"
rem ==================================================================
rem Please Change ONLY "LOGFILE" & "WORKING_DIR" parameter values based on your local set-up of ClearCase
rem ================================================================== 
set LOGFILE=C:\Siebel\GIT\siebel_git_integration.log
set WORKING_DIR=C:\Siebel\GIT\Siebel_CRM\Repository_Objects
rem ================================================================== 
rem LOGFILE SHOULD NOT HAVE SPACES IN ITS PATH 
rem AND WATCH OUT FOR TRAILING SPACES ON THE SET COMMANDS! 
rem ================================================================== 
rem ==================================================================
set COMMENT=
set FILE=
echo ===========================START============================ >> %LOGFILE%   
echo %DATE% %TIME% >> %LOGFILE%   
echo ARGS: >> %LOGFILE%   
echo %* >> %LOGFILE%
echo ============================================================ >> %LOGFILE%   
echo PARSING.. >> %LOGFILE%   
set ACTION=%1
shift 
set DIR=%1
shift
set COMMENT=%COMMENT% %1
set COMMENT_FILE=%1
shift
set FILE=%1
SET DIR_NO_QUOTE=###%DIR%###
SET DIR_NO_QUOTE=%DIR_NO_QUOTE:"###=%
SET DIR_NO_QUOTE=%DIR_NO_QUOTE:###"=%
SET DIR_NO_QUOTE=%DIR_NO_QUOTE:###=%

echo Change local directory to "%WORKING_DIR%" >> %LOGFILE%
chdir /d "%WORKING_DIR%" >> %LOGFILE% 2>&1

echo current directory is >> %LOGFILE% 2>&1
cd >> %LOGFILE% 2>&1

if errorlevel 100 goto END >> %LOGFILE% 2>&1 
 
echo ACTION: %ACTION% >> %LOGFILE%   
echo DIR: %DIR% >> %LOGFILE%   
echo COMMENT_FILE: %COMMENT_FILE% >> %LOGFILE%   
echo FILE: %FILE% >> %LOGFILE%   
echo DIR_NO_QUOTE: %DIR_NO_QUOTE% >> %LOGFILE%   
echo ============================================================ >> %LOGFILE%
if %ACTION%==checkout goto CHECK_OUT
if %ACTION%==checkin goto CHECK_IN

:CHECK_OUT 
echo =====================REMOTE_URL_UPDATE====================== >> %LOGFILE%  
echo %SET_REMOTE_URL% >> %LOGFILE%
%SET_REMOTE_URL%
echo =========================GIT_UPDATE=========================== >> %LOGFILE%
echo update git local repository i.e. %WORKING_DIR% for file: %FILE% from Source Control System >> %LOGFILE%

if errorlevel 100 goto END >> %LOGFILE%
echo %UPDATE% >> %LOGFILE%
%UPDATE% >> %LOGFILE% 2>&1
if errorlevel 100 goto END >> %LOGFILE%
echo ======================GIT_UPDATE DONE========================= >> %LOGFILE%   
goto END  
 
:CHECK_IN 
echo =========================GIT_PUSH============================ >> %LOGFILE%   
echo Add in file: %FILE% into BitBucket Source Control System >> %LOGFILE%   
echo Change local directory to "%WORKING_DIR%" >> %LOGFILE% 
chdir "%WORKING_DIR%" >> %LOGFILE% 2>&1

echo ***** COMMENT_FILE ***** >> %LOGFILE%
SET /p COMMENT=<%COMMENT_FILE%
SET COMMENT=%COMMENT:~3%
SET COMMENT='%COMMENT%'
echo %COMMENT% >> %LOGFILE% 2>&1 
echo ************************ >> %LOGFILE%

echo Copying %DIR_NO_QUOTE%%FILE% to local Working Copy Folder: %WORKING_DIR% >> %LOGFILE% 
copy "%DIR_NO_QUOTE%%FILE%" "%WORKING_DIR%\%FILE%" >> %LOGFILE% 2>&1   
echo Add %FILE% into BitBucket Source Control System >> %LOGFILE% 
echo %ADD% >> %LOGFILE%
%ADD% >> %LOGFILE% 2>&1

echo committing the file in git local repository before pushing the changes to server >> %LOGFILE%
echo %COMMIT% %COMMENT%" >> %LOGFILE%
%COMMIT% %COMMENT%" >> %LOGFILE% 2>&1

echo pushing the committed changes from git local repository to BitBucket server >> %LOGFILE%
echo %PUSH% >> %LOGFILE%
%PUSH% >> %LOGFILE% 2>&1

goto END   

echo =======================GIT_PUSH DONE========================= >> %LOGFILE%   
goto END 
 
:END
echo %DATE% %TIME% >> %LOGFILE%   
echo ============================END============================= >> %LOGFILE%
ENDLOCAL


