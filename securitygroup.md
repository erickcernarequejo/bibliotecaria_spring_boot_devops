+-----------------------------------+
|     ARQUITECTURA DE SEGURIDAD    |
|    GRUPOS DE SEGURIDAD (SG)      |
+-----------------------------------+

+------------------------+
| SG-App-Bibliotecaria  |
+------------------------+
RECURSOS:
- Instancia EC2 con Spring Boot
- Subnet Privada

REGLAS DE ENTRADA:
✅ Puerto 8080
- Origen:
    * IPs corporativas
    * VPC CIDR
    * Balanceador de carga (opcional)
      ✅ Puerto 22 (SSH)
- Origen:
    * IPs administración
    * SG-Bastion-Bibliotecaria

REGLAS DE SALIDA:
✅ Puerto 5432 (PostgreSQL)
- Destino: SG-RDS-Bibliotecaria
  ✅ HTTPS (443)
- Para actualizaciones
  ✅ DNS (53)

+------------------------+
| SG-RDS-Bibliotecaria  |
+------------------------+
RECURSOS:
- Instancia RDS PostgreSQL
- Subnet Privada

REGLAS DE ENTRADA:
✅ Puerto 5432 (PostgreSQL)
- Origen:
    * SG-App-Bibliotecaria
    * SG-CodeBuild-Bibliotecaria
    * SG-Bastion-Bibliotecaria

REGLAS DE SALIDA:
❌ Sin salidas necesarias

+---------------------------+
| SG-CodeBuild-Bibliotecaria|
+---------------------------+
RECURSOS:
- Proyecto CodeBuild
- Subnet Privada

REGLAS DE SALIDA:
✅ Puerto 5432 (PostgreSQL)
- Destino: SG-RDS-Bibliotecaria
  ✅ HTTPS (443)
- Descargas dependencias
  ✅ DNS (53)
  ✅ Acceso repositorios código

+------------------------------+
| SG-Bastion-Bibliotecaria    |
+------------------------------+
RECURSOS:
- Instancia EC2 Bastion
- Subnet Pública

REGLAS DE ENTRADA:
✅ Puerto 22 (SSH)
- Origen:
    * IPs administración
    * VPN corporativa

REGLAS DE SALIDA:
✅ Puerto 5432 (PostgreSQL)
- Destino: SG-RDS-Bibliotecaria
  ✅ SSH otras instancias
  ✅ DNS
  ✅ Actualizaciones sistema

+-----------------------------------+
|     FLUJOS DE CONEXIÓN           |
+-----------------------------------+
🔒 App → RDS: Permitido (5432)
🔒 CodeBuild → RDS: Permitido (5432)
🔒 Bastion → RDS: Permitido (5432)
🔒 Internet → App: Controlado (8080)
🔒 Admin → Bastion: Controlado (22)

PRINCIPIOS:
- Mínimo privilegio
- Segmentación de red
- Control de acceso granular