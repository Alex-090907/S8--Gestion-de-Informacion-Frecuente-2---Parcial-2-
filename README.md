# S8- Gestion de Informacion (Frecuente 2 - Parcial 2)
Este programa está desarrollado en Java, orientado a objetos, y se conecta a una base de datos. Permite gestionar el ingreso y salida de vehículos en un parqueadero, controlando el espacio 
disponible y calculando el cobro según el tipo de vehículo.

# Estructura del proyecto

```
src
├── (default package)
   ├── Conexion.java
   ├── FrmParqueadero.java
   ├── ParqueaderoDAO.java
   └── Vehiculo.java

```
# Clase Conexion.java: Conexión con la base de datos

La clase `Conexion` se encarga de establecer la conexión entre el sistema desarrollado en Java y la base de datos.

Permite que las demás clases puedan realizar operaciones de consulta, registro y actualización de información en la base de datos.

# Clase Vehiculo.java: Modelo del vehículo

La clase `Vehiculo` representa la información de cada vehículo registrado en el sistema.

Contiene atributos relacionados con:

- Placa
- Tipo de vehículo
- Propietario
- Edad del propietario

Esta clase permite organizar la información del vehículo mediante programación orientada a objetos.

# Clase ParqueaderoDAO.java: Lógica del sistema

La clase `ParqueaderoDAO` contiene las principales operaciones relacionadas con la gestión del parqueadero.

Entre sus funciones se encuentran:

- Registrar el ingreso de vehículos.
- Registrar la salida de vehículos.
- Consultar los vehículos registrados.
- Consultar los espacios disponibles.
- Controlar la disponibilidad de espacios.
- Calcular el valor a pagar.
- Aplicar el descuento correspondiente a personas mayores de 60 años.
- Actualizar la información del parqueadero en la base de datos.

# Clase FrmParqueadero.java: Interfaz gráfica

La clase `FrmParqueadero` contiene la interfaz gráfica del sistema y permite al usuario interactuar con las diferentes funciones del parqueadero.

La interfaz cuenta con:

- Campo para ingresar la placa.
- Selección del tipo de vehículo.
- Campo para ingresar el propietario.
- Campo para ingresar la edad.
- Botón para guardar el ingreso.
- Botón para registrar la salida.
- Tabla con el historial de vehículos.
- Contador de espacios disponibles.

La cantidad de espacios libres se actualiza conforme se registran los ingresos y salidas de vehículos.

# Base de datos

El sistema utiliza una base de datos para almacenar y administrar la información del parqueadero.

La base de datos contiene las siguientes tablas principales:

- tarifas: almacena las tarifas correspondientes a cada tipo de vehículo.
- espacios: permite controlar los espacios disponibles del parqueadero.
- registros: almacena la información de los vehículos que ingresan y salen.
# Funcionamiento del sistema
**Registro de ingreso**

El usuario ingresa la placa, selecciona el tipo de vehículo, escribe el nombre del propietario y registra su edad.

Al seleccionar **"Guardar Ingreso"**, el sistema verifica que existan espacios disponibles. Si existe un espacio libre, registra el vehículo y disminuye automáticamente la cantidad de espacios disponibles.

**Control de espacios**

El sistema verifica constantemente la disponibilidad del parqueadero.

Cuando no existen espacios disponibles, se impide el ingreso de nuevos vehículos y se muestra un mensaje de advertencia al usuario.

**Registro de salida**

Para registrar la salida, el usuario ingresa la placa del vehículo y selecciona "Registrar Salida".

El sistema calcula automáticamente:

- El tiempo de permanencia.
- La tarifa correspondiente al tipo de vehículo.
- El valor total a pagar.
- El descuento del 20% para propietarios mayores de 60 años.

Después de registrar la salida, el espacio vuelve a estar disponible para otro vehículo.

# Reglas principales del sistema
- Cada vehículo debe registrarse mediante su placa.
- El ingreso depende de la disponibilidad de espacios.
- La tarifa se determina según el tipo de vehículo.
- El valor a pagar se calcula al registrar la salida.
- Los propietarios mayores de 60 años reciben un descuento del 20%.
- Al registrar una salida, el espacio ocupado vuelve a quedar disponible.
- La información se almacena en la base de datos MySQL.

# Ejecución

El proyecto puede ejecutarse desde Eclipse mediante la clase:

`FrmParqueadero.java`

Antes de ejecutar el sistema, es necesario tener disponible la base de datos MySQL y configurar correctamente los datos de conexión en:

`Conexion.java`

# Video explicativo

https://youtu.be/OxpuAnkrwnk

Autor

Alexis Vera — Ingeniería de Software, Universidad de los Andes (Uniandes)
