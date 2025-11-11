# Configuración

Es importante que las clases de configuración estén en un paquete diferente al resto de clases de la aplicación, por
ejemplo, en el paquete `config` o `configuration`.
Y en el primer nivel de dicho paquete y que el resto de las clases
estén en niveles iguales o inferiores para que puedan ver las clases de configuración en el Contexto de Spring.
Si no los test pueden no encontrarlas y dar errores.