# GugleCar
Proyecto de coches autoconducidos en un entorno virtual, para la comunicación de los distintos agentes se ha usado la plataforma Magentix. Este proyecto no puede ejecutarse sin el correspondiente servidor que haga de sensores para los agentes.

Durante la ejecución se situan cuatro coches de forma aleatoria en los bordes del mapa y deben llegar el máximo número de vehículos, con recursos limitados, a las zonas destacadas en rojo o metas. Si algún vehículo se choca o se queda sin recursos se le considera que ha tenido un accidente.

Existen varios tipos de vehículos con diferentes carácterísticas. Unos con mayor rango en el sensor de obstaculos u otros como la capacidad de sobrevolar edificios(drones)

Para el control de todos aspectos se ha diseñado un agente controlador que gestione el tráfico en todo el mapa y gestiona que vehículos pueden repostar.

----------

## Traza de ejecución en un mapa de cuatro vehículos
![alt text][trace]

[trace]: https://raw.githubusercontent.com/Jestern/GugleCar/master/Documentacion/Mapas/mapa8.PNG "Ruta que han seguido los vehículos hasta llegar a las metas."
