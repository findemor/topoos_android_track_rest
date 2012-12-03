topoos_android_track_rest
=========================

Proyecto Android de demostración de registro de un track en topoos, empleando directamente el API Rest.

----------

topoos es una plataforma de servicios basados en localización especialmente diseñada para facilitar la creación de aplicaciones móviles dependientes de contexto

----------

DESCRIPCIÓN DEL PROYECTO:
El proyecto muestra la pantalla de identificación de usuarios (login), donde identificara al usuario mediante OAuth 2.0 client-flow (o flujo implicito) y almacena el token del API en las preferencias.
Despues, en la actividad PanelActiviy, el usuario puede iniciar el chip GPS.
Cuando el GPS detecta cambios en la ubicacion del dispositivo, creara un Track, y en siguientes actualizaciones registrara posiciones en el track. Cuando se pulsa el boton de finalizar track, se registra una posicion de cierre, segun se especifica en la documentacion de topoos.

Una caracteristica principal del proyecto es que no hace uso del SDK, sino que actua directamente contra el API Rest.

ATENCIÓN: 
Antes de poder utilizar la aplicación, es necesario obtener un CLIENT_ID de topoos y configurarlo en el fichero AppConfiguration.java

OJO: 
Este proyecto de demo no controla el ciclo de vida de las actividades para facilitar su lectura.

----------

Para más información www.topoos.com