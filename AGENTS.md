# CoffeeBreak - Guía de despliegue

## Requisitos

- Java 25 (OpenJDK)
- Apache Tomcat 11
- MySQL 8+
- Maven 3.9+ (opcional, para compilar)

## 1. Base de datos

```sql
CREATE DATABASE coffeebreak_db;
USE coffeebreak_db;
SOURCE coffeebreak_db.sql;
```

O ejecuta el script directamente:

```
mysql -u root -p < coffeebreak_db.sql
```

## 2. Configurar conexión a BD

> **Nota:** `src/conexion/ConexionBD.java` contiene credenciales reales y está en `.gitignore`.
> Usa `src/conexion/ConexionBD.java.template` como base (sin credenciales, incluido en el repo).

```bash
cp src/conexion/ConexionBD.java.template src/conexion/ConexionBD.java
```

Editar `src/conexion/ConexionBD.java` con tus credenciales:

| Variable   | Valor por defecto                     |
|------------|---------------------------------------|
| URL        | `jdbc:mysql://localhost:3306/coffeebreak_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| USER       | `root`                                |
| PASSWORD   | `123`                                 |

Si MySQL corre en un contenedor Docker separado, usar `host.docker.internal` en lugar de `localhost`.

## 3. Compilar

```bash
cd CoffeeBreak
mvn clean package -DskipTests
```

Esto genera `target/CoffeeBreak.war`.

Alternativa sin Maven:

```bash
mkdir -p web/WEB-INF/classes web/WEB-INF/lib
# Copiar gson-2.11.0.jar y mysql-connector-j-9.4.0.jar a web/WEB-INF/lib/
LIBS=$(echo web/WEB-INF/lib/*.jar | tr ' ' ':')
find src -name "*.java" > /tmp/sources.txt
javac -d web/WEB-INF/classes -cp "$LIBS" @/tmp/sources.txt
cd web
jar -cf ../CoffeeBreak.war .
```

## 4. Desplegar en Tomcat

Opción A — Copiar WAR:

```bash
cp target/CoffeeBreak.war /opt/tomcat/webapps/
```

Opción B — Contexto XML (desarrollo):

Crear `/opt/tomcat/conf/Catalina/localhost/CoffeeBreak.xml`:

```xml
<Context path="/CoffeeBreak" docBase="/ruta/a/CoffeeBreak/web" reloadable="true"/>
```

## 5. Configurar Tomcat para Java 25

Java 25 requiere abrir el módulo `java.time` para Gson (reflexión sobre `LocalDateTime`).

> **Nota:** `setenv.sh` contiene configuraciones locales y está en `.gitignore`.

Crear `/opt/tomcat/bin/setenv.sh`:

```bash
#!/bin/sh
CATALINA_OPTS="$CATALINA_OPTS --add-opens java.base/java.time=ALL-UNNAMED"
```

Dar permisos de ejecución:

```bash
chmod +x /opt/tomcat/bin/setenv.sh
```

## 6. Iniciar Tomcat

```bash
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk
/opt/tomcat/bin/startup.sh
```

## 7. Verificar

```
http://localhost:8080/CoffeeBreak/
http://localhost:8080/CoffeeBreak/api/productos
```

## Estructura final del proyecto

```
CoffeeBreak/
├── coffeebreak_db.sql
├── pom.xml
├── AGENTS.md
├── src/
│   ├── beans/          # JavaBeans (6 clases)
│   ├── dao/            # DAOs (6 clases)
│   ├── conexion/
│   │   ├── ConexionBD.java          # ⚠ Ignorado (credenciales reales)
│   │   └── ConexionBD.java.template # Plantilla sin credenciales
│   ├── controller/     # API REST (4 controllers)
│   ├── service/        # Lógica de negocio (4 services)
│   └── utils/          # PasswordUtils, Validaciones, JsonUtils
└── web/
    ├── WEB-INF/
    │   ├── web.xml
    │   ├── lib/        # Dependencias JAR
    │   └── classes/    # .class compilados
    ├── css/styles.css
    ├── js/             # 7 archivos JS
    ├── index.html
    ├── menu.html
    ├── carrito.html
    ├── pedido.html
    ├── historial.html
    ├── login.html
    ├── registro.html
    ├── 404.html
    └── error.html
```
