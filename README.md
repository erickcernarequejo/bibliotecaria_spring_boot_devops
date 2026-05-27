# Bibliotecaria con Spring Boot y DevOps CI/CD

Solución de backend para gestionar libros y autores diseñada y desarrollada con Java 21, Maven y una base de datos PostgreSQL. La aplicación ofrece opciones básicas de listado, registro, eliminación y filtrado por identificador de autores.
Todo el proceso de desarrollo, integración y despliegue de la aplicación se llevó a cabo con prácticas de Integración Continua y Despliegue Continuo (CI/CD) utilizando servicios de AWS y GitHub. Desde el control de versiones con GitHub, la construcción automatizada con AWS CodeBuild, hasta la implementación sin interrupciones con AWS CodeDeploy y la automatización del flujo de trabajo con AWS CodePipeline, la aplicación se desarrolla y despliega de manera eficiente y confiable en instancias EC2, garantizando una experiencia sin problemas para los usuarios.

## Descripción Flujo CI/CD

A continuación se detalla los pasos realizados desde el almacenamiento en el repositorio hasta el despliegue en instancia EC2.

### [Administración de identidades | IAM ](https://aws.amazon.com/es/iam/?trk=3cfe03ed-c5b4-45aa-b6e1-27fc8d2b4f35&sc_channel=ps&s_kwcid=AL!4422!10!71468491682381!71469018664105&ef_id=6e1e4aae96561686e3f62f60dca8e66a:G:s)

1. Crear el rol **EC2InstanceRoleBibliotecariaDevops** con las políticas correspondientes de AWS para permitir el uso del agente CloudWatch, agente CodeDeploy y System Manager

    1. En la opción roles de la consola AWS seleccionar "Create role".

    2. En la opción Trusted entity type seleccionar "AWS Service" y en Use Case seleccionar "EC2".

    3. En la opción Permissions policies buscar y seleccionar las políticas:

        * **AmazonSSMManagedInstanceCore**, política que permite a SSM ejecutar comandos de forma remota, aplicar parches y configurar software en instancias EC2.

        * **AmazonEC2RoleforAWSCodeDeploy**, política que permite a AWS CodeDeploy interactuar con instancias EC2 durante las implementaciones.

        * **CloudWatchAgentServerPolicy**, política que permite al agente de Amazon CloudWatch recopilar y enviar de forma segura métricas y registros a CloudWatch en su nombre.

    4. En la opción nombre del rol ingresar "EC2InstanceRoleBibliotecariaDevops", verificar que los cambios sean correctos y crear rol.
   

2. Crear el rol **CodeDeployRoleBibliotecariaDevops** para ejecutar CodeDeploy

    1. En la opción roles de la consola AWS seleccionar "Create role".
    2. En la opción Trusted entity type seleccionar "AWS Service" y en Use Case seleccionar "CodeDeploy".
    3. En la opción Permissions policies buscar seleccionar las políticas AWSCodeDeployRole y EC2ReadParameterStore.
    5. En la opción nombre del rol ingresar "CodeDeployRoleBibliotecariaDevops", verificar que los cambios sean correctos y crear rol.

3. Crear el rol **CodeBuildRoleBibliotecariaDevops** para ejecutar CodeBuild

   1. En la opción roles de la consola AWS seleccionar "Create role".
   2. En la opción Trusted entity type seleccionar "AWS Service" y en Use Case seleccionar "CodeBuild".
   3. En la opción Permissions policies verificar y seleccionar la política AmazonSSMFullAccess, CodeBuildBasePolicy, CodeBuildCachePolicy, CodeBuildConectionPolicy y CodeBuildVpcPolicy
   5. En la opción nombre del rol ingresar "CodeBuildRoleBibliotecariaDevops", verificar que los cambios sean correctos y crear rol.

### [AWS VPC](https://aws.amazon.com/es/vpc/)

1. Ingresar al panel VPC y seleccionar la opción Crear VPC
2. En la configuración de VPC elegir "VPC and more" y colocar el nombre BibliotecariaDevops
3. En el bloque CIDR, IPV6 y Tenancy mantener los valores por defecto.
4. Elegir 2 zonas de disponibilidad (us-east-1a y us-east-1b)
3. Elegir 2 sub redes públicas y 2 sub redes privadas
4. Elegir NAT Gateway en 1 zona de disponibilidad.
5. En VPC endpoint elegir None

**Grupo de seguridad**

1. Crear Grupo de seguridad con el nombre SG-App-Bibliotecaria y en reglas de entradas incluir.

   ```yaml
      CUSTOM TCP ==> 8080 ==> 0.0.0.0/0
      ```
   en reglas de salida colocar:
   ```yaml
      All Traffic ==> All ==> 0.0.0.0/0
      ```

2. Crear Grupo de seguridad con el nombre SG-RDS-Bibliotecaria y en reglas de entradas incluir.

   ```yaml
      PostgreSQL ==> 5432 ==> SG-App-Bibliotecaria
      PostgreSQL ==> 5432 ==> SG-CodeBuild-Bibliotecaria
      PostgreSQL ==> 5432 ==> SG-Bastion-Bibliotecaria
      ```
   en reglas de salida no colocar nada

3. Crear Grupo de seguridad con el nombre SG-Bastion-Bibliotecaria y en reglas de salida incluir.

   ```yaml
      SSH ==> 22 ==> 0.0.0.0/0
      ```
   en reglas de salida colocar:
   ```yaml
      PostgreSQL ==> 5432 ==> SG-RDS-Bibliotecaria
      ```

4. Crear Grupo de seguridad con el nombre SG-CodeBuild-Bibliotecaria y en reglas de salida incluir.

   ```yaml
      PostgreSQL ==> 5432 ==> SG-RDS-Bibliotecaria
      HTTPS ==> 443 ==> 0.0.0.0/0
      ```
   
5. Crear Grupo de seguridad con el nombre SG-rds y en reglas de entradas incluir.

   ```yaml
      PostgreSQL ==> 5432 ==> SG-codebuild
      ```
   Opcional en reglas de salida colocar:
   ```yaml
      All Traffic ==> All ==> 0.0.0.0/0
      ```

### [AWS RDS](https://aws.amazon.com/es/rds/)

1. Ingresar a la consola de amazon RDS y seleccionar la opción "Create database"

2. Elegir método de creación de base de datos estándar

3. En la sección "Engine options" seleccionar PostgreSQL

4. En la sección "Templates" seleccionar la opción "Free tier".

5. En la sección "Settings" colocar como nombre identificador de instancia "db-bibliotecaria".

6. En la sección "Credentials Settings" mantener nombre de usuario "postgres" y asignar una contraseña. admin123

7. En la sección "Storage" deshabilitar el escalado automático.

8. En la sección "Connectivity" marcar la opción "No" en acceso al público, elegir el grupo de seguridad "SG-Bibliotecaria-RDS-Devops" y "SG-rds", en Availability Zone elegir "us-east-1a" y dejar demás valores por defecto.

9. En la sección "Additional configuration" colocar como nombre "bibliotecaria_db" y proceder a seleccionar **Create database**
   
### [EC2 ](https://docs.aws.amazon.com/es_es/ec2/)

Crear instancia MyDbToBastionHost y asignar al grupo de seguridad (ec2-rds-1)

1. Ingresar a la consola EC2, seleccionar "Launch an instance" y asignar a la instancia el nombre  "**BibliotecariaDevops**"

2. En la opción Imágenes de aplicaciones y sistemas operativos seleccionar Amazon Linux y AMI de Amazon Linux 2023, apto para la capa gratuita.

3. En la opción tipo de instancia seleccionar t2.micro, apto para la capa gratuita.

4. En la opción par de claves (inicio de sesión), para este laboratorio, basta con elegir continuar sin un par de claves.

5. En la opción configuraciones de red:

   1. Seleccionar la VPC BibliotecariaDevops-VPC y elegir subnet us-east-1a.
   2. Asignar automáticamente la IP pública y elegir habilitar.
   2. Seleccionar el grupo de seguridad con el nombre **SG-App-Bibliotecaria**.

6. En la opción detalles avanzados, seleccionar como perfil de instancia IAM el rol que se creó "EC2InstanceRoleBibliotecariaDevops"

### [AWS S3](https://aws.amazon.com/es/s3/)

1. Ingresar a la consola de amazon S3 y seleccionar la opción "Create Bucket"

2. Ingresar el nombre del bucket "bibliotecaria-devops-build-cache"

3. En la sección "Object Ownership" seleccionar el valor recomendado "ACLs disabled"

4. En la sección "Block Public Access settings for this bucket" seleccionar la opción "Block all public access".

5. En todas las demás secciones de creación del bucket mantener los valores por defecto.

### [AWS CodeBuild](https://aws.amazon.com/es/codebuild/)

1. En la opción Build Project de la consola de AWS hacer clic en la opción "Create Project"

2. Ingresar el nombre del proyecto "Bibliotecaria-devops-build"

3. En source seleccionar "GitHub", establecer conexión y elegir el nombre del repositorio donde se encuentra almacenado nuestro proyecto.

4. En Environment realizar lo siguiente:
   * Seleccionar el sistema operativo Linux, 
   * Seleccionar runtime Standard 
   * Seleccionar imagen aws/codebuild/amazonlinux2-x86-64-standard:5.0.
   * Seleccionar el rol CodeBuildRoleBibliotecariaDevops
   * En configuración adicional elegir la VPC BibliotecariaDevops-vpc
   * En subnet elegir us-east-1a
   * En Security groups elegir SG-codebuild

5. En Buildspec seleccionar la opción "Use a buildspec file" y colocar el nombre buildspec.yml que es el archivo que se encuentra dentro del repositorio y se desea que se ejecute las instrucciones que se detallan dentro.
6. En Artifacts "Additional configuration" seleccionar Cahe Type S3 y elegir el nombre "bibliotecaria-devops-build-cache" del bucket creado en el paso anterior.
7. En Logs dejar la opción CloudWatch logs - optional seleccionada.
8. Proceder a seleccionar la opción "Create build project"

### [Amazon CodeDeploy](https://aws.amazon.com/es/codedeploy/)

1. Seleccionar la opción Applications en el menú de opciones lateral de la consola de AWS.

2. Seleccionar la opción Create Application e ingresar el nombre "Bibliotecaria-devops-deploy" y en la opción Compute platform seleccionar EC2/On-premises.

3. Seleccionar la aplicación que se ha creado con el nombre Bibliotecaria-devops-deploy y dentro seleccionar la opción Create deployment group.

4. En la opción Deployment group name ingresar "Bibliotecaria-devops-deploy-group".

5. En la opción Service role seleccionar el rol creado "CodeDeployRoleBibliotecariaDevops".

6. En la opción Deployment type dejar el valor por defecto en "In-place".

7. En la opción Environment configuration seleccionar "Amazon EC2 instances" y agregar el tag group con los valores

   ```
   Key: Name
   Value: BibliotecariaDevops
   ```

8. En la opción Deployment settings seleccionar "CodeDeployDefault.OneAtATime"

9. En la opción Load balancer desmarcar la opción "Enable load balancing" ya que sólo se ejecutará una instancia.

### [AWS CodePipeline](https://aws.amazon.com/es/codepipeline/)

1. Seleccionar la opción Create pipeline en la consola de AWS.
2. Seleccionar la opción Crear canalización e ingresar el nombre "Bibliotecaria-devops-pipeline" y en create options seleccionar "Build custom pipeline".
3. En la opción Rol del servicio seleccionar "Nuevo rol de servicio" y otorgar un nombre o dejar el que se genera por defecto.
4. En la etapa de Origen seleccionar como proveedor "GitHub App" y establecer conexión de la cuenta AWS a GitHub seleccionando "bibliotecaria-conexion", elegir el repositorio bibliotecaria_spring_boot_devops y la rama **main** con todo lo demás por defecto.
5. En la etapa de compilación seleccionar como proveedor de compilación "AWS CodeBuild", la región en la que creamos y seleccionar el nombre "Bibliotecaria-devops-build" .
6. En la etapa de implementación seleccionar "AWS CodeDeploy", la región en la que fue creada, el nombre "bibliotecaria-devops-deploy" y el grupo de implementación "Bibliotecaria-devops-deploy-group".
7. En la última etapa revisamos todos los detalles y se procede a crear la canalización.

Cambio de usuario:
Si por alguna razón inicias sesión con un usuario diferente y necesitas cambiar a ec2-user
sudo su - ec2-user

cat /opt/codedeploy-agent/deployment-root/deployment-logs/codedeploy-agent-deployments.log

**Crear Política AWS**

1. Crear política ***CodeBuildBasePolicy*** para otorgar permisos a los servicios CodeBuild, CloudWatch Logs y S3.

   ```json
   {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Resource": [
                "arn:aws:logs:*:*:log-group:/aws/codebuild/*",
                "arn:aws:logs:*:*:log-group:/aws/codebuild/*:*"
            ],
            "Action": [
                "logs:CreateLogGroup",
                "logs:CreateLogStream",
                "logs:PutLogEvents"
            ]
        },
        {
            "Effect": "Allow",
            "Resource": [
                "arn:aws:s3:::codepipeline-*-*"
            ],
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:GetObjectVersion",
                "s3:GetBucketAcl",
                "s3:GetBucketLocation"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "codebuild:CreateReportGroup",
                "codebuild:CreateReport",
                "codebuild:UpdateReport",
                "codebuild:BatchPutTestCases",
                "codebuild:BatchPutCodeCoverages"
            ],
            "Resource": [
                "arn:aws:codebuild:*:*:report-group/*"
            ]
        }
    ]
   }
   ```

2. Crear política ***CodeBuildCachePolicy*** para otorgar permisos a los servicios CodeBuild, CloudWatch Logs y S3.

   ```json
   {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:GetBucketAcl",
                "s3:GetBucketLocation"
            ],
            "Resource": [
                "arn:aws:s3:::*-devops-build-cache",
                "arn:aws:s3:::*-devops-build-cache/*"
            ]
        }
    ]
   }
   ```

3. Crear política ***CodeBuildVpcPolicy*** para otorgar permisos a los servicios CodeBuild, CloudWatch Logs y S3.

   ```json
   {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ec2:CreateNetworkInterface",
                "ec2:DescribeDhcpOptions",
                "ec2:DescribeNetworkInterfaces",
                "ec2:DeleteNetworkInterface",
                "ec2:DescribeSubnets",
                "ec2:DescribeSecurityGroups",
                "ec2:DescribeVpcs"
            ],
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": [
                "ec2:CreateNetworkInterfacePermission"
            ],
            "Resource": "arn:aws:ec2:*:*:network-interface/*",
            "Condition": {
                "StringEquals": {
                    "ec2:AuthorizedService": "codebuild.amazonaws.com"
                },
                "ArnLike": {
                    "ec2:Subnet": [
                        "arn:aws:ec2:*:*:subnet/*"
                    ]
                }
            }
        }
    ]
   }
   ```

4. Crear política ***EC2ReadParameterStore*** para otorgar permisos a los servicios Parameter Store.

   ```json
   {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "Statement1",
            "Effect": "Allow",
            "Action": [
                "ssm:GetParameter"
            ],
            "Resource": [
                "*"
            ]
        }
    ]
   }
   ```

4. Crear política ***CodeBuildConectionPolicy*** para otorgar permisos a los servicios Parameter Store.

   ```json
   {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": [
                "codeconnections:GetConnection",
                "codeconnections:GetConnectionToken"
            ],
            "Resource": "*"
        }
    ]
   }
   ```

### [AWS SYSTEM MANAGER](https://aws.amazon.com/es/systems-manager/)

1. Ingresar al servicio Systems Manager en el menú de servicios, busca y selecciona "Systems Manager".
2. En el panel de navegación izquierdo, expande y selecciona "State Manager".
3. Haz clic en el botón "Create association" para crear una nueva asociación con el nombre CodeDeployAgentBibliotecariaDevops.
4. Selecciona el documento de comando AWS-ConfigureAWSPackage.
3. Configura los parámetros del documento con Action "Install", Installation Type "Uninstall and reinstall" y Name "AWSCodeDeployAgent"
4. Selecciona las instancias de destino en la opción "Specify instance tags" y elegir Key: Name y Tag: BibliotecariaDevops.
5. En programación elegir "No schedule" para que se instale una vez.
>>>>>>> Stashed changes
