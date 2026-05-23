@echo off
title Apagando Servidor Ometeotl
echo Cerrando las conexiones y apagando el sistema de compras...
taskkill /F /IM javaw.exe
echo.
echo Servidor apagado correctamente. Ya puede cerrar la computadora.
timeout /t 10 >nul
exit